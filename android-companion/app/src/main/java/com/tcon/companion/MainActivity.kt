package com.tcon.companion

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var promptInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 48, 36, 36)
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

        root.addView(TextView(this).apply {
            text = "AI WORKSPACE"
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
        })

        addButton(root, "Buka ChatGPT") { openApp(CHATGPT_PACKAGE, "ChatGPT") }
        addButton(root, "Buka Gemini") { openApp(GEMINI_PACKAGE, "Gemini") }
        addButton(root, "Buka Claude") { openApp(CLAUDE_PACKAGE, "Claude") }

        root.addView(TextView(this).apply {
            text = "INSTRUKSI / HANDOFF"
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 24, 0, 8)
        })

        promptInput = EditText(this).apply {
            hint = "Tulis instruksi atau hasil yang ingin dibagikan ke AI"
            minLines = 5
            gravity = Gravity.TOP or Gravity.START
            setText(getPreferences(MODE_PRIVATE).getString(KEY_PROMPT, ""))
        }
        root.addView(promptInput, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        addButton(root, "Bagikan instruksi ke aplikasi AI") { sharePrompt() }

        root.addView(TextView(this).apply {
            text = "Status: fondasi handoff. TCon belum membaca percakapan AI atau menjalankan diskusi otomatis. Pengiriman tetap dikonfirmasi oleh Anda."
            textSize = 12f
            setPadding(0, 20, 0, 0)
        })

        setContentView(root)
    }

    override fun onPause() {
        getPreferences(MODE_PRIVATE).edit()
            .putString(KEY_PROMPT, promptInput.text.toString())
            .apply()
        super.onPause()
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

    private fun sharePrompt() {
        val prompt = promptInput.text.toString().trim()
        if (prompt.isEmpty()) {
            Toast.makeText(this, "Tulis instruksi terlebih dahulu.", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "$appName belum terpasang atau tidak tersedia.", Toast.LENGTH_LONG).show()
            } else {
                startActivity(launchIntent)
            }
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "$appName tidak dapat dibuka.", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val KEY_PROMPT = "saved_prompt"
        private const val CHATGPT_PACKAGE = "com.openai.chatgpt"
        private const val GEMINI_PACKAGE = "com.google.android.apps.bard"
        private const val CLAUDE_PACKAGE = "com.anthropic.claude"
    }
}
