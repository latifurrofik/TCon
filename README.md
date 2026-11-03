# TCon — AI-assisted YouTube Shorts Studio

TCon is a cloud-first production system for Indonesian YouTube Shorts. The repository is managed from Android; GitHub Actions performs production tasks on hosted runners.

## Product goals
- Create vertical 9:16 Shorts with motion graphics and editorial 2D visuals.
- Support a reusable, layered visual asset library managed by an AI Asset Manager.
- Generate Indonesian narration, timed captions, and rendered MP4 previews.
- Produce up to three videos per day after the pipeline is stable.
- Require explicit human approval before any YouTube publication.

## Current production entry points
- `package.json`: single root Node/Remotion project.
- `src/`: Remotion composition and episode rendering components.
- `data/episodes/episode_N/`: canonical workspace for each episode; see `data/episodes/README.md`.
- `data/episode-01.json`: legacy sample retained temporarily for pipeline compatibility.
- `scripts/`: media generation scripts.
- `.github/workflows/`: cloud validation and manual production workflows.
- `docs/`: architecture, operations, and implementation decisions.

## Episode workspace

Each episode uses a short, consistent folder name such as `data/episodes/episode_1/`. It contains `metadata.json`, `naskah/`, `prompt/`, `assets/`, `audio/`, and `output/`. Copy `data/episodes/episode-template/` when starting a new episode. See [episode workspace rules](data/episodes/README.md).

## Run from GitHub on Android
1. Open **Actions**.
2. Choose **TCon cloud Shorts production**.
3. Run the workflow manually.
4. Download and review the preview artifact.

Production currently creates a preview artifact only. Publishing is intentionally not automated; an approval gate must be implemented and tested before YouTube upload is enabled.

## Development
Requires Node.js 20+ and Python 3.11+ for the current media scripts.

```bash
npm install
npm run typecheck
npm run dev
```

See [Remotion production notes](README-REMOTION.md) and [system architecture](docs/ARCHITECTURE.md).
