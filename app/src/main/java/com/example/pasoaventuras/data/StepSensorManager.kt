package com.example.pasoaventuras.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    // Valor total del sensor en el arranque de la sesion (se establece al primer evento)
    private var sessionStartSteps = -1L

    // Ultima lectura del sensor para calcular el delta (pasos nuevos)
    private var lastSensorValue = -1L

    val isAvailable: Boolean get() = stepSensor != null

    fun startListening() {
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
            AppState.isSensorAvailable = true
            AppState.isWalking = true
        } else {
            AppState.isSensorAvailable = false
            AppState.isWalking = false
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
        AppState.isWalking = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_STEP_COUNTER) return

        val currentTotal = event.values[0].toLong()

        // Primera lectura: establecer el offset de inicio de sesion
        if (sessionStartSteps < 0) {
            sessionStartSteps = currentTotal
            lastSensorValue = currentTotal
            // Restaurar pasos guardados en Firestore como base del dia
            // (AppState.steps ya fue cargado desde Firebase, no lo sobreescribimos)
            return
        }

        // Calcular pasos nuevos desde la ultima lectura (delta = 1 por cada paso real)
        val delta = currentTotal - lastSensorValue
        lastSensorValue = currentTotal

        if (delta > 0) {
            AppState.addSteps(delta.toInt())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
