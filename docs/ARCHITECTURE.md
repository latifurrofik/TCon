# TCon System Architecture

## Design principles
1. Cloud-first: Android is the control surface; GitHub Actions runs builds and renders.
2. Human-in-the-loop publishing: rendering and publication are separate stages. No public upload without explicit approval for that specific video.
3. Reusable assets: episode assets and permanent library assets have separate lifecycles.
4. Safe automation: preserve originals, never overwrite approved assets silently, and route uncertain classifications to review.
5. Provider independence: image, speech, and optional image-to-video providers must be replaceable without rewriting the renderer.
6. Traceability: every episode, asset, render, and approval should have stable IDs and manifests.

## Target pipeline
```text
Research & fact check
        |
Episode brief -> Script -> Storyboard
                         |
                  Asset selection
                         |
            Asset Request Queue (if missing)
                         |
                  AI Asset Manager
                         |
          Visual assets + metadata registry
                         |
             TTS + timed captions + SFX
                         |
                 Remotion renderer
                         |
             Preview artifact + manifest
                         |
                 Human approval
                         |
             YouTube upload (later stage)
```

## Repository ownership
- `src/`: Remotion runtime and reusable visual components.
- `data/`: episode source data, registries, manifests, and request queues.
- `scripts/`: deterministic automation and provider adapters.
- `public/assets/`: reusable visual assets.
- `public/images/`, `public/audio/`: generated episode-specific media; generated outputs are ignored by Git unless deliberately promoted.
- `.github/workflows/`: cloud jobs, validation, and gated production.
- `docs/`: operating procedures and architecture decisions.
- `tests/`: automated checks for data contracts and pipeline logic.
- `schemas/`: JSON schemas shared by scripts and episode data.

## Current state (audit)
- One root Remotion application is the production entry point used by the workflows.
- A second, older `remotion/` application duplicated the root app and was not used by the active workflow; it was removed in the repository cleanup branch.
- The current episode and media scripts are hard-coded to episode 01. Multi-episode discovery and configuration remain future work.
- Image generation uses AI Horde; availability and anonymous queue time are not guaranteed.
- Edge TTS is used for narration.
- Render output is an Actions artifact; no upload step exists in the current production workflow.
- The legacy `bot.py` uploader can publish videos publicly without a per-video approval gate. It is retained for now to avoid deleting a separate capability, but must not be wired into the production workflow.

## Planned modules (not all implemented)
1. Episode schema and multi-episode manifest.
2. AI Asset Manager: intake, metadata extraction, classification confidence, review queue, and safe GitHub commits.
3. Asset Request Queue linked to storyboard scene IDs.
4. Storyboard-to-Remotion scene compiler and reusable motion components.
5. Audio mix stage for narration, music, and SFX.
6. Render QA: duration, dimensions, audio presence, caption bounds, and artifact manifest.
7. Approval record tied to an exact render checksum.
8. YouTube uploader that accepts only an approved render ID and never defaults to public visibility.

## Safety boundaries
- No secrets in source control.
- Never delete or replace reusable assets automatically.
- Low-confidence assets go to `PERLU_REVIEW`.
- A render is not an approval.
- Approval for one render does not approve later renders.
- Do not store OAuth refresh tokens or service credentials in the repository.
