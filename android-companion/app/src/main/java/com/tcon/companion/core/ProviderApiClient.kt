package com.tcon.companion.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Optional official-API adapter. Requires a provider API key and may incur charges.
 * Model names are constructor parameters so they can be changed without code edits.
 */
class ProviderApiClient(
    override val id: ProviderId,
    private val apiKey: () -> String?,
    private val model: String,
    override val displayName: String = when (id) {
        ProviderId.OPENAI -> "GPT"
        ProviderId.GEMINI -> "Gemini"
        ProviderId.CLAUDE -> "Claude"
    }
) : AiProvider {
    override val capabilities = ProviderCapabilities(textInput = true, streaming = false)

    override suspend fun respond(
        systemInstruction: String,
        conversation: List<AiMessage>,
        maxResponseCharacters: Int
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val secret = apiKey()?.takeIf { it.isNotBlank() }
                ?: error("API key untuk $displayName belum disimpan.")
            val (endpoint, body, headers) = requestFor(secret, systemInstruction, conversation)
            val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 20_000
                readTimeout = 60_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                headers.forEach { (name, value) -> setRequestProperty(name, value) }
            }
            try {
                connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
                val status = connection.responseCode
                val stream = if (status in 200..299) connection.inputStream else connection.errorStream
                val responseText = stream?.use { input ->
                    BufferedReader(InputStreamReader(input, Charsets.UTF_8)).readText()
                }.orEmpty()
                if (status !in 200..299) {
                    val detail = runCatching { JSONObject(responseText).optJSONObject("error")?.optString("message") }.getOrNull()
                    error("$displayName API HTTP $status: ${detail?.takeIf { it.isNotBlank() } ?: responseText.take(500)}")
                }
                extractText(JSONObject(responseText)).trim().take(maxResponseCharacters)
                    .ifBlank { error("$displayName mengembalikan respons kosong.") }
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun requestFor(
        secret: String,
        systemInstruction: String,
        conversation: List<AiMessage>
    ): Triple<String, JSONObject, Map<String, String>> {
        // Each provider must see its own earlier turns as assistant messages,
        // while peer-AI contributions are presented as attributed user context.
        // This prevents one model from mistaking another model's words for its own.
        val turns = conversation.filter { it.role != MessageRole.SYSTEM }
        fun roleFor(message: AiMessage, assistantRole: String, userRole: String) =
            if (message.provider == id) assistantRole else userRole
        fun contentFor(message: AiMessage) =
            if (message.provider == null) message.content
            else "[${providerLabel(message.provider)} berkata]:\n${message.content}"
        return when (id) {
            ProviderId.OPENAI -> {
                val input = JSONArray()
                turns.forEach { message ->
                    input.put(JSONObject()
                        .put("role", roleFor(message, "assistant", "user"))
                        .put("content", contentFor(message)))
                }
                Triple(
                    "https://api.openai.com/v1/responses",
                    JSONObject().put("model", model).put("instructions", systemInstruction).put("input", input),
                    mapOf("Authorization" to "Bearer $secret")
                )
            }
            ProviderId.GEMINI -> {
                val contents = JSONArray()
                turns.forEach { message ->
                    contents.put(JSONObject()
                        .put("role", roleFor(message, "model", "user"))
                        .put("parts", JSONArray().put(JSONObject().put("text", contentFor(message)))))
                }
                Triple(
                    "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent",
                    JSONObject()
                        .put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemInstruction))))
                        .put("contents", contents),
                    mapOf("x-goog-api-key" to secret)
                )
            }
            ProviderId.CLAUDE -> {
                val messages = JSONArray()
                turns.forEach { message ->
                    messages.put(JSONObject()
                        .put("role", roleFor(message, "assistant", "user"))
                        .put("content", contentFor(message)))
                }
                Triple(
                    "https://api.anthropic.com/v1/messages",
                    JSONObject().put("model", model).put("max_tokens", 1200)
                        .put("system", systemInstruction).put("messages", messages),
                    mapOf("x-api-key" to secret, "anthropic-version" to "2023-06-01")
                )
            }
        }
    }

    private fun providerLabel(provider: ProviderId): String = when (provider) {
        ProviderId.OPENAI -> "GPT"
        ProviderId.GEMINI -> "Gemini"
        ProviderId.CLAUDE -> "Claude"
    }

    private fun extractText(json: JSONObject): String = when (id) {
        ProviderId.OPENAI -> {
            val output = json.optJSONArray("output") ?: JSONArray()
            buildString {
                for (i in 0 until output.length()) {
                    val content = output.optJSONObject(i)?.optJSONArray("content") ?: continue
                    for (j in 0 until content.length()) {
                        val item = content.optJSONObject(j) ?: continue
                        if (item.optString("type") == "output_text") append(item.optString("text"))
                    }
                }
            }
        }
        ProviderId.GEMINI -> {
            val candidates = json.optJSONArray("candidates") ?: JSONArray()
            val parts = candidates.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts") ?: JSONArray()
            buildString {
                for (i in 0 until parts.length()) append(parts.optJSONObject(i)?.optString("text").orEmpty())
            }
        }
        ProviderId.CLAUDE -> {
            val content = json.optJSONArray("content") ?: JSONArray()
            buildString {
                for (i in 0 until content.length()) {
                    val item = content.optJSONObject(i) ?: continue
                    if (item.optString("type") == "text") append(item.optString("text"))
                }
            }
        }
    }
}
