package com.example.pasoaventuras

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pasoaventuras.data.AppState
import com.example.pasoaventuras.data.FirebaseManager
import com.example.pasoaventuras.data.LocationTracker
import com.example.pasoaventuras.data.StepSensorManager
import com.example.pasoaventuras.ui.components.BottomNavBar
import com.example.pasoaventuras.ui.screens.*
import com.example.pasoaventuras.ui.theme.PasoAventurasTheme

class MainActivity : ComponentActivity() {

    private lateinit var stepSensorManager: StepSensorManager
    private lateinit var locationTracker: LocationTracker

    private var showHardwareWarningDialog by mutableStateOf(false)

    // Lanzador para permisos múltiples (Actividad Física + GPS)
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationFineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val locationCoarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        // Intentar registrar el mejor sensor disponible (si no hay permiso de actividad, cae al acelerómetro)
        stepSensorManager.startListening()

        if (locationFineGranted || locationCoarseGranted) {
            locationTracker.start()
        }

        // Si el dispositivo no tiene chip de hardware dedicado de podómetro, avisar al usuario
        if (!stepSensorManager.hasHardwarePedometer) {
            showHardwareWarningDialog = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mantener la pantalla encendida mientras la app esté abierta para evitar suspensión abrupta en tablets
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Inicializar administradores de hardware
        stepSensorManager = StepSensorManager(this)
        locationTracker = LocationTracker(this)

        // Enlazar callbacks de UI
        AppState.onRequestPermissions = {
            checkAndRequestPermissions()
        }
        AppState.onOpenAppSettings = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        }

        // Estado inicial
        AppState.init()
        FirebaseManager.cleanupExpiredGuestUsers()

        if (FirebaseManager.isUserLoggedIn) {
            AppState.isLoggedIn = true
            FirebaseManager.currentUser?.let { user ->
                AppState.userEmail = user.email ?: AppState.userEmail
                FirebaseManager.loadUserDataFromFirestore(user.uid)
            }
        }

        checkAndRequestPermissions()

        setContent {
            PasoAventurasTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

                var isLoggedIn by remember { mutableStateOf(AppState.isLoggedIn) }

                // Alerta al iniciar si el dispositivo no cuenta con sensor de podómetro por hardware dedicado
                if (showHardwareWarningDialog) {
                    AlertDialog(
                        onDismissRequest = { showHardwareWarningDialog = false },
                        title = { Text(text = "⚠️ Aviso de Sensor de Hardware") },
                        text = {
                            Text(
                                text = "Tu dispositivo no cuenta con un sensor de podómetro de hardware dedicado (TYPE_STEP_COUNTER).\n\n" +
                                        "PasoAventuras usará el acelerómetro calibrado y el GPS satelital como respaldo para continuar contando tus pasos y midiendo tu distancia."
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = { showHardwareWarningDialog = false }) {
                                Text("Entendido")
                            }
                        }
                    )
                }

                if (!isLoggedIn) {
                    LoginScreen(
                        onLoginSuccess = {
                            isLoggedIn = true
                            checkAndRequestPermissions()
                        }
                    )
                } else {
                    Scaffold(
                        bottomBar = {
                            BottomNavBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "dashboard",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            composable("dashboard") { DashboardScreen() }
                            composable("misiones") { MisionesScreen() }
                            composable("gacha") {
                                GachaScreen(
                                    onGoToAlbum = {
                                        navController.navigate("album") {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                            composable("album") { AlbumScreen() }
                            composable("perfil") {
                                PerfilScreen(
                                    onLogout = {
                                        isLoggedIn = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        // Iniciar siempre el sensor (si no tiene permisos de actividad, caerá al acelerómetro seguro)
        stepSensorManager.startListening()

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            locationTracker.start()
            if (!stepSensorManager.hasHardwarePedometer) {
                showHardwareWarningDialog = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        stepSensorManager.startListening()
        locationTracker.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        stepSensorManager.stopListening()
        locationTracker.stop()
    }
}
