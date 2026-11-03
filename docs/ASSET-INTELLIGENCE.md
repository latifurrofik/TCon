# Asset Intelligence v1

## What it does

`scripts/analyze_assets.py` produces a read-only analysis report for images in the asset inbox:

- extracts dimensions, format, mode, file size, and possible alpha transparency;
- creates a 64-bit difference hash (dHash) and flags pairs whose Hamming distance is at or below the configured threshold;
- suggests broad asset categories and tags from filename tokens only;
- marks every result as pending human review.

## Run

Install the media requirements, then run from the repository root:

```bash
python -m pip install -r requirements-media.txt
python scripts/analyze_assets.py
```

Use a different scan folder or similarity threshold:

```bash
python scripts/analyze_assets.py --scan-dir public/assets/inbox --threshold 6
```

Save a report without overwriting an existing file:

```bash
python scripts/analyze_assets.py --output data/asset-analysis/inbox-report.json
```

Threshold is a dHash Hamming distance from 0 to 64. A smaller threshold is stricter. Similarity is only a review hint: dHash may miss crops or major edits and may flag visually simple unrelated images.

## Classification limits

This version does **not** inspect image semantics with an AI vision model. Category suggestions are based exclusively on filename tokens and have low confidence. A filename containing `person` may suggest TOKOH, but the tool has not verified that a person is actually present. Ambiguous names remain PERLU_REVIEW.

## Safety

- Read-only: does not edit the asset registry.
- Does not move, rename, delete, or modify source images.
- Does not approve, reject, or promote assets.
- Does not link assets to storyboard scenes.
- Does not call external APIs or require paid services.
- Reports are review aids, not authoritative classification.

The next stage can add opt-in visual-semantic classification and storyboard matching, but those require separate model/runtime evaluation and must preserve human approval.
