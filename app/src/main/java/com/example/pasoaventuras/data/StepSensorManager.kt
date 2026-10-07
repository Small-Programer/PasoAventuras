package com.example.pasoaventuras.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepSensorManager(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private var initialStepOffset = -1

    val isAvailable: Boolean get() = stepSensor != null

    fun startListening() {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            AppState.isSensorAvailable = true
        } ?: run {
            AppState.isSensorAvailable = false
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()
            if (initialStepOffset < 0) {
                initialStepOffset = totalSteps
            }
            val stepsToday = totalSteps - initialStepOffset
            if (stepsToday >= 0) {
                AppState.steps = stepsToday
                AppState.xp = stepsToday
                AppState.coins = 5 + (stepsToday / 500)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
