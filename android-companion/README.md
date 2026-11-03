# TCon Android Companion

TCon is an Android-first workspace for collaboration between ChatGPT (OpenAI), Gemini, and Claude (Anthropic).

## Implemented in this development branch
- Native Kotlin Android workspace with launch shortcuts for the three official apps.
- Text handoff through Android Sharesheet and local draft persistence.
- Provider-neutral conversation models and bounded sequential meeting orchestrator.
- Optional official API adapters for OpenAI Responses, Gemini generateContent, and Anthropic Messages.
- API-key encryption at rest using an AES-GCM key held by Android Keystore.
- Basic configuration UI for provider model IDs and API keys, plus a user-confirmed sequential discussion action.
- Meeting setup lets users select configured AI participants and choose 1–5 rounds; each selected AI gets one turn per round.
- Local SQLite history records meeting topics and transcript entries, with a viewer for the latest 50 meetings.
- Editable local project memory notes can be added and deleted; up to 5,000 characters of saved notes are passed as reference context to each AI in a new meeting.
- A local task board supports creating, editing, deleting, and tracking tasks through three statuses; open tasks are passed as reference context to new AI meetings.
- Users can attach up to five small text documents (TXT, Markdown, CSV/TSV, JSON, XML, HTML, YAML, LOG; 512 KB each). Their extracted UTF-8 text is included as reference context for all selected AI participants.
- Unit tests for orchestration order, transcript propagation, turn/length bounds, and provider failure handling.
- Indonesian push-to-talk speech recognition using the Android speech service.
- Sequential AI response playback through Android TextToSpeech, with pitch differences to distinguish speakers.

## Build
Open this directory in Android Studio, sync Gradle, and run the `app` configuration.

Requirements: JDK 17, Android SDK Platform 35, Android 8.0 (API 26) or newer.

## Important limitations
- Direct provider APIs are separate from consumer app subscriptions and can incur charges. The app displays a confirmation before starting API discussion. Do not configure API keys unless you accept the provider's pricing.
- Model IDs are entered by the user because model availability and names can change.
- App handoff does not read replies from ChatGPT, Gemini, or Claude consumer apps. Automatic discussion uses the optional official API adapters.
- Voice is currently push-to-talk and sequential TTS, not continuous full-duplex or hands-free conversation. Distinct AI speakers currently use pitch differences, not guaranteed separate natural voices.
- PDF, DOCX, XLSX, images, audio, and video are not supported by the current attachment reader. Attachments are text-only, limited in size, and held temporarily in memory for the current app session; original files are not uploaded as binary files.
- Streaming, automatic memory extraction/summarization, task automation, and in-app updates are not implemented yet. Meeting history and manually managed project notes are stored locally on the device.
- The Claude app package ID is configured as `com.anthropic.claude`; verify on the target phone.

## Security principles
- Microphone permission is requested only when the user taps voice input. Audio recognition is delegated to the Android speech-recognition service; its data handling depends on the selected device/service.
- Never extract provider app passwords, cookies, or session tokens.
- API keys are encrypted with Android Keystore and are not logged.
- Accessibility automation, if added later, must be opt-in, visible, narrowly scoped, and stoppable.
- Require user approval for consequential external actions.
