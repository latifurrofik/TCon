#!/usr/bin/env python3
"""Inspect and register a YouTube thumbnail stored at an episode folder root.

The default command is read-only. --register records an existing thumbnail as
pending_review. --approve is an explicit human action and only approves a
valid thumbnail after registration.
"""
from __future__ import annotations

import argparse
import hashlib
import json
from datetime import datetime, timezone
from pathlib import Path
from typing import Any

from scripts.manage_assets import image_dimensions

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_EPISODE = ROOT / "data" / "episodes" / "episode_1"
THUMBNAIL_NAMES = ("thumbnail.jpg", "thumbnail.png")


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def inspect_thumbnail(episode_dir: Path) -> dict[str, Any]:
    candidates = [episode_dir / name for name in THUMBNAIL_NAMES if (episode_dir / name).is_file()]
    if len(candidates) > 1:
        raise ValueError("Found both thumbnail.jpg and thumbnail.png; keep exactly one.")
    if not candidates:
        return {"exists": False, "status": "missing", "path": None}
    path = candidates[0]
    dimensions = image_dimensions(path)
    if not dimensions:
        raise ValueError(f"Could not read image dimensions: {path}")
    return {
        "exists": True,
        "status": "pending_review",
        "path": path.name,
        "sha256": sha256_file(path),
        "file_size_bytes": path.stat().st_size,
        "dimensions": {"width": dimensions[0], "height": dimensions[1]},
        "aspect_ratio": round(dimensions[0] / dimensions[1], 4),
    }


def update_metadata(episode_dir: Path, report: dict[str, Any], approve: bool) -> None:
    metadata_path = episode_dir / "metadata.json"
    metadata = json.loads(metadata_path.read_text(encoding="utf-8"))
    if not report["exists"]:
        raise ValueError("Cannot register or approve: thumbnail.jpg/png is missing.")
    previous = metadata.get("thumbnail", {})
    status = "approved" if approve else "pending_review"
    record = {
        **report,
        "status": status,
        "approved_at": datetime.now(timezone.utc).isoformat() if approve else None,
    }
    if approve and previous.get("sha256") != report["sha256"]:
        raise ValueError("Thumbnail file changed since registration; register it again and review before approval.")
    metadata["thumbnail"] = record
    metadata_path.write_text(json.dumps(metadata, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--episode-dir", type=Path, default=DEFAULT_EPISODE)
    actions = parser.add_mutually_exclusive_group()
    actions.add_argument("--register", action="store_true", help="Record the current thumbnail as pending_review.")
    actions.add_argument("--approve", action="store_true", help="Explicitly approve the registered, unchanged thumbnail.")
    args = parser.parse_args()
    episode_dir = args.episode_dir.resolve()
    report = inspect_thumbnail(episode_dir)
    metadata_path = episode_dir / "metadata.json"
    if args.register or args.approve:
        update_metadata(episode_dir, report, args.approve)
        print("Thumbnail status updated: " + ("approved" if args.approve else "pending_review"))
    else:
        existing = json.loads(metadata_path.read_text(encoding="utf-8")).get("thumbnail", {})
        if report["exists"] and existing.get("sha256") == report.get("sha256"):
            report["status"] = existing.get("status", "pending_review")
        print(json.dumps(report, ensure_ascii=False, indent=2))
        print("Read-only mode. Use --register after placing a thumbnail; use --approve only after human review.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
