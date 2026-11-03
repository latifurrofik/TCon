# TCon development status

## Implemented foundation
- Android launcher workspace for ChatGPT, Gemini, and Claude.
- Text prompt handoff through Android Sharesheet.
- Local draft persistence.
- Provider-neutral conversation models and capability declarations.
- Bounded sequential meeting orchestrator with provider injection.
- Unit tests for participant order, transcript propagation, turn/length bounds, and provider failure handling.

## Not implemented yet
- Concrete OpenAI, Gemini, and Anthropic API adapters.
- Secure provider credential entry/storage and usage/cost controls.
- Automatic response capture from official consumer apps.
- Voice capture, speech output, and audio turn arbitration.
- Persistent conversation database, file ingestion, project memory, task automation.

## Cost and access
Official consumer app subscriptions do not automatically provide API credits. Direct API adapters must be optional, use explicit user-configured credentials, and clearly disclose potential charges. Assisted app handoff remains the no-API path.

## Validation status
No GitHub Actions workflow has been started from this development branch. The build and unit tests must be run and reviewed before the PR is marked ready.
