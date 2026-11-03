# TCon Android Companion

TCon is being developed as an Android-first workspace for collaboration between ChatGPT, Gemini, and Claude.

## Current implemented foundation
- Native Kotlin Android app.
- Launch shortcuts for installed ChatGPT, Gemini, and Claude apps.
- Text handoff through Android Sharesheet.
- Local persistence of the draft prompt.
- No collection of passwords, cookies, private app sessions, or API keys.

## Build
Open this directory in Android Studio, sync Gradle, and run the `app` configuration.

Requirements:
- JDK 17
- Android SDK Platform 35
- Android 8.0 (API 26) or newer

## Important current limitations
This is a handoff foundation, not an automatic multi-AI conversation system. TCon cannot currently read AI app conversations, control their microphones, or retrieve their responses automatically. The user chooses the target app in the Android Sharesheet and confirms actions.

App package visibility and share behavior must be tested on a real device. The Claude package identifier is currently configured as `com.anthropic.claude`; if the installed app uses a different identifier on the device, the launcher will report it unavailable.

## Development principles
- Prefer official app handoff and free/local Android capabilities first.
- Treat direct provider APIs as optional adapters; API usage may incur separate charges.
- Accessibility automation, if later added, must be opt-in, transparent, narrowly scoped, and stoppable.
- Never extract or store provider app passwords, cookies, or session tokens.
- Require user approval for consequential external actions.
