# TCon Asset Manager — Foundation v1

## Purpose
Inventory incoming image files, detect exact duplicates, and create consistent registry records for later human review. This first version is deliberately conservative: it does not use AI vision, move files, generate cutouts, or connect assets to storyboard shots.

## Inbox
Place candidate files under \`public/assets/inbox/\`. Supported extensions are PNG, JPG/JPEG, WebP, GIF, BMP, TIF, and TIFF. Keep source files in place. The inbox is not a permanent approved library category.

## Commands
Run a read-only inventory (default):
\`\`\`bash
python scripts/manage_assets.py
\`\`\`

Register newly discovered files without moving them:
\`\`\`bash
python scripts/manage_assets.py --apply
\`\`\`

Use a different folder or registry:
\`\`\`bash
python scripts/manage_assets.py --scan-dir public/assets/inbox --registry data/asset-library.json
\`\`\`

## Duplicate detection
- SHA-256 hashes detect byte-identical files.
- Duplicates within the current scan and files already present in the registry are reported, not copied or deleted.
- Perceptual similarity (visually similar but differently encoded/cropped images) is not implemented.

## Classification and review
Every newly registered file starts in \`PERLU_REVIEW\` with \`pending_review\` status. The tool does not infer visual content or rights. A reviewer must confirm category, subcategory, description, tags, layer type, transparency, source, and license before promotion to an approved library category.

## Safety guarantees
- Read-only unless \`--apply\` is supplied.
- Never moves, renames, deletes, or overwrites image files.
- Never overwrites existing registry entries by asset ID.
- Exact duplicates are reported and skipped.
- Unknown dimensions remain null; PNG and JPEG dimensions are read using the Python standard library.
- Registry updates append records only.

## Current limitations
No visual classification, perceptual duplicate detection, metadata extraction from image-generation providers, asset approval UI, or automatic storyboard matching. These are separate milestones and must not be implied by this foundation.
