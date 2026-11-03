#!/usr/bin/env python3
"""Analyze image assets for review: metadata, filename hints, and perceptual similarity.

This tool never edits the registry or source files. Filename-based categories are
suggestions only; visual classification and approval remain human decisions.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import re
from pathlib import Path
from typing import Any

from PIL import Image, UnidentifiedImageError

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_SCAN = ROOT / "public" / "assets" / "inbox"
EXTENSIONS = {".png", ".jpg", ".jpeg", ".webp", ".gif", ".bmp", ".tif", ".tiff"}
CATEGORY_HINTS = {
    "TOKOH": {"person", "people", "character", "tokoh", "manusia", "figure"},
    "BACKGROUND": {"background", "bg", "environment", "scene", "lingkungan", "latar"},
    "OBJEK": {"object", "phone", "clock", "chair", "table", "objek", "ponsel"},
    "SIMBOL": {"symbol", "icon", "network", "loop", "path", "simbol", "ikon"},
    "ELEMEN": {"effect", "particle", "glow", "shadow", "light", "efek", "cahaya"},
}


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def filename_hints(path: Path) -> dict[str, Any]:
    tokens = set(filter(None, re.split(r"[^a-z0-9]+", path.stem.lower())))
    matches = [category for category, words in CATEGORY_HINTS.items() if tokens & words]
    category = matches[0] if len(matches) == 1 else "PERLU_REVIEW"
    return {
        "suggested_category": category,
        "category_confidence": "low" if category != "PERLU_REVIEW" else "none",
        "matched_filename_tokens": sorted(tokens & set().union(*CATEGORY_HINTS.values())),
        "tags": sorted(tokens),
        "method": "filename_tokens_only",
    }


def difference_hash(image: Image.Image) -> int:
    gray = image.convert("L").resize((9, 8), Image.Resampling.LANCZOS)
    pixels = list(gray.getdata())
    value = 0
    for y in range(8):
        row = pixels[y * 9:(y + 1) * 9]
        for x in range(8):
            value = (value << 1) | int(row[x] > row[x + 1])
    return value


def analyze_image(path: Path, root: Path) -> dict[str, Any]:
    with Image.open(path) as image:
        image.seek(0)
        width, height = image.size
        has_alpha = "A" in image.getbands() or "transparency" in image.info
        perceptual_hash = difference_hash(image.copy())
        return {
            "path": path.relative_to(root).as_posix(),
            "sha256": sha256_file(path),
            "file_size_bytes": path.stat().st_size,
            "format": image.format,
            "dimensions": {"width": width, "height": height},
            "mode": image.mode,
            "transparent_background_possible": has_alpha,
            "perceptual_hash_dhash": f"{perceptual_hash:016x}",
            "filename_hints": filename_hints(path),
            "review_status": "pending_review",
        }


def hamming_distance(left: str, right: str) -> int:
    return (int(left, 16) ^ int(right, 16)).bit_count()


def build_report(scan_dir: Path, root: Path, threshold: int = 6) -> dict[str, Any]:
    assets = []
    errors = []
    for path in sorted(p for p in scan_dir.rglob("*") if p.is_file()):
        if path.suffix.lower() not in EXTENSIONS:
            continue
        try:
            assets.append(analyze_image(path, root))
        except (OSError, UnidentifiedImageError, ValueError) as exc:
            errors.append({"path": path.relative_to(root).as_posix(), "error": str(exc)})
    similar = []
    for index, left in enumerate(assets):
        for right in assets[index + 1:]:
            distance = hamming_distance(
                left["perceptual_hash_dhash"], right["perceptual_hash_dhash"]
            )
            if distance <= threshold:
                similar.append({
                    "path_a": left["path"],
                    "path_b": right["path"],
                    "hamming_distance": distance,
                    "threshold": threshold,
                    "review_note": "Possible visual duplicate; verify manually.",
                })
    return {
        "report_version": 1,
        "scan_directory": scan_dir.relative_to(root).as_posix(),
        "scanned_images": len(assets),
        "similarity_method": "64-bit difference hash (dHash)",
        "similarity_threshold": threshold,
        "assets": assets,
        "possible_similar_pairs": similar,
        "errors": errors,
        "safety": {
            "registry_modified": False,
            "source_files_modified": False,
            "automatic_classification": False,
            "approval_granted": False,
        },
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--scan-dir", type=Path, default=DEFAULT_SCAN)
    parser.add_argument("--threshold", type=int, default=6,
                        help="Maximum dHash Hamming distance for a possible-similarity warning (0-64).")
    parser.add_argument("--output", type=Path, default=None,
                        help="Optional JSON report path. Existing files are never overwritten.")
    args = parser.parse_args()
    if not 0 <= args.threshold <= 64:
        parser.error("--threshold must be between 0 and 64")
    scan_dir = args.scan_dir.resolve()
    if not scan_dir.exists() or not scan_dir.is_dir():
        parser.error(f"scan directory does not exist or is not a directory: {scan_dir}")
    report = build_report(scan_dir, ROOT, args.threshold)
    rendered = json.dumps(report, ensure_ascii=False, indent=2) + "\n"
    if args.output:
        output = args.output.resolve()
        if output.exists():
            parser.error(f"refusing to overwrite existing report: {output}")
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_text(rendered, encoding="utf-8")
        print(f"Wrote analysis report: {output}")
    else:
        print(rendered, end="")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
