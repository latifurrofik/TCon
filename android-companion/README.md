# TCon Companion for Android

Native Android MVP for passing image prompts between the official ChatGPT and Gemini apps without a server or API key.

## Current features
- Prompt editor with local draft persistence.
- Android Sharesheet handoff for text prompts.
- Launch buttons for the installed ChatGPT and Gemini apps.
- No password, cookie, session-token, or API-key handling.

## Build
Open the `android-companion` directory in Android Studio, allow Gradle sync, then run the `app` configuration on an Android device or emulator.

Requirements:
- Android Studio with JDK 17.
- Android SDK Platform 35.
- Android 8.0 (API 26) or newer device.

## Current limitations
This is a handoff MVP, not full cross-app automation. Android may show a Sharesheet and the user chooses Gemini. App package IDs and target-app share support must be verified on the actual device. It does not listen to ChatGPT or Gemini conversations, read their screens, or automate image generation.

## Next
Test installation and prompt sharing on the user's Android phone before adding any AccessibilityService. Accessibility must be opt-in, narrowly scoped, visible, and stoppable by the user.
