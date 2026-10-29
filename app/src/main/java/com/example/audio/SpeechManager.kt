package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechManager(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingSpeech: String? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speechStatus = MutableStateFlow("")
    val speechStatus: StateFlow<String> = _speechStatus.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.ENGLISH)
                }
                tts?.setSpeechRate(0.92f)
                tts?.setPitch(1.0f)
                isInitialized = true

                // Execute any speech requested while TTS was initializing
                pendingSpeech?.let { text ->
                    speak(text)
                    pendingSpeech = null
                }
            }
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
                _speechStatus.value = "Reading in English..."
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
                _speechStatus.value = ""
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
                _speechStatus.value = ""
            }
        })
    }

    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate.coerceIn(0.5f, 2.0f))
    }

    fun setLanguage(locale: Locale) {
        tts?.setLanguage(locale)
    }

    fun speakRiddle(
        riddleNumber: Int,
        question: String,
        options: List<String> = emptyList(),
        includeOptions: Boolean = true
    ) {
        val builder = StringBuilder()
        builder.append("Riddle number ").append(riddleNumber).append(". ")
        builder.append(question).append(". ")

        if (includeOptions && options.isNotEmpty()) {
            builder.append("Here are your choices: ")
            val letters = listOf("A", "B", "C", "D")
            options.forEachIndexed { index, option ->
                val label = letters.getOrElse(index) { "${index + 1}" }
                builder.append("Option ").append(label).append(": ").append(option).append(". ")
            }
        }

        speak(builder.toString())
    }

    fun speak(text: String) {
        if (!isInitialized) {
            pendingSpeech = text
            return
        }
        if (_isSpeaking.value) {
            stop()
        } else {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "RIDDLE_ENGLISH_SPEECH")
            _isSpeaking.value = true
        }
    }

    fun speakOption(optionLetter: String, optionText: String) {
        speak("Option $optionLetter: $optionText")
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        _speechStatus.value = ""
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
