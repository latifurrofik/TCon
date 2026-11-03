package com.tcon.companion.core

enum class ProviderId { OPENAI, GEMINI, CLAUDE }
enum class MessageRole { USER, ASSISTANT, SYSTEM }

data class AiMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val provider: ProviderId? = null,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

data class BinaryAttachment(
    val name: String,
    val mimeType: String,
    val base64Data: String
)

data class MeetingConfig(
    val topic: String,
    val participants: List<ProviderId>,
    val maxTurnsPerProvider: Int = 1,
    val maxResponseCharacters: Int = 1600,
    val projectContext: String = "",
    val attachments: List<BinaryAttachment> = emptyList()
) {
    init {
        require(topic.isNotBlank()) { "Topik rapat tidak boleh kosong." }
        require(participants.isNotEmpty()) { "Pilih minimal satu AI." }
        require(maxTurnsPerProvider in 1..5) { "Jumlah giliran harus 1 sampai 5." }
        require(maxResponseCharacters in 100..6000) { "Batas respons harus 100 sampai 6000 karakter." }
    }
}
