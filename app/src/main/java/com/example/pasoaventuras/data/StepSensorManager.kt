package com.example.pasoaventuras.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs
import kotlin.math.sqrt

class StepSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // Sensores de podómetro nativos (por hardware)
    private val stepDetector: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    private val stepCounter: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var activeSensorType: Int = 0

    // Para TYPE_STEP_COUNTER
    private var sessionStartCounter = -1L
    private var lastCounterVal = -1L

    // Para acelerómetro: Filtro de paso alto y orientación invariante
    private var gravity = FloatArray(3)
    private var linearAcceleration = FloatArray(3)
    private var lastLinearMag = 0f
    private var isPeak = false
    private var lastStepTimestamp = 0L

    // true si el dispositivo tiene chip de podometro dedicado (no solo acelerometro)
    val hasHardwarePedometer: Boolean
        get() = stepDetector != null || stepCounter != null

    val isAvailable: Boolean 
        get() = stepDetector != null || stepCounter != null || accelerometer != null

    fun startListening() {
        var registered = false

        // 1. PRIORIDAD MÁXIMA: Hardware Step Detector (chip físico dedicado)
        if (stepDetector != null) {
            try {
                registered = sensorManager.registerListener(
                    this, 
                    stepDetector, 
                    SensorManager.SENSOR_DELAY_UI
                )
                if (registered) activeSensorType = Sensor.TYPE_STEP_DETECTOR
            } catch (_: Exception) {
                registered = false
            }
        }

        // 2. SEGUNDA PRIORIDAD: Hardware Step Counter (acumulador por chip físico)
        if (!registered && stepCounter != null) {
            try {
                registered = sensorManager.registerListener(
                    this, 
                    stepCounter, 
                    SensorManager.SENSOR_DELAY_UI
                )
                if (registered) activeSensorType = Sensor.TYPE_STEP_COUNTER
            } catch (_: Exception) {
                registered = false
            }
        }

        // 3. TERCERA PRIORIDAD: Acelerómetro universal (funciona en tablets y cualquier dispositivo sin requerir permiso de actividad)
        if (!registered && accelerometer != null) {
            try {
                registered = sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_UI
                )
                if (registered) activeSensorType = Sensor.TYPE_ACCELEROMETER
            } catch (_: Exception) {
                registered = false
            }
        }

        AppState.isSensorAvailable = registered
        AppState.hasHardwarePedometer = activeSensorType == Sensor.TYPE_STEP_DETECTOR ||
                activeSensorType == Sensor.TYPE_STEP_COUNTER
        AppState.isWalking = registered
    }

    fun stopListening() {
        try {
            sensorManager.unregisterListener(this)
        } catch (_: Exception) {}
        AppState.isWalking = false
        sessionStartCounter = -1L
        lastCounterVal = -1L
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        // Solo procesamos el sensor activo principal para evitar contar doble
        if (event.sensor.type != activeSensorType) return

        when (event.sensor.type) {
            Sensor.TYPE_STEP_DETECTOR -> {
                // El procesador del teléfono ya validó que es un paso real (no se deja engañar por agitar)
                val count = event.values[0].toInt()
                if (count > 0) {
                    AppState.addSteps(count)
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
                if (delta in 1..25) {
                    AppState.addSteps(delta.toInt())
                }
                lastCounterVal = currentTotal
            }

            Sensor.TYPE_ACCELEROMETER -> {
                // Algoritmo aislado de orientación:
                // Paso 1: Filtro pasa-bajas para aislar la gravedad (9.8 m/s²) sin importar si está horizontal o vertical
                val alpha = 0.8f
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

                // Paso 2: Restar gravedad para obtener aceleración lineal pura
                linearAcceleration[0] = event.values[0] - gravity[0]
                linearAcceleration[1] = event.values[1] - gravity[1]
                linearAcceleration[2] = event.values[2] - gravity[2]

                // Paso 3: Magnitud escalar (es idéntica en horizontal, vertical o bolsillo)
                val lx = linearAcceleration[0]
                val ly = linearAcceleration[1]
                val lz = linearAcceleration[2]
                val currentMag = sqrt((lx * lx + ly * ly + lz * lz).toDouble()).toFloat()

                val now = System.currentTimeMillis()

                // Paso 4: Detección de paso humano con patrón pico-valle:
                // Una pisada real genera entre 1.8 y 7.0 m/s². Agitar el dispositivo genera picos erráticos > 8.0 o continuos.
                if (currentMag > 1.8f && currentMag < 7.5f) {
                    if (currentMag > lastLinearMag) {
                        isPeak = true
                    } else if (isPeak && (lastLinearMag - currentMag) > 0.35f) {
                        isPeak = false
                        // Una zancada humana al caminar toma entre 360 ms y 1300 ms
                        val elapsed = now - lastStepTimestamp
                        if (elapsed in 360..1300) {
                            lastStepTimestamp = now
                            AppState.addSteps(1)
                        } else if (elapsed > 1300) {
                            // Primer paso después de estar detenido
                            lastStepTimestamp = now
                            AppState.addSteps(1)
                        }
                    }
                } else if (currentMag >= 7.5f) {
                    // Si el movimiento es muy brusco (agitar con la mano rápido), se descarta
                    isPeak = false
                }

                lastLinearMag = currentMag
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
