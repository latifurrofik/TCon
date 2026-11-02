#!/usr/bin/env python3
"""Generate one 9:16 scene image per episode scene with the Gemini image API.

Requires GEMINI_API_KEY. The API may incur charges; check current Google AI pricing
and quotas before running. Generated assets are written to public/images/episode-01.
"""
import base64
import json
import os
import time
import urllib.error
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "data" / "episode-01.json"
OUT = ROOT / "public" / "images" / "episode-01"
MODEL = os.getenv("GEMINI_IMAGE_MODEL", "gemini-3.1-flash-image")
ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/interactions"


def request_image(api_key: str, prompt: str) -> bytes:
    payload = {
        "model": MODEL,
        "input": prompt,
        "response_format": {
            "type": "image",
            "mime_type": "image/png",
            "aspect_ratio": "9:16",
            "image_size": "1K"
        }
    }
    req = urllib.request.Request(
        ENDPOINT,
        data=json.dumps(payload).encode("utf-8"),
        headers={"Content-Type": "application/json", "x-goog-api-key": api_key},
        method="POST"
    )
    try:
        with urllib.request.urlopen(req, timeout=300) as response:
            result = json.loads(response.read().decode("utf-8"))
    except urllib.error.HTTPError as exc:
        detail = exc.read().decode("utf-8", errors="replace")
        raise RuntimeError(f"Gemini API HTTP {exc.code}: {detail[:1200]}") from exc

    # Interactions API returns generated image blocks in model_output steps.
    for step in result.get("steps", []):
        if step.get("type") == "model_output":
            for block in step.get("content", []):
                if block.get("type") == "image" and block.get("data"):
                    return base64.b64decode(block["data"])
    output_image = result.get("output_image")
    if isinstance(output_image, dict) and output_image.get("data"):
        return base64.b64decode(output_image["data"])
    raise RuntimeError("Gemini response did not contain an image block: " + json.dumps(result)[:1200])


def main():
    api_key = os.getenv("GEMINI_API_KEY")
    if not api_key:
        raise SystemExit("Missing GEMINI_API_KEY. Add it as a GitHub Actions repository secret.")
    episode = json.loads(DATA.read_text(encoding="utf-8"))
    OUT.mkdir(parents=True, exist_ok=True)

    for index, scene in enumerate(episode["scenes"], start=1):
        target = OUT / f"scene-{index:02d}.png"
        if target.exists() and os.getenv("OVERWRITE_IMAGES", "false").lower() != "true":
            print(f"SKIP existing {target}")
            continue
        prompt = (
            scene["visual_prompt"]
            + " Maintain the same coherent visual language across this episode: "
              "cinematic editorial 2D illustration, deep navy and charcoal palette, "
              "restrained golden-yellow accents, dramatic soft lighting, clear focal subject, "
              "clean composition. Portrait 9:16. No typography, captions, watermark, or logo."
        )
        print(f"Generating scene {index}/{len(episode['scenes'])} with {MODEL}...")
        image_bytes = request_image(api_key, prompt)
        target.write_bytes(image_bytes)
        print(f"Saved {target} ({len(image_bytes)} bytes)")
        if index < len(episode["scenes"]):
            time.sleep(2)


if __name__ == "__main__":
    main()
