# Android core module

- `AiModels.kt`: provider identities, transcript messages, and bounded meeting configuration.
- `AiProvider.kt`: provider-neutral adapter contract and declared capabilities.
- `MeetingOrchestrator.kt`: sequential discussion coordinator with bounded rounds and response lengths.
- `ApiKeyVault.kt`: AES-GCM encryption at rest with Android Keystore-managed keys.
- `ProviderApiClient.kt`: optional official API adapter for OpenAI Responses, Gemini generateContent, and Anthropic Messages.

Provider adapters require user-configured API keys and model IDs. API calls can incur charges; consumer app subscriptions are not assumed to include API usage. The Android app handoff is a separate assisted mode and does not retrieve app conversations.
