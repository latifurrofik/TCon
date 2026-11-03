#!/usr/bin/env python3
"""Generate episode scene art through the volunteer-powered AI Horde.

Uses the public anonymous key by default: no paid API key or billing account.
Anonymous jobs have the lowest queue priority and may take a long time.
"""
import base64
import json
import os
import re
import time
import urllib.error
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "data" / "episode-01.json"
OUT = ROOT / "public" / "images" / "episode-01"
API = "https://aihorde.net/api/v2"
API_KEY = os.getenv("AI_HORDE_API_KEY") or "0000000000"
MAX_WAIT = int(os.getenv("AI_HORDE_MAX_WAIT_SECONDS", "2400"))


def api_request(url, method="GET", payload=None):
    headers = {
        "apikey": API_KEY,
        "Client-Agent": "TCon-Shorts:1.0:github.com/latifurrofik/TCon",
        "Accept": "application/json",
    }
    data = None
    if payload is not None:
        headers["Content-Type"] = "application/json"
        data = json.dumps(payload).encode("utf-8")
    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=90) as response:
            raw = response.read()
            content_type = response.headers.get("Content-Type", "")
            if "application/json" in content_type or raw[:1] in (b"{", b"["):
                return json.loads(raw.decode("utf-8"))
            return raw
    except urllib.error.HTTPError as exc:
        detail = exc.read().decode("utf-8", errors="replace")
        raise RuntimeError(f"AI Horde HTTP {exc.code}: {detail[:1000]}") from exc


def generate_image(prompt, target):
    payload = {
        "prompt": prompt,
        "params": {
            "width": 512,
            "height": 896,
            "steps": 20,
            "n": 1,
            "cfg_scale": 7,
            "sampler_name": "k_euler",
            "karras": True,
        },
        "nsfw": False,
        "censor_nsfw": True,
        "r2": True,
        "replacement_filter": True,
    }
    submitted = api_request(f"{API}/generate/async", "POST", payload)
    request_id = submitted.get("id")
    if not request_id:
        raise RuntimeError("AI Horde did not return a request ID: " + json.dumps(submitted)[:1000])
    print(f"Queued AI Horde request {request_id}; initial queue position: {submitted.get('queue_position', 'unknown')}", flush=True)

    deadline = time.time() + MAX_WAIT
    while time.time() < deadline:
        status = api_request(f"{API}/generate/check/{request_id}")
        if status.get("faulted"):
            raise RuntimeError("AI Horde generation faulted: " + json.dumps(status)[:1000])
        if status.get("done"):
            break
        print(
            f"Waiting for volunteer worker: queue={status.get('queue_position', '?')}, "
            f"wait_estimate={status.get('wait_time', '?')}s",
            flush=True,
        )
        time.sleep(10)
    else:
        raise TimeoutError(
            f"AI Horde request {request_id} did not finish within {MAX_WAIT}s. "
            "Re-run the workflow; already-created images will be retained."
        )

    result = api_request(f"{API}/generate/status/{request_id}")
    generations = result.get("generations") or []
    if not generations:
        raise RuntimeError("AI Horde finished without an image: " + json.dumps(result)[:1000])
    image_value = generations[0].get("img")
    if not image_value:
        raise RuntimeError("AI Horde result did not include image data.")
    if image_value.startswith("http://") or image_value.startswith("https://"):
        image_bytes = api_request(image_value)
        if isinstance(image_bytes, dict):
            raise RuntimeError("Expected image bytes but received JSON from image URL.")
    else:
        if image_value.startswith("data:") and "," in image_value:
            image_value = image_value.split(",", 1)[1]
        image_bytes = base64.b64decode(image_value)
    if not image_bytes.startswith(b"RIFF") and not image_bytes.startswith(b"\x89PNG") and not image_bytes.startswith(b"\xff\xd8"):
        raise RuntimeError("AI Horde returned an unrecognized image format.")
    target.write_bytes(image_bytes)
    print(f"Saved {target} ({len(image_bytes)} bytes)", flush=True)


def main():
    episode = json.loads(DATA.read_text(encoding="utf-8"))
    OUT.mkdir(parents=True, exist_ok=True)
    for index, scene in enumerate(episode["scenes"], start=1):
        target = OUT / f"scene-{index:02d}.webp"
        if target.exists() and os.getenv("OVERWRITE_IMAGES", "false").lower() != "true":
            print(f"SKIP existing {target}", flush=True)
            continue
        prompt = (
            scene["visual_prompt"]
            + " Cinematic editorial 2D illustration, dark navy and charcoal palette, "
              "restrained golden-yellow accents, dramatic soft lighting, clear focal subject, "
              "clean uncluttered composition, portrait 9:16. No text, letters, captions, "
              "watermark, logo, collage, frame, or border."
        )
        print(f"Generating scene {index}/{len(episode['scenes'])} with AI Horde (free community workers)...", flush=True)
        generate_image(prompt, target)
        if index < len(episode["scenes"]):
            time.sleep(3)


if __name__ == "__main__":
    main()
