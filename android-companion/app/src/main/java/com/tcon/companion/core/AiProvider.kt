package com.tcon.companion.core

data class ProviderCapabilities(
    val textInput: Boolean = true,
    val imageInput: Boolean = true,
    val fileInput: Boolean = true,
    val streaming: Boolean = false,
    val speechInput: Boolean = false,
    val speechOutput: Boolean = false
)

data class ProviderStatus(
    val id: ProviderId,
    val available: Boolean,
    val detail: String? = null
)

/**
 * Provider-neutral contract. Concrete adapters must use official provider APIs
 * or a clearly labelled user-assisted Android handoff. Never read another app's
 * private session, cookies, or credentials.
 */
interface AiProvider {
    val id: ProviderId
    val displayName: String
    val capabilities: ProviderCapabilities

    suspend fun respond(
        systemInstruction: String,
        conversation: List<AiMessage>,
        maxResponseCharacters: Int
    ): Result<String>

    suspend fun respondWithAttachments(
        systemInstruction: String,
        conversation: List<AiMessage>,
        maxResponseCharacters: Int,
        attachments: List<BinaryAttachment>
    ): Result<String> = respond(systemInstruction, conversation, maxResponseCharacters)
}
