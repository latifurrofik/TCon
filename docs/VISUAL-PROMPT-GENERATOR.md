# Visual Prompt Generator

## Purpose

`scripts/generate_prompts.py` converts an episode's scene descriptions into a deterministic, reviewable prompt manifest. It applies the approved Visual Production Bible style lock and can read both the current legacy `visual_prompt` field and the newer storyboard `visual.description` structure.

It does **not** generate images, call an image provider, upload assets, or modify episode source files.

## Run

From the repository root:

```bash
python scripts/generate_prompts.py
```

Defaults:
- Input: `data/episode-01.json`
- Output: `data/prompt-manifests/episode-01.prompts.json`

For another episode:

```bash
python scripts/generate_prompts.py --episode data/episodes/episode-02.json
```

The generator refuses to overwrite an existing manifest. Use `--overwrite` only when intentionally regenerating it:

```bash
python scripts/generate_prompts.py --overwrite
```

## Output contract

Each manifest contains:
- episode identity and format;
- references to the Visual Production Bible and Storyboard Bible;
- stable shot IDs (existing IDs are retained; legacy scenes receive deterministic IDs);
- start/end timing and exact narration segment;
- the composed image prompt;
- source field and pending review status.

The generator validates that scenes exist, have a usable visual description, use numeric start/end values, and do not overlap. It does not require scenes to be contiguous because gaps may be intentional during editing; review timing coverage separately.

## Compatibility and safety

- Legacy `visual_prompt` is accepted to avoid breaking the existing episode.
- New storyboard data is preferred when `visual.description` is present.
- Style constraints are centralized in the script and follow Visual Production Bible v1.0.
- Prompt generation is deterministic and uses only Python's standard library.
- Existing episode data, source images, and approved assets are never overwritten.
- The manifest is a prompt handoff, not evidence that an image has been generated or approved.
- Review prompts before sending them to a provider. Image generation remains a separate, explicitly configured stage.

## Next integration

A later change can add a JSON Schema, asset-library reference resolution, and a provider adapter. Those are deliberately excluded here so prompt construction remains testable and provider-independent.
