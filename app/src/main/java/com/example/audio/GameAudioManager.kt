package com.example.audio

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sin

class GameAudioManager(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var speechRecognizer: SpeechRecognizer? = null
    private val audioScope = CoroutineScope(Dispatchers.Default)

    var onTtsStart: (() -> Unit)? = null
    var onTtsDone: (() -> Unit)? = null
    var onSpeechRecognized: ((String) -> Unit)? = null

    init {
        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
                tts?.setPitch(1.35f) // Energetic anime character pitch
                tts?.setSpeechRate(1.1f) // Lively anime tempo
                val arabic = Locale("ar")
                val available = tts?.isLanguageAvailable(arabic)
                if (available != TextToSpeech.LANG_MISSING_DATA && available != TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.language = arabic
                } else {
                    tts?.language = Locale.US
                }
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        onTtsStart?.invoke()
                    }

                    override fun onDone(utteranceId: String?) {
                        onTtsDone?.invoke()
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        onTtsDone?.invoke()
                    }
                })
            }
        }
    }

    fun speak(text: String) {
        if (!isTtsReady || tts == null) return
        val isArabic = text.any { it in '\u0600'..'\u06FF' }
        if (isArabic) {
            tts?.language = Locale("ar")
        } else {
            tts?.language = Locale.US
        }
        tts?.setPitch(1.38f)
        tts?.setSpeechRate(1.08f)
        val params = Bundle()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "anzu_voice_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        tts?.stop()
        onTtsDone?.invoke()
    }

    fun startListening(onTextResult: (String) -> Unit) {
        this.onSpeechRecognized = onTextResult
        try {
            if (speechRecognizer == null) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            }
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ar")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "تحدث مع أنزو مازاكي...")
            }
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {
                    // Fallback phrases if microphone service error or not granted
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val heard = matches[0]
                        onSpeechRecognized?.invoke(heard)
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            // Ignore
        }
    }

    // Synthesized Sound Effects
    fun playPopSound() {
        audioScope.launch {
            generateTone(frequency = 750.0, durationMs = 60, volume = 0.4f)
        }
    }

    fun playCoinSound() {
        audioScope.launch {
            generateTone(frequency = 987.77, durationMs = 80, volume = 0.5f) // B5
            generateTone(frequency = 1318.51, durationMs = 150, volume = 0.55f) // E6
        }
    }

    fun playMunchSound() {
        audioScope.launch {
            for (i in 0..2) {
                generateTone(frequency = 420.0 - (i * 40), durationMs = 70, volume = 0.35f)
                kotlinx.coroutines.delay(40)
            }
        }
    }

    fun playCardFlipSound() {
        audioScope.launch {
            generateTone(frequency = 600.0, durationMs = 40, volume = 0.3f)
        }
    }

    fun playCardMatchSound() {
        audioScope.launch {
            generateTone(frequency = 523.25, durationMs = 80, volume = 0.4f) // C5
            generateTone(frequency = 659.25, durationMs = 80, volume = 0.45f) // E5
            generateTone(frequency = 783.99, durationMs = 80, volume = 0.5f) // G5
            generateTone(frequency = 1046.50, durationMs = 180, volume = 0.55f) // C6
        }
    }

    fun playRhythmHit(perfect: Boolean) {
        audioScope.launch {
            if (perfect) {
                generateTone(frequency = 880.0, durationMs = 80, volume = 0.5f) // A5
                generateTone(frequency = 1174.66, durationMs = 100, volume = 0.6f) // D6
            } else {
                generateTone(frequency = 659.25, durationMs = 70, volume = 0.4f) // E5
            }
        }
    }

    fun playDanceMelodyNote(step: Int) {
        audioScope.launch {
            val scale = listOf(523.25, 587.33, 659.25, 698.46, 783.99, 880.0, 987.77, 1046.50)
            val freq = scale[step % scale.size]
            generateTone(frequency = freq, durationMs = 120, volume = 0.45f)
        }
    }

    private fun generateTone(frequency: Double, durationMs: Int, volume: Float = 0.5f) {
        try {
            val sampleRate = 44100
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            if (numSamples <= 0) return
            val sample = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                // Sine wave with soft attack/decay envelope to prevent clicks
                val envelope = when {
                    i < sampleRate * 0.01 -> i / (sampleRate * 0.01)
                    i > numSamples - (sampleRate * 0.02) -> (numSamples - i) / (sampleRate * 0.02)
                    else -> 1.0
                }
                val wave = sin(2.0 * Math.PI * frequency * t) * envelope * volume
                sample[i] = (wave * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(sample.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(sample, 0, sample.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 10)
            audioTrack.release()
        } catch (e: Exception) {
            // AudioTrack release or creation fail safe
        }
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            // Ignore
        }
    }
}
