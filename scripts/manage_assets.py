#!/usr/bin/env python3
"""Inventory image assets without moving, deleting, or overwriting source files.

Default mode is a read-only report. Pass --apply to add new, unclassified assets
to the registry; uncertain assets always remain in PERLU_REVIEW/pending_review.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import mimetypes
import struct
from collections import defaultdict
from datetime import datetime, timezone
from pathlib import Path
from typing import Any

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_SCAN = ROOT / "public" / "assets" / "inbox"
DEFAULT_REGISTRY = ROOT / "data" / "asset-library.json"
IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".webp", ".gif", ".bmp", ".tif", ".tiff"}


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def image_dimensions(path: Path) -> tuple[int, int] | None:
    """Read common image dimensions without requiring third-party packages."""
    try:
        with path.open("rb") as f:
            header = f.read(32)
            if header.startswith(b"\x89PNG\r\n\x1a\n") and len(header) >= 24:
                return struct.unpack(">II", header[16:24])
            if header[:2] == b"\xff\xd8":
                f.seek(2)
                while True:
                    marker_start = f.read(1)
                    if not marker_start:
                        break
                    if marker_start != b"\xff":
                        continue
                    marker = f.read(1)
                    while marker == b"\xff":
                        marker = f.read(1)
                    if marker in (b"\xd8", b"\xd9", b"\x00"):
                        continue
                    length_bytes = f.read(2)
                    if len(length_bytes) != 2:
                        break
                    length = struct.unpack(">H", length_bytes)[0]
                    if marker[0] in {
                        0xC0, 0xC1, 0xC2, 0xC3, 0xC5, 0xC6, 0xC7,
                        0xC9, 0xCA, 0xCB, 0xCD, 0xCE, 0xCF,
                    }:
                        data = f.read(5)
                        if len(data) == 5:
                            height, width = struct.unpack(">HH", data[1:5])
                            return width, height
                        break
                    f.seek(length - 2, 1)
    except (OSError, struct.error):
        return None
    return None


def load_registry(path: Path) -> dict[str, Any]:
    data = json.loads(path.read_text(encoding="utf-8"))
    if not isinstance(data, dict) or not isinstance(data.get("assets"), list):
        raise ValueError(f"Invalid asset registry structure: {path}")
    return data


def inventory(scan_dir: Path, registry: dict[str, Any]) -> dict[str, Any]:
    if not scan_dir.exists():
        return {"scanned": 0, "new_assets": [], "duplicates": [], "ignored": []}
    known_hashes = {
        item.get("sha256"): item
        for item in registry["assets"]
        if isinstance(item, dict) and item.get("sha256")
    }
    seen: dict[str, Path] = {}
    new_assets, duplicates, ignored = [], [], []
    files = sorted(p for p in scan_dir.rglob("*") if p.is_file())
    for path in files:
        if path.suffix.lower() not in IMAGE_EXTENSIONS:
            ignored.append(path.relative_to(ROOT).as_posix())
            continue
        digest = sha256_file(path)
        relative = path.relative_to(ROOT).as_posix()
        if digest in seen:
            duplicates.append({
                "path": relative,
                "duplicate_of": seen[digest].relative_to(ROOT).as_posix(),
                "sha256": digest,
                "match": "same_scan",
            })
            continue
        seen[digest] = path
        if digest in known_hashes:
            duplicates.append({
                "path": relative,
                "duplicate_of": known_hashes[digest].get("path"),
                "sha256": digest,
                "match": "registry",
            })
            continue
        dimensions = image_dimensions(path)
        asset_id = "AST-" + digest[:12].upper()
        new_assets.append({
            "asset_id": asset_id,
            "path": relative,
            "category": "PERLU_REVIEW",
            "subcategory": "belum_diklasifikasi",
            "description": "",
            "tags": [],
            "layer_type": None,
            "transparent_background": None,
            "dimensions": (
                {"width": dimensions[0], "height": dimensions[1]}
                if dimensions else None
            ),
            "mime_type": mimetypes.guess_type(path.name)[0],
            "file_size_bytes": path.stat().st_size,
            "sha256": digest,
            "status": "pending_review",
            "source": "inbox_import",
            "license": "unknown",
            "prompt": None,
            "provenance": [],
        })
    return {
        "scanned": len(files),
        "new_assets": new_assets,
        "duplicates": duplicates,
        "ignored": ignored,
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--scan-dir", type=Path, default=DEFAULT_SCAN)
    parser.add_argument("--registry", type=Path, default=DEFAULT_REGISTRY)
    parser.add_argument("--apply", action="store_true", help="Append new pending-review records to the registry.")
    args = parser.parse_args()
    scan_dir = args.scan_dir.resolve()
    registry_path = args.registry.resolve()
    registry = load_registry(registry_path)
    report = inventory(scan_dir, registry)
    print(json.dumps(report, ensure_ascii=False, indent=2))
    if args.apply and report["new_assets"]:
        existing_ids = {a.get("asset_id") for a in registry["assets"] if isinstance(a, dict)}
        additions = [a for a in report["new_assets"] if a["asset_id"] not in existing_ids]
        registry["assets"].extend(additions)
        registry["updated_at"] = datetime.now(timezone.utc).isoformat()
        registry_path.write_text(json.dumps(registry, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        print(f"Registered {len(additions)} asset(s) as pending review.")
    elif args.apply:
        print("No new assets to register.")
    else:
        print("Read-only mode: registry was not changed. Re-run with --apply to register pending-review records.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
