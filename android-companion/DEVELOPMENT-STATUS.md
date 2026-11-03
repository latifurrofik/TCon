# TCon development status

## Implemented in this branch
- Android launcher workspace for ChatGPT, Gemini, and Claude.
- Text prompt handoff through Android Sharesheet and local draft persistence.
- Provider-neutral models and bounded sequential meeting orchestrator.
- Optional OpenAI Responses, Gemini generateContent, and Anthropic Messages API adapter.
- AES-GCM API-key vault backed by Android Keystore.
- Basic UI for entering provider model IDs/API keys, starting a confirmed discussion, viewing transcript, and stopping the discussion.
- Unit tests for orchestration order, transcript propagation, turn/length bounds, and provider failure handling.

## Not implemented yet
- Build/test validation on GitHub Actions or a physical Android device.
- Streaming responses, voice capture/output, audio focus and turn arbitration.
- Durable database for chat/meeting history, project memory, file ingestion and artifacts.
- Task board, workflow automation, notifications, and in-app update mechanism.
- Automatic reading of responses from consumer ChatGPT/Gemini/Claude apps (not exposed through the handoff intents).

## Cost and access
Official consumer app subscriptions do not automatically include API credits. Direct API adapters require separate provider API keys and can incur charges. The app asks for confirmation before an API discussion. Manual app handoff remains available without API keys.

## Validation status
No GitHub Actions workflow has been started from this development branch. Run the debug build and unit tests only after the user approves workflow execution. Review provider model IDs and API behavior against current provider documentation before production use.
