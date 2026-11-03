# TCon development status

## Implemented in this branch
- Android launcher workspace for ChatGPT, Gemini, and Claude.
- Text prompt handoff through Android Sharesheet and local draft persistence.
- Provider-neutral models and bounded sequential meeting orchestrator.
- Optional OpenAI Responses, Gemini generateContent, and Anthropic Messages API adapter.
- AES-GCM API-key vault backed by Android Keystore.
- Basic UI for entering provider model IDs/API keys, selecting configured meeting participants, choosing 1–5 discussion rounds, confirming API cost, viewing transcript, and stopping the discussion.
- Local SQLite meeting history: stores meeting topic, participants, round count, and transcript entries; the UI can reopen the latest 50 meetings. Data stays in app-private storage and Android backup is disabled.
- Editable project memory stored in SQLite, with add/list/delete controls. Saved notes are injected as project context into every provider's system instruction for new meetings.
- Local project task board in SQLite: add/edit/delete tasks, track TODO / IN_PROGRESS / DONE status, and include up to 20 open tasks as reference context in new AI meetings. Database schema migration v2 to v3 is included.
- Text and multimodal attachments through Android file picker: up to five files per meeting. TXT/Markdown/CSV/TSV/JSON/XML/HTML/YAML/LOG up to 512 KB each and 12,000 context characters total; PDF/JPEG/PNG/WebP are sent natively as base64 inputs to OpenAI Responses, Gemini generateContent, and Anthropic Messages (2 MB per binary file, 5 MB combined). Each selected provider receives binary files on its first turn only.
- Provider-specific conversation roles: each AI sees its own earlier messages as assistant turns and peer AI contributions as attributed user context; adjacent same-role turns are coalesced for API compatibility.
- HTTP request cancellation wired to coroutine cancellation; stopping a meeting disconnects the active provider request.
- Android app backup disabled so encrypted API-key ciphertext is not restored without its device-bound Keystore key.
- Indonesian push-to-talk speech recognition with runtime microphone permission.
- Sequential TTS playback of provider responses; microphone is cancelled when the app goes to background.
- Unit tests for orchestration order, transcript propagation, turn/length bounds, provider failure handling, and repeated participant rounds.

## Not implemented yet
- Build/test validation on GitHub Actions or a physical Android device; a manual-only debug build workflow has been added but has not been started.
- Streaming responses, continuous/full-duplex voice, hands-free turn detection, robust audio-focus arbitration, and selectable distinct TTS voices.
- DOCX, XLSX, audio, and video ingestion are not implemented. PDF and JPEG/PNG/WebP binary attachments are temporary in-memory inputs; they are not stored in meeting history or persisted as project files.
- Automatic memory extraction/summarization and artifact management; project memory is currently a simple user-managed notes store.
- Task due-date editing, reminders/notifications, and task workflow automation.
- Task board, workflow automation, notifications, and in-app update mechanism.
- Automatic reading of responses from consumer ChatGPT/Gemini/Claude apps (not exposed through the handoff intents).

## Cost and access
Official consumer app subscriptions do not automatically include API credits. Direct API adapters require separate provider API keys and can incur charges. The app asks for confirmation before an API discussion. Manual app handoff remains available without API keys.

## Validation status
The manual-only Android debug workflow is present at `.github/workflows/android-debug.yml`; it runs `testDebugUnitTest` and `assembleDebug`, then publishes the debug APK and test reports as artifacts. It has not been started. Voice behavior still requires real-device testing with the Android speech service and TTS engine. Review provider model IDs and API behavior against current provider documentation before production use.
