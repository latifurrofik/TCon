package com.tcon.companion

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.tcon.companion.core.AiMessage
import com.tcon.companion.core.ApiKeyVault
import com.tcon.companion.core.MeetingConfig
import com.tcon.companion.core.MeetingOrchestrator
import com.tcon.companion.core.MessageRole
import com.tcon.companion.core.ProviderApiClient
import com.tcon.companion.core.ProviderId
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var promptInput: EditText
    private lateinit var transcriptView: TextView
    private lateinit var apiKeyVault: ApiKeyVault
    private lateinit var activityScope: CoroutineScope
    private var meetingJob: Job? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var ttsReady = false
    private var pendingVoiceStart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiKeyVault = ApiKeyVault(this)
        activityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        initializeTextToSpeech()
        initializeSpeechRecognizer()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 40, 36, 36)
            gravity = Gravity.TOP
        }
        root.addView(TextView(this).apply {
            text = "TCon"
            textSize = 30f
            setTypeface(null, Typeface.BOLD)
        })
        root.addView(TextView(this).apply {
            text = "Ruang kerja kolaborasi GPT, Gemini, dan Claude"
            textSize = 15f
            setPadding(0, 8, 0, 20)
        })

        addSection(root, "APLIKASI AI")
        addButton(root, "Buka ChatGPT") { openApp(CHATGPT_PACKAGE, "ChatGPT") }
        addButton(root, "Buka Gemini") { openApp(GEMINI_PACKAGE, "Gemini") }
        addButton(root, "Buka Claude") { openApp(CLAUDE_PACKAGE, "Claude") }

        addSection(root, "KONEKSI API OPSIONAL")
        addButton(root, "Atur koneksi GPT") {
            configureProvider(ProviderId.OPENAI, "GPT", "Contoh: gpt-4.1-mini")
        }
        addButton(root, "Atur koneksi Gemini") {
            configureProvider(ProviderId.GEMINI, "Gemini", "Contoh: gemini-2.5-flash")
        }
        addButton(root, "Atur koneksi Claude") {
            configureProvider(ProviderId.CLAUDE, "Claude", "Masukkan ID model dari Anthropic")
        }
        root.addView(TextView(this).apply {
            text = "Koneksi API terpisah dari langganan aplikasi AI dan dapat menimbulkan biaya. API key disimpan terenkripsi menggunakan Android Keystore. Jangan masukkan password akun aplikasi."
            textSize = 12f
            setPadding(0, 4, 0, 12)
        })

        addSection(root, "DISKUSI MULTI-AI")
        promptInput = EditText(this).apply {
            hint = "Tulis topik atau instruksi diskusi"
            minLines = 3
            gravity = Gravity.TOP or Gravity.START
            setText(getPreferences(MODE_PRIVATE).getString(KEY_PROMPT, ""))
        }
        root.addView(promptInput, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        addButton(root, "Bagikan instruksi ke aplikasi AI") { sharePrompt() }
        addButton(root, "Input instruksi dengan suara") { startVoiceInput() }
        addButton(root, "Hentikan input / suara AI") { stopVoiceFeatures() }
        addButton(root, "Mulai diskusi dengan AI yang terhubung") { confirmAndStartMeeting() }
        addButton(root, "Hentikan diskusi") {
            meetingJob?.cancel()
            meetingJob = null
            appendTranscript("TCon", "Diskusi dihentikan oleh pengguna.")
        }

        addSection(root, "TRANSKRIP")
        transcriptView = TextView(this).apply {
            text = "Belum ada diskusi."
            textSize = 14f
            setTextIsSelectable(true)
            setPadding(12, 12, 12, 12)
        }
        root.addView(transcriptView, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        root.addView(TextView(this).apply {
            text = "Mode handoff aplikasi resmi tidak membaca jawaban dari aplikasi lain secara otomatis. Diskusi otomatis menggunakan API resmi jika kamu mengonfigurasi API key dan model masing-masing."
            textSize = 12f
            setPadding(0, 24, 0, 8)
        })

        val scroll = ScrollView(this).apply { addView(root) }
        setContentView(scroll)
    }

    override fun onPause() {
        if (::promptInput.isInitialized) {
            getPreferences(MODE_PRIVATE).edit()
                .putString(KEY_PROMPT, promptInput.text.toString())
                .apply()
        }
        super.onPause()
    }

    override fun onDestroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        activityScope.cancel()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED && pendingVoiceStart) {
                pendingVoiceStart = false
                startVoiceInput()
            } else {
                pendingVoiceStart = false
                toast("Izin mikrofon tidak diberikan.")
            }
        }
    }

    private fun initializeSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            toast("Layanan pengenalan suara tidak tersedia di perangkat ini.")
            return
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) = toast("Silakan bicara.")
                override fun onBeginningOfSpeech() = Unit
                override fun onRmsChanged(rmsdB: Float) = Unit
                override fun onBufferReceived(buffer: ByteArray?) = Unit
                override fun onEndOfSpeech() = Unit
                override fun onError(error: Int) = toast("Input suara berhenti. Coba lagi.")
                override fun onResults(results: Bundle?) {
                    val spoken = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                    if (!spoken.isNullOrBlank()) {
                        promptInput.setText(spoken)
                        promptInput.setSelection(promptInput.text.length)
                        toast("Instruksi suara berhasil ditranskripsikan.")
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) = Unit
                override fun onEvent(eventType: Int, params: Bundle?) = Unit
            })
        }
    }

    private fun startVoiceInput() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            pendingVoiceStart = true
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO)
            return
        }
        val recognizer = speechRecognizer
        if (recognizer == null) {
            toast("Pengenalan suara belum tersedia.")
            return
        }
        textToSpeech?.stop()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "id-ID")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        runCatching { recognizer.startListening(intent) }
            .onFailure { toast("Tidak dapat memulai mikrofon: ${it.message}") }
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val languageStatus = textToSpeech?.setLanguage(Locale("id", "ID"))
                ttsReady = languageStatus != TextToSpeech.LANG_MISSING_DATA &&
                    languageStatus != TextToSpeech.LANG_NOT_SUPPORTED
                if (!ttsReady) toast("Suara Bahasa Indonesia belum tersedia. Periksa mesin TTS Android.")
            } else {
                toast("Mesin text-to-speech gagal diinisialisasi.")
            }
        }
    }

    private fun speakAiResponse(provider: ProviderId?, text: String) {
        val tts = textToSpeech ?: return
        if (!ttsReady) return
        val pitch = when (provider) {
            ProviderId.OPENAI -> 1.0f
            ProviderId.GEMINI -> 1.12f
            ProviderId.CLAUDE -> 0.9f
            null -> 1.0f
        }
        tts.setPitch(pitch)
        tts.setSpeechRate(1.0f)
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, "tcon-${provider?.name ?: "ai"}-${System.currentTimeMillis()}")
    }

    private fun stopVoiceFeatures() {
        speechRecognizer?.cancel()
        textToSpeech?.stop()
        toast("Input dan output suara dihentikan.")
    }

    private fun addSection(parent: LinearLayout, label: String) {
        parent.addView(TextView(this).apply {
            text = label
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 22, 0, 8)
        })
    }

    private fun addButton(parent: LinearLayout, label: String, action: () -> Unit) {
        val button = Button(this).apply {
            text = label
            setOnClickListener { action() }
        }
        parent.addView(button, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ))
    }

    private fun configureProvider(provider: ProviderId, name: String, modelHint: String) {
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 8, 36, 0)
        }
        val modelInput = EditText(this).apply {
            hint = modelHint
            setSingleLine(true)
            setText(getPreferences(MODE_PRIVATE).getString(modelKey(provider), ""))
        }
        val keyInput = EditText(this).apply {
            hint = if (apiKeyVault.read(provider) == null) "API key $name" else "API key sudah tersimpan; isi hanya untuk mengganti"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setSingleLine(true)
        }
        form.addView(modelInput)
        form.addView(keyInput)

        AlertDialog.Builder(this)
            .setTitle("Koneksi $name")
            .setMessage("Masukkan ID model dan API key resmi. Penggunaan API dapat dikenai biaya oleh penyedia.")
            .setView(form)
            .setPositiveButton("Simpan") { _, _ ->
                val model = modelInput.text.toString().trim()
                val key = keyInput.text.toString().trim()
                if (model.isBlank()) {
                    toast("ID model wajib diisi.")
                    return@setPositiveButton
                }
                if (key.isNotBlank()) apiKeyVault.save(provider, key)
                if (apiKeyVault.read(provider) == null) {
                    toast("API key belum tersimpan. Isi API key terlebih dahulu.")
                    return@setPositiveButton
                }
                getPreferences(MODE_PRIVATE).edit().putString(modelKey(provider), model).apply()
                toast("Koneksi $name disimpan.")
            }
            .setNeutralButton("Hapus API key") { _, _ ->
                apiKeyVault.delete(provider)
                toast("API key $name dihapus.")
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun confirmAndStartMeeting() {
        val topic = promptInput.text.toString().trim()
        if (topic.isBlank()) {
            toast("Tulis topik diskusi terlebih dahulu.")
            return
        }
        val configured = ProviderId.values().mapNotNull { provider ->
            val model = getPreferences(MODE_PRIVATE).getString(modelKey(provider), "").orEmpty()
            val key = apiKeyVault.read(provider)
            if (model.isNotBlank() && !key.isNullOrBlank()) {
                provider to ProviderApiClient(provider, { apiKeyVault.read(provider) }, model)
            } else null
        }.toMap()

        if (configured.isEmpty()) {
            toast("Belum ada AI API yang dikonfigurasi. Atur minimal satu koneksi API.")
            return
        }

        val names = configured.keys.joinToString { providerName(it) }
        AlertDialog.Builder(this)
            .setTitle("Mulai diskusi multi-AI?")
            .setMessage("AI yang aktif: $names. Permintaan API dapat menggunakan kuota dan menimbulkan biaya. Lanjutkan?")
            .setPositiveButton("Mulai") { _, _ -> startMeeting(topic, configured) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun startMeeting(topic: String, providers: Map<ProviderId, ProviderApiClient>) {
        meetingJob?.cancel()
        transcriptView.text = ""
        appendTranscript("Kamu", topic)
        val initial = listOf(AiMessage(
            id = "user-${System.currentTimeMillis()}",
            role = MessageRole.USER,
            content = topic
        ))
        val orchestrator = MeetingOrchestrator(providers)
        meetingJob = activityScope.launch {
            appendTranscript("TCon", "Diskusi dimulai. Urutan: ${providers.keys.joinToString { providerName(it) }}")
            try {
                orchestrator.runMeeting(
                    config = MeetingConfig(
                        topic = topic,
                        participants = providers.keys.toList(),
                        maxTurnsPerProvider = 1,
                        maxResponseCharacters = 1400
                    ),
                    initialMessages = initial
                ) { message ->
                    appendTranscript(providerName(message.provider), message.content)
                    speakAiResponse(message.provider, message.content)
                }
                appendTranscript("TCon", "Diskusi selesai.")
            } catch (cancelled: CancellationException) {
                appendTranscript("TCon", "Diskusi dibatalkan.")
                throw cancelled
            } catch (error: Exception) {
                appendTranscript("TCon", "Terjadi kesalahan: ${error.message ?: "tidak diketahui"}")
            }
        }
    }

    private fun appendTranscript(speaker: String?, message: String) {
        if (!::transcriptView.isInitialized) return
        val label = speaker ?: "AI"
        if (transcriptView.text.toString() == "Belum ada diskusi.") transcriptView.text = ""
        transcriptView.append("\n\n$label\n$message")
    }

    private fun sharePrompt() {
        val prompt = promptInput.text.toString().trim()
        if (prompt.isEmpty()) {
            toast("Tulis instruksi terlebih dahulu.")
            return
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, prompt)
        }
        startActivity(Intent.createChooser(intent, "Pilih aplikasi AI tujuan"))
    }

    private fun openApp(packageName: String, appName: String) {
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent == null) {
                toast("$appName belum terpasang atau tidak tersedia.")
            } else startActivity(launchIntent)
        } catch (_: ActivityNotFoundException) {
            toast("$appName tidak dapat dibuka.")
        }
    }

    private fun providerName(provider: ProviderId?): String = when (provider) {
        ProviderId.OPENAI -> "GPT"
        ProviderId.GEMINI -> "Gemini"
        ProviderId.CLAUDE -> "Claude"
        null -> "AI"
    }

    private fun modelKey(provider: ProviderId) = "model_${provider.name.lowercase()}"

    private fun toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    companion object {
        private const val KEY_PROMPT = "saved_prompt"
        private const val REQUEST_RECORD_AUDIO = 4107
        private const val CHATGPT_PACKAGE = "com.openai.chatgpt"
        private const val GEMINI_PACKAGE = "com.google.android.apps.bard"
        private const val CLAUDE_PACKAGE = "com.anthropic.claude"
    }
}
