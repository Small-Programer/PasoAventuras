package com.example.pasoaventuras.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper

/**
 * Rastreador GPS complementario al podometro.
 * - Mide la distancia real recorrida (km) usando coordenadas satelitales.
 * - Detecta si el usuario va en vehiculo (velocidad > 20 km/h) para bloquear el conteo de pasos.
 * Los pasos SIEMPRE los cuenta el podometro; el GPS solo valida y mide distancia.
 */
class LocationTracker(context: Context) : LocationListener {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var lastLocation: Location? = null

    companion object {
        // Velocidad maxima considerada caminata/trote (km/h). Por encima se asume vehiculo.
        const val VEHICLE_SPEED_KMH = 20f
        // Precision minima aceptable de la lectura GPS (metros)
        private const val MAX_ACCURACY_METERS = 25f
        // Desplazamiento minimo para sumar distancia (filtra el "temblor" del GPS estando quieto)
        private const val MIN_DISTANCE_METERS = 4f
        // Saltos irreales entre lecturas (error de GPS)
        private const val MAX_JUMP_METERS = 150f
    }

    @SuppressLint("MissingPermission")
    fun start() {
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        try {
            if (gpsEnabled) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 3000L, 3f, this, Looper.getMainLooper()
                )
            }
            if (networkEnabled) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 5000L, 5f, this, Looper.getMainLooper()
                )
            }
            AppState.isGpsActive = gpsEnabled || networkEnabled
        } catch (e: SecurityException) {
            AppState.isGpsActive = false
        }
    }

    fun stop() {
        locationManager.removeUpdates(this)
        AppState.isGpsActive = false
        AppState.isInVehicle = false
        AppState.currentSpeedKmh = 0f
        lastLocation = null
    }

    override fun onLocationChanged(location: Location) {
        // Ignorar lecturas imprecisas
        if (location.hasAccuracy() && location.accuracy > MAX_ACCURACY_METERS) return

        val previous = lastLocation

        // Velocidad: usar la del GPS si existe, si no calcularla entre dos puntos
        val speedKmh = when {
            location.hasSpeed() -> location.speed * 3.6f
            previous != null -> {
                val seconds = (location.time - previous.time) / 1000f
                if (seconds > 0) previous.distanceTo(location) / seconds * 3.6f else 0f
            }
            else -> 0f
        }

        AppState.currentSpeedKmh = speedKmh
        AppState.isInVehicle = speedKmh > VEHICLE_SPEED_KMH

        if (previous != null && !AppState.isInVehicle) {
            val meters = previous.distanceTo(location)
            if (meters in MIN_DISTANCE_METERS..MAX_JUMP_METERS) {
                AppState.addGpsDistance(meters / 1000f)
            }
        }

        lastLocation = location
    }

    override fun onProviderEnabled(provider: String) {
        AppState.isGpsActive = true
    }

    override fun onProviderDisabled(provider: String) {
        val anyEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        AppState.isGpsActive = anyEnabled
    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}
