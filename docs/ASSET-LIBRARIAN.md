# TCon AI Asset Librarian

## Purpose
Maintain a reusable, layered visual asset library for Remotion Shorts. The main library is shared across episodes; episode-specific assets remain isolated until explicitly promoted.

## Folder rules
- `TOKOH/`: people and character cutouts only.
- `BACKGROUND/`: environments and scene backgrounds only.
- `OBJEK/`: independent foreground objects and props.
- `SIMBOL/`: conceptual and metaphorical visual assets.
- `ELEMEN/`: reusable visual effects such as light, particles, shadows, and overlays.
- `PERLU_REVIEW/`: assets whose category, quality, rights, or layer status is uncertain.
- `KHUSUS_EPISODE/episode-NN/`: assets used only by that episode.

An image containing multiple inseparable elements must be recorded as a composite asset; it must not be silently split or misfiled.

## Layering requirements
Prefer transparent PNG or WebP for characters, objects, symbols, and effects. Backgrounds may be opaque. A composite image must be marked `layer_type: "composite"`. AI may suggest extraction into layers, but must not overwrite the original.

## Registry
All accepted assets are indexed in `data/asset-library.json`. Each record should include:
- `asset_id`: stable unique identifier.
- `path`: repository-relative path.
- `category` and `subcategory`.
- `description` and `tags`.
- `layer_type`: character, background, object, symbol, effect, or composite.
- `transparent_background`.
- `status`: pending_review, approved, or rejected.
- `source` and `license` when known.

## AI librarian safety rules
1. Never delete assets automatically.
2. Never overwrite an existing asset.
3. Never move an approved asset without explicit approval.
4. Put uncertain classifications in `PERLU_REVIEW/`.
5. Keep episode-only assets separate from the main library.
6. Preserve original uploads; derived cutouts get new asset IDs.
7. Record proposed changes before applying them.

## Planned workflow
1. Ingest image files.
2. Inspect image content and technical properties.
3. Suggest category, subcategory, tags, and layer type.
4. Validate destination and duplicate candidates.
5. Auto-file only when confidence is high and the destination is unambiguous; otherwise request review.
6. Update the registry.
7. Let the storyboard generator search approved assets by meaning and tags.

## Current implementation status
This commit establishes the folder structure and registry contract. Automated visual classification, duplicate detection, upload UI, and storyboard integration are subsequent stages.
