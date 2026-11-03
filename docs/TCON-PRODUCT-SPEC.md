# TCon — Product & Technical Specification

**Status:** Living specification / implementation target  
**Platform priority:** Android  
**Core participants:** ChatGPT (GPT), Google Gemini, Anthropic Claude

## 1. Product objective

TCon is a user-controlled multi-AI workspace. It coordinates conversations and tasks among three independent AI providers, supports text and voice interaction, preserves project context, and presents results for user review.

TCon is not intended to impersonate provider apps or extract their private sessions. Each integration must use an official, documented interface or an explicitly user-approved Android handoff path.

## 2. Core interaction modes

1. **Personal chat:** user works with one selected AI.
2. **Pair discussion:** two selected AIs respond to each other through TCon's orchestrator.
3. **Council / group meeting:** GPT, Gemini, and Claude discuss one topic in controlled turns.
4. **Director mode:** a chosen coordinator delegates subtasks, collects results, and requests revisions.
5. **Independent tasks:** one AI performs a task without requiring the others.
6. **Voice room:** user speaks, TCon transcribes, routes the message, and speaks responses with distinct voice identities where supported.
7. **Review mode:** AIs critique a draft, file, answer, or artifact against explicit criteria.

## 3. Provider architecture

Use a provider-neutral interface with adapters for:
- OpenAI
- Google Gemini
- Anthropic Claude
- Android handoff adapters for installed official apps

Provider capabilities must be declared rather than assumed: text input/output, image input, file input, streaming, tool use, speech input/output, context limits, and availability.

API adapters require user-supplied provider credentials stored using Android Keystore-backed encrypted storage or a secure backend secret store. Never log secrets. Never assume consumer subscriptions include API credits. Show provider, model, and estimated/known usage before API calls where possible.

The no-API-key path is an assisted handoff path, not a promise of invisible automated access to official app conversations.

## 4. Orchestrator

The orchestrator owns:
- conversation state and turn order;
- role assignment and task routing;
- per-provider timeouts, cancellation, retries, and error reporting;
- context assembly and message provenance;
- token/cost limits for API adapters;
- approval gates for external actions;
- meeting pause, resume, and stop controls.

A meeting must have configurable maximum turns, response length, and stop conditions to prevent runaway loops.

## 5. Discussion and collaboration

- User can choose coordinator and participant set.
- Each AI receives the relevant prior discussion, not hidden provider data.
- Participants can agree, disagree, ask clarifying questions, and cite supplied sources.
- Coordinator summarizes consensus and unresolved disagreement.
- Optional structured vote with rationale; no false claim that majority equals truth.
- Cross-review uses a rubric selected by the user.
- Revision cycles are bounded and user-stoppable.
- Preserve each response's provider/model identity and timestamp.

## 6. Voice subsystem

- Speech-to-text input with visible microphone state and explicit permission.
- Text-to-speech output with configurable voice/rate.
- Separate voice profile per AI where Android TTS engines permit.
- Turn queue and half-duplex arbitration initially; avoid simultaneous playback/recording feedback.
- Push-to-talk and hands-free modes.
- Barge-in, mute, pause, stop-all, and transcript controls.
- Audio focus handling, Bluetooth/headset routing, and recovery after app backgrounding.
- Do not claim live full-duplex voice until acoustic echo cancellation and real-device tests pass.

## 7. Memory and project context

- Local conversation history and searchable project spaces.
- User-approved persistent facts and project decisions.
- Per-project files and reference material.
- Clear source attribution: user input, provider response, imported file, or generated summary.
- Edit, export, and delete controls.
- Configurable retention; sensitive data excluded by default.
- Context summarization with user-visible review before replacing detailed history.

## 8. Files and artifacts

Support staged implementation for text, Markdown, PDF, images, and common office files. File access must use Android's Storage Access Framework or explicit share/import actions. Keep original files unchanged unless the user requests edits. Track generated artifacts and their source task.

## 9. Task and workflow engine

- Task inbox, status, priority, owner/provider, dependencies, and due date.
- Sequential and parallel task execution.
- Retry policy and failure recovery.
- User approval checkpoints.
- Workflow templates and reusable prompts.
- Notifications for completion, failure, and approval required.
- Audit trail of every action and provider call.

## 10. Safety, privacy, and control

- No password, cookie, or session-token extraction from other apps.
- No hidden microphone or screen capture.
- Explicit permission for microphone, files, notifications, and optional accessibility.
- Accessibility automation must be opt-in, narrowly scoped, visibly active, and easy to stop.
- External publishing, sending, purchasing, deletion, or account changes require user approval.
- Encrypt local sensitive data; minimize telemetry; provide clear data deletion.
- Stop-all control must cancel queued work and active requests where supported.

## 11. Android application screens

1. Home/dashboard and provider availability.
2. One-to-one AI chat.
3. Group meeting room.
4. Voice room with transcript and turn indicators.
5. Task board and approval inbox.
6. Project memory and files.
7. Workflow/template library.
8. Provider/model settings and credential status.
9. Usage, cost, and privacy controls.
10. Activity log and diagnostics.

## 12. Delivery phases and acceptance criteria

### Phase 0 — Safe foundation
- Buildable native Android project and CI debug build.
- Three provider launch shortcuts and text handoff.
- Draft persistence and visible integration limitations.
- Acceptance: installs and opens on target Android device; all available official apps can be launched or clearly reported unavailable.

### Phase 1 — App structure
- Navigation, provider registry, conversation models, local repository, settings.
- Acceptance: create/select chats and meetings; preserve history across app restarts.

### Phase 2 — Official API adapters (optional, cost-aware)
- OpenAI, Gemini, and Anthropic adapters behind a common interface.
- Secure credential handling, timeouts, cancellation, error states, usage display.
- Acceptance: each configured provider can complete a text request independently; no provider is called without explicit configuration.

### Phase 3 — Orchestrated group discussion
- Pair and three-provider meetings, turn management, role prompts, bounded loops, summary.
- Acceptance: all configured providers can respond in sequence and the transcript identifies each speaker.

### Phase 4 — Voice
- Speech input, TTS, distinct speaker labels, queueing, barge-in and stop controls.
- Acceptance: real-device test without feedback loops or overlapping AI speech; microphone state always visible.

### Phase 5 — Collaboration tools
- Cross-review, rubric scoring, revision rounds, voting, file context.
- Acceptance: reproducible task with auditable inputs, outputs, and approval checkpoints.

### Phase 6 — Memory and automation
- Project memory, task board, workflow templates, notifications, audit log.
- Acceptance: user can inspect, edit, export, and delete stored context and stop any workflow.

### Phase 7 — Hardening and distribution
- Accessibility review, offline/error handling, tests, signed distribution strategy, update mechanism.
- Acceptance: documented device matrix, repeatable build, tested update path, and no secrets in repository.

## 13. Non-goals and constraints

- No guarantee that official consumer apps expose their live conversations to third-party apps.
- No silent use of paid APIs or subscription entitlements.
- No promise of fully hands-free cross-app automation until proven on target Android versions and app releases.
- Debug APK delivery and in-app updating are separate milestones; do not block the first installable build on an updater.
