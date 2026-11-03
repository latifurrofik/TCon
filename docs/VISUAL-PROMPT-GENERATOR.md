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
- Input: `data/episodes/episode_1/naskah/storyboard.json`
- Output: `data/episodes/episode_1/prompt/visual-prompts.json`

For another episode, pass its storyboard file. The output is automatically placed in that episode's `prompt/` folder:

```bash
python scripts/generate_prompts.py --episode data/episodes/episode_2/naskah/storyboard.json
```

Legacy JSON episode files remain supported; their manifests continue to use `data/prompt-manifests/` unless `--output` is supplied.

The generator refuses to overwrite an existing manifest. The default input/output follow the canonical `episode_N` workspace. Use `--overwrite` only when intentionally regenerating it:

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
