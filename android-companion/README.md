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

## Release signing

The release signing key is a long-lived application identity. Keep the same keystore and key alias for every future release. If the key is lost or replaced, Android will not accept an ordinary in-place update signed by the new key.

### Generate the keystore on Android with Termux

1. Install Termux from its official F-Droid/GitHub distribution.
2. In Termux, install the JDK tools:

   ```sh
   pkg update
   pkg install openjdk-17
   ```

3. Generate the keystore. Keytool prompts for passwords; do not put passwords directly in the command:

   ```sh
   keytool -genkeypair -v \
     -keystore tcon-release.jks \
     -alias tcon-release \
     -keyalg RSA -keysize 4096 -validity 10000
   ```

4. Verify that the key can be read:

   ```sh
   keytool -list -v -keystore tcon-release.jks -alias tcon-release
   ```

5. Make at least two secure backups of `tcon-release.jks` outside the repository. Store the keystore passwords and alias in a password manager or another secure location. Never commit the keystore or send it in chat.

### GitHub Actions secrets

In the repository, open **Settings → Secrets and variables → Actions → New repository secret**. The release workflow (implemented in the next release-publishing issue) will use these secret names:

- `TCON_RELEASE_KEYSTORE_BASE64`: Base64-encoded bytes of `tcon-release.jks`.
- `TCON_RELEASE_STORE_PASSWORD`: keystore password.
- `TCON_RELEASE_KEY_ALIAS`: `tcon-release`.
- `TCON_RELEASE_KEY_PASSWORD`: key password.

Create the Base64 value locally in Termux with:

```sh
base64 -w 0 tcon-release.jks
```

Copy the output directly into the GitHub secret field. Treat it as a private key: do not paste it into chat, an issue, a commit, or a workflow log. The workflow must decode it to a temporary file during the build and must not upload or print the keystore.

### Local Gradle release build

The Gradle release build reads these environment variables:

- `TCON_RELEASE_STORE_FILE`: path to the local `.jks` file.
- `TCON_RELEASE_STORE_PASSWORD`
- `TCON_RELEASE_KEY_ALIAS`
- `TCON_RELEASE_KEY_PASSWORD`

A release task intentionally fails with a clear message if any value is missing. Debug builds do not require these values and remain independently buildable.

## Current limitations
This is a handoff MVP, not full cross-app automation. Android may show a Sharesheet and the user chooses Gemini. App package IDs and target-app share support must be verified on the actual device. It does not listen to ChatGPT or Gemini conversations, read their screens, or automate image generation.

## Next
Test installation and prompt sharing on the user's Android phone before adding any AccessibilityService. Accessibility must be opt-in, narrowly scoped, visible, and stoppable by the user.
