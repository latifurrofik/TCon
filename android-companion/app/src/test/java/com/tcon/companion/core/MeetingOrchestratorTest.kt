package com.tcon.companion.core

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MeetingOrchestratorTest {
    private class FakeProvider(
        override val id: ProviderId,
        override val displayName: String
    ) : AiProvider {
        override val capabilities = ProviderCapabilities()
        val receivedTranscriptSizes = mutableListOf<Int>()
        override suspend fun respond(systemInstruction: String, conversation: List<AiMessage>, maxResponseCharacters: Int): Result<String> {
            receivedTranscriptSizes += conversation.size
            return Result.success("$displayName response")
        }
    }

    @Test fun participantsRunInOrderAndReceivePreviousTurns() = runBlocking {
        val gpt = FakeProvider(ProviderId.OPENAI, "GPT")
        val gemini = FakeProvider(ProviderId.GEMINI, "Gemini")
        val claude = FakeProvider(ProviderId.CLAUDE, "Claude")
        val result = MeetingOrchestrator(mapOf(
            ProviderId.OPENAI to gpt, ProviderId.GEMINI to gemini, ProviderId.CLAUDE to claude
        )).runMeeting(
            MeetingConfig("Rencana proyek", listOf(ProviderId.OPENAI, ProviderId.GEMINI, ProviderId.CLAUDE)),
            listOf(AiMessage("user-1", MessageRole.USER, "Buat rencana."))
        )
        assertEquals(4, result.size)
        assertEquals(listOf(ProviderId.OPENAI, ProviderId.GEMINI, ProviderId.CLAUDE), result.drop(1).map { it.provider })
        assertEquals(listOf(1), gpt.receivedTranscriptSizes)
        assertEquals(listOf(2), gemini.receivedTranscriptSizes)
        assertEquals(listOf(3), claude.receivedTranscriptSizes)
    }

    @Test fun limitsResponseLengthAndTurnCount() = runBlocking {
        val gpt = object : AiProvider {
            override val id = ProviderId.OPENAI
            override val displayName = "GPT"
            override val capabilities = ProviderCapabilities()
            var calls = 0
            override suspend fun respond(systemInstruction: String, conversation: List<AiMessage>, maxResponseCharacters: Int): Result<String> {
                calls++
                return Result.success("x".repeat(500))
            }
        }
        val result = MeetingOrchestrator(mapOf(ProviderId.OPENAI to gpt)).runMeeting(
            MeetingConfig("Uji", listOf(ProviderId.OPENAI), maxTurnsPerProvider = 2, maxResponseCharacters = 120), emptyList()
        )
        assertEquals(2, gpt.calls)
        assertTrue(result.all { it.content.length <= 120 })
    }

    @Test fun recordsProviderFailuresWithoutAbortingMeeting() = runBlocking {
        val broken = object : AiProvider {
            override val id = ProviderId.CLAUDE
            override val displayName = "Claude"
            override val capabilities = ProviderCapabilities()
            override suspend fun respond(systemInstruction: String, conversation: List<AiMessage>, maxResponseCharacters: Int) =
                Result.failure<String>(IllegalStateException("offline"))
        }
        val result = MeetingOrchestrator(mapOf(ProviderId.CLAUDE to broken)).runMeeting(
            MeetingConfig("Uji", listOf(ProviderId.CLAUDE)), emptyList()
        )
        assertTrue(result.single().content.contains("offline"))
    }
}
