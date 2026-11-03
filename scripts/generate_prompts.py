#!/usr/bin/env python3
"""Build deterministic, reviewable image prompts from TCon episode/storyboard data.

This script does not call an image provider. It creates a prompt manifest only.
Existing output is never overwritten unless --overwrite is explicitly supplied.
"""
from __future__ import annotations

import argparse
import json
import re
from pathlib import Path
from typing import Any

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_EPISODE = ROOT / "data" / "episodes" / "episode_1" / "naskah" / "storyboard.json"
DEFAULT_OUTPUT_DIR = ROOT / "data" / "prompt-manifests"
VISUAL_BIBLE = "docs/VISUAL-PRODUCTION-BIBLE.md"
STORYBOARD_BIBLE = "docs/STORYBOARD-BIBLE.md"

STYLE_LOCK = (
    "Balanced editorial 2D illustration; symbolic anonymous human figures; "
    "hybrid detail with simplified readable foreground subjects and key objects, "
    "supporting midground details, and atmospheric background depth. "
    "Use the TCon palette: deep navy #101B2A, slate #263B49, cyan #20B8CC, "
    "coral #F07859, and warm ivory #E9E5DC. Modern, informative, approachable, "
    "thoughtful, cinematic but uncluttered. Portrait 9:16 composition, designed "
    "for a 1080x1920 mobile video frame, clear focal hierarchy, safe negative "
    "space for captions, and separable visual layers where useful."
)
NEGATIVE_LOCK = (
    "No photorealism, no 3D render, no anime, no childish cartoon style, "
    "no random text, no letters, no captions, no watermark, no unrelated logos, "
    "no copied platform branding, no clutter, no generic floating-icon collage."
)


def clean(value: Any) -> str:
    if value is None:
        return ""
    if isinstance(value, (dict, list)):
        return json.dumps(value, ensure_ascii=False)
    return str(value).strip()


def shot_prompt(scene: dict[str, Any], index: int, episode_id: str) -> dict[str, Any]:
    shot_id = clean(scene.get("id")) or f"{episode_id.upper()}-S{index:02d}"
    visual = scene.get("visual") if isinstance(scene.get("visual"), dict) else {}
    legacy_visual = clean(scene.get("visual_prompt"))
    description = clean(visual.get("description")) or legacy_visual
    if not description:
        description = clean(scene.get("story_beat")) or clean(scene.get("viewer_takeaway"))
    if not description:
        raise ValueError(f"{shot_id}: missing visual.description, visual_prompt, story_beat, and viewer_takeaway")

    parts = [
        f"SHOT {shot_id}.",
        f"Story beat: {clean(scene.get('story_beat')) or clean(scene.get('headline')) or 'Communicate the episode beat clearly.'}",
        f"Viewer takeaway: {clean(scene.get('viewer_takeaway')) or clean(scene.get('caption')) or 'Make the intended idea immediately understandable.'}",
        f"Visual direction: {description}",
    ]
    for key, label in (
        ("metaphor", "Metaphor"),
        ("composition", "Composition"),
        ("camera", "Camera"),
    ):
        value = clean(visual.get(key))
        if value:
            parts.append(f"{label}: {value}")
    if scene.get("asset_requirements"):
        parts.append("Asset requirements: " + clean(scene["asset_requirements"]))
    parts.extend([STYLE_LOCK, NEGATIVE_LOCK])
    prompt = " ".join(part for part in parts if part)
    return {
        "id": shot_id,
        "start_seconds": scene.get("start"),
        "end_seconds": scene.get("end"),
        "narration": clean(scene.get("narration")),
        "prompt": prompt,
        "source_visual_field": "visual.description" if visual.get("description") else "visual_prompt",
        "review_status": "pending",
    }


def build_manifest(episode: dict[str, Any]) -> dict[str, Any]:
    episode_id = clean(episode.get("id")) or "episode-unknown"
    scenes = episode.get("scenes")
    if not isinstance(scenes, list) or not scenes:
        raise ValueError("Episode must contain a non-empty scenes array.")
    shots = [shot_prompt(scene, i, episode_id) for i, scene in enumerate(scenes, 1)]
    previous_end = None
    for shot in shots:
        start, end = shot["start_seconds"], shot["end_seconds"]
        if not isinstance(start, (int, float)) or not isinstance(end, (int, float)) or end <= start:
            raise ValueError(f"{shot['id']}: start/end must be numeric and end must be greater than start.")
        if previous_end is not None and start < previous_end:
            raise ValueError(f"{shot['id']}: scene overlaps the previous scene.")
        previous_end = end
    return {
        "manifest_version": 1,
        "episode_id": episode_id,
        "title": clean(episode.get("title")),
        "language": clean(episode.get("language")) or "id-ID",
        "format": episode.get("format", {"width": 1080, "height": 1920, "fps": 30}),
        "standards": {
            "visual_bible": VISUAL_BIBLE,
            "storyboard_bible": STORYBOARD_BIBLE,
            "visual_style": "TCon Visual Production Bible v1.0",
        },
        "generator": "scripts/generate_prompts.py",
        "provider": None,
        "note": "Prompt manifest only. No image generation is performed. Review prompts before sending them to an image provider.",
        "shots": shots,
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--episode", type=Path, default=DEFAULT_EPISODE)
    parser.add_argument("--output", type=Path, default=None)
    parser.add_argument("--overwrite", action="store_true", help="Explicitly replace an existing prompt manifest.")
    args = parser.parse_args()

    episode_path = args.episode.resolve()
    if args.output:
        output_path = args.output.resolve()
    elif episode_path.parent.name == "naskah" and episode_path.parent.parent.parent.name == "episodes":
        output_path = episode_path.parent.parent / "prompt" / "visual-prompts.json"
    else:
        output_path = DEFAULT_OUTPUT_DIR / f"{episode_path.stem}.prompts.json"
    if output_path.exists() and not args.overwrite:
        raise SystemExit(f"Refusing to overwrite existing manifest: {output_path}. Use --overwrite to replace it.")
    episode = json.loads(episode_path.read_text(encoding="utf-8"))
    manifest = build_manifest(episode)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Created {output_path} with {len(manifest['shots'])} shot prompts.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
