package com.example.pasoaventuras

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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

    // Lanzador para permisos multiples (Actividad Fisica + GPS)
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val activityGranted = permissions[Manifest.permission.ACTIVITY_RECOGNITION] ?: true
        val locationFineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val locationCoarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        stepSensorManager.startListening()

        if (locationFineGranted || locationCoarseGranted) {
            locationTracker.start()
        }

        // Si el dispositivo no tiene chip de hardware dedicado de podometro, avisar al usuario
        if (!stepSensorManager.hasHardwarePedometer) {
            showHardwareWarningDialog = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar administradores de hardware
        stepSensorManager = StepSensorManager(this)
        locationTracker = LocationTracker(this)

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

                // Alerta al iniciar si el dispositivo no cuenta con sensor de podometro por hardware dedicado
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
                        modifier = Modifier.fillMaxSize(),
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
                            modifier = Modifier.padding(innerPadding)
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

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            stepSensorManager.startListening()
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
