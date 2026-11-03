# TCon cloud Shorts production

## One-time setup
1. Open the repository Settings → Secrets and variables → Actions.
2. Add a repository secret named `GEMINI_API_KEY` containing your Google AI Studio API key.
3. Check the current Gemini image API pricing, quota, and availability for your account before running. Image generation is not guaranteed to be free.
4. Open Actions → **TCon cloud Shorts production** → Run workflow.

## What the workflow does
- Generates five portrait 9:16 illustrations using Gemini image generation.
- Generates Indonesian narration MP3s using Edge TTS and word-boundary timestamps.
- Builds timed subtitle data.
- Runs TypeScript validation.
- Renders a 1080×1920, 30 FPS MP4 on a GitHub-hosted Linux runner.
- Publishes the MP4 and intermediate assets as a downloadable workflow artifact for seven days.

## Important costs and safeguards
- The workflow is manual only; it does not run on every push and does not upload to YouTube.
- Gemini image generation may be billable and has account-specific quotas.
- GitHub Actions usage is subject to the repository/account's included minutes and usage limits. A 50-second 1080×1920 render can consume meaningful runner time.
- Edge TTS is an unofficial service integration; availability and terms may change.
- The generated MP4 is a preview. Review it before any publishing workflow is enabled.
- Never put API keys in source files or commit history; use GitHub Actions Secrets.
