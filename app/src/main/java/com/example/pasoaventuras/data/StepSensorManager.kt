package com.example.pasoaventuras.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class StepSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    // Preferir detector de paso (1 evento = 1 paso exacto), sino contador, sino acelerómetro
    private val stepDetector: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    private val stepCounter: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var sessionStartCounter = -1L
    private var lastCounterVal = -1L

    // Variables para algoritmo de respaldo con acelerómetro
    private var lastAccelMagnitude = 0f
    private var lastStepTimestamp = 0L

    val isAvailable: Boolean 
        get() = stepDetector != null || stepCounter != null || accelerometer != null

    fun startListening() {
        var registered = false

        if (stepDetector != null) {
            registered = sensorManager.registerListener(
                this, 
                stepDetector, 
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        if (!registered && stepCounter != null) {
            registered = sensorManager.registerListener(
                this, 
                stepCounter, 
                SensorManager.SENSOR_DELAY_UI
            )
        }

        // Si el teléfono no tiene podómetro de hardware dedicado, usar acelerómetro
        if (!registered && accelerometer != null) {
            registered = sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        AppState.isSensorAvailable = registered
        AppState.isWalking = registered
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
        AppState.isWalking = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_STEP_DETECTOR -> {
                // Cada evento representa exactamente 1 paso detectado
                val stepsCount = event.values[0].toInt()
                if (stepsCount > 0) {
                    AppState.addSteps(stepsCount)
                }
            }

            Sensor.TYPE_STEP_COUNTER -> {
                val currentTotal = event.values[0].toLong()
                if (sessionStartCounter < 0) {
                    sessionStartCounter = currentTotal
                    lastCounterVal = currentTotal
                    return
                }
                val delta = currentTotal - lastCounterVal
                if (delta in 1..100) {
                    AppState.addSteps(delta.toInt())
                }
                lastCounterVal = currentTotal
            }

            Sensor.TYPE_ACCELEROMETER -> {
                // Algoritmo de detección de picos de aceleración (reserva universal)
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

                val deltaMagnitude = magnitude - lastAccelMagnitude
                lastAccelMagnitude = magnitude

                val now = System.currentTimeMillis()
                // Umbral dinámico para evitar falsos positivos y rebotes (< 300 ms entre pasos)
                if (deltaMagnitude > 3.2f && (now - lastStepTimestamp) > 320) {
                    lastStepTimestamp = now
                    AppState.addSteps(1)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
