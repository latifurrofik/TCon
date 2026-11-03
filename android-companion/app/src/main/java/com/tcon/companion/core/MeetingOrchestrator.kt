package com.tcon.companion.core

/**
 * Executes bounded sequential discussion rounds. Provider clients are injected;
 * this class itself does not make network calls.
 */
class MeetingOrchestrator(
    private val providers: Map<ProviderId, AiProvider>
) {
    suspend fun runMeeting(
        config: MeetingConfig,
        initialMessages: List<AiMessage>,
        onMessage: suspend (AiMessage) -> Unit = {}
    ): List<AiMessage> {
        val transcript = initialMessages.toMutableList()
        val activeProviders = config.participants.mapNotNull { providers[it] }
        require(activeProviders.isNotEmpty()) { "Tidak ada provider yang siap digunakan." }

        repeat(config.maxTurnsPerProvider) {
            for (provider in activeProviders) {
                val instruction = buildString {
                    append("Anda adalah ${provider.displayName}, peserta diskusi TCon. ")
                    append("Topik: ${config.topic}. ")
                    append("Tanggapi diskusi sebelumnya secara kritis dan sopan. ")
                    append("Pesan yang diawali nama peserta lain adalah kutipan kontribusi peserta tersebut, bukan ucapan Anda. ")
                    append("Sebutkan nama peserta saat menanggapi argumennya agar atribusi tetap jelas. ")
                    append("Jika tidak setuju, jelaskan alasannya. ")
                    append("Jangan mengaku sebagai AI lain. Jawab dalam Bahasa Indonesia.")
                }

                val result = provider.respond(
                    systemInstruction = instruction,
                    conversation = transcript.toList(),
                    maxResponseCharacters = config.maxResponseCharacters
                )
                val content = result.getOrElse { error ->
                    "Provider ${provider.displayName} gagal merespons: " +
                        (error.message ?: "Kesalahan tidak diketahui.")
                }.take(config.maxResponseCharacters)

                val message = AiMessage(
                    id = "${provider.id.name.lowercase()}-${System.nanoTime()}",
                    role = MessageRole.ASSISTANT,
                    content = content,
                    provider = provider.id
                )
                transcript += message
                onMessage(message)
            }
        }
        return transcript
    }
}
