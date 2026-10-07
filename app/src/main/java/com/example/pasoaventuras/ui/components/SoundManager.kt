package com.example.pasoaventuras.ui.components

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.pasoaventuras.data.AppState
import kotlin.concurrent.thread
import kotlin.math.sin

object SoundManager {

    private fun playPcm(sampleRate: Int = 44100, generateSamples: () -> ShortArray) {
        if (!AppState.audioEnabled) return
        thread {
            try {
                val samples = generateSamples()
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = maxOf(minBufferSize, samples.size * 2)

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
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(samples, 0, samples.size)
                audioTrack.play()
                Thread.sleep(600)
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }

    // playBeep('click'): frecuencia 600 Hz por 80ms
    fun playClick() {
        playPcm {
            val sampleRate = 44100
            val duration = 0.08
            val numSamples = (sampleRate * duration).toInt()
            val samples = ShortArray(numSamples)
            val freq = 600.0

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val decay = 1.0 - (i.toDouble() / numSamples)
                val s = sin(2.0 * Math.PI * freq * t) * decay * 0.3
                samples[i] = (s * Short.MAX_VALUE).toInt().toShort()
            }
            samples
        }
    }

    // playBeep('coin'): B5 (987.77 Hz) subiendo a E6 (1318.51 Hz) por 350ms
    fun playCoin() {
        playPcm {
            val sampleRate = 44100
            val duration = 0.35
            val numSamples = (sampleRate * duration).toInt()
            val samples = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = if (t < 0.12) {
                    987.77 + (1318.51 - 987.77) * (t / 0.12)
                } else {
                    1318.51
                }
                val decay = Math.exp(-t * 9.0)
                val s = sin(2.0 * Math.PI * freq * t) * decay * 0.5
                samples[i] = (s * Short.MAX_VALUE).toInt().toShort()
            }
            samples
        }
    }

    // playBeep('step'): de 300 Hz a 150 Hz rápido (60ms)
    fun playStep() {
        playPcm {
            val sampleRate = 44100
            val duration = 0.06
            val numSamples = (sampleRate * duration).toInt()
            val samples = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = 300.0 - (150.0 * (t / duration))
                val decay = 1.0 - (t / duration)
                val s = sin(2.0 * Math.PI * freq * t) * decay * 0.35
                samples[i] = (s * Short.MAX_VALUE).toInt().toShort()
            }
            samples
        }
    }

    // playBeep('legendary'): 4 tonos ascendentes estilo arpegio fanfarria
    fun playLegendary() {
        playPcm {
            val sampleRate = 44100
            val duration = 0.8
            val numSamples = (sampleRate * duration).toInt()
            val samples = ShortArray(numSamples)
            val freqs = listOf(523.25, 659.25, 783.99, 1046.50)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val noteIdx = ((t / 0.8) * freqs.size).toInt().coerceIn(0, freqs.size - 1)
                val freq = freqs[noteIdx]
                val decay = Math.exp(-t * 2.5)
                val s = sin(2.0 * Math.PI * freq * t) * decay * 0.5
                samples[i] = (s * Short.MAX_VALUE).toInt().toShort()
            }
            samples
        }
    }
}
