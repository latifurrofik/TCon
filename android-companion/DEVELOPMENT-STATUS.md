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
- Local project task board in SQLite: add/edit/delete tasks, track TODO / IN_PROGRESS / DONE status, set a fixed calendar deadline and LOW / MEDIUM / HIGH / URGENT priority, and include status, priority, deadline, and details for up to 20 open tasks as reference context in new AI meetings. Database schema migration v2 to v4 preserves existing tasks and defaults migrated priorities to MEDIUM.
- Text and multimodal attachments through Android file picker: up to five files per meeting. TXT/Markdown/CSV/TSV/JSON/XML/HTML/YAML/LOG up to 512 KB each; DOCX/XLSX are extracted locally with bounded parsing. Extracted text is capped at 12,000 characters total. PDF/JPEG/PNG/WebP are sent natively as base64 inputs to OpenAI Responses, Gemini generateContent, and Anthropic Messages (2 MB per binary file, 5 MB combined). Each selected provider receives binary files on its first turn only.
- Provider-specific conversation roles: each AI sees its own earlier messages as assistant turns and peer AI contributions as attributed user context; adjacent same-role turns are coalesced for API compatibility.
- HTTP request cancellation wired to coroutine cancellation; stopping a meeting disconnects the active provider request.
- Android app backup disabled so encrypted API-key ciphertext is not restored without its device-bound Keystore key.
- Indonesian push-to-talk speech recognition with runtime microphone permission.
- Sequential TTS playback of provider responses; microphone is cancelled when the app goes to background.
- Unit tests for orchestration order, transcript propagation, turn/length bounds, provider failure handling, and repeated participant rounds.
- In-app updater: checks latest stable GitHub Release, validates semantic version and monotonic versionCode, bounds DownloadManager transfer size (150 MB cap / 15-minute timeout), verifies exact asset size and GitHub SHA-256 digest, checks APK package ID/versionName/versionCode, then opens Android's package installer through FileProvider. User approval and Android's unknown-app installation permission remain required.
- Manual signed-release workflow added. It builds/tests a signed APK and publishes it as a GitHub Release asset when release signing secrets are configured.

## Not implemented yet
- Build/test validation on GitHub Actions or a physical Android device; debug and signed-release workflows are manual and have not been started. No APK artifact has been produced or verified yet.
- Streaming responses, continuous/full-duplex voice, hands-free turn detection, robust audio-focus arbitration, and selectable distinct TTS voices.
- DOCX/XLSX extraction currently reads document text and worksheet cell values only; embedded images, charts, macros, formulas without cached values, and complex layout are not interpreted. Audio and video ingestion are not implemented. PDF and JPEG/PNG/WebP binary attachments are temporary in-memory inputs; they are not stored in meeting history or persisted as project files.
- Automatic memory extraction/summarization and artifact management; project memory is currently a simple user-managed notes store.
- Task reminders/notifications and task workflow automation. Fixed-date deadline editing and task priority selection are implemented; reminders are not implemented.
- The in-app updater cannot find an installable update until a stable GitHub Release with a signed APK asset is published. The signed-release workflow derives versionCode from semantic version using major × 1,000,000 + minor × 1,000 + patch, and refuses to reuse a Git tag. Permanent release-signing secrets and a first signed release are still required. A debug APK cannot normally be upgraded in place to a differently signed production APK.
- DOCX/XLSX extraction currently reads document text and worksheet cell values only; embedded images, charts, macros, formulas without cached values, and complex layout are not interpreted. Audio and video ingestion are not implemented. PDF and JPEG/PNG/WebP binary attachments are temporary in-memory inputs; they are not stored in meeting history or persisted as project files.
- Automatic reading of responses from consumer ChatGPT/Gemini/Claude apps (not exposed through the handoff intents).

## Cost and access
Official consumer app subscriptions do not automatically include API credits. Direct API adapters require separate provider API keys and can incur charges. The app asks for confirmation before an API discussion. Manual app handoff remains available without API keys.

## Validation status
The manual-only Android debug workflow is present at `.github/workflows/android-debug.yml`; it runs `testDebugUnitTest` and `assembleDebug`, then publishes the debug APK and test reports as artifacts. The signed-release workflow is at `.github/workflows/android-release.yml` and requires the four documented release-signing secrets. Neither workflow has been started. Voice behavior still requires real-device testing with the Android speech service and TTS engine. Review provider model IDs and API behavior against current provider documentation before production use.
