# Android core module

- `AiModels.kt`: provider identities, transcript messages, and bounded meeting configuration.
- `AiProvider.kt`: adapter contract and declared capabilities.
- `MeetingOrchestrator.kt`: sequential, bounded discussion coordinator with dependency injection.

No network client or provider credential is included yet. OpenAI, Gemini, and Anthropic adapters must be implemented separately using official APIs and explicit user configuration. Android app handoff is a separate assisted mode and must not be represented as automatic response retrieval.
