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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pasoaventuras.data.AppState
import com.example.pasoaventuras.data.StepSensorManager
import com.example.pasoaventuras.ui.components.BottomNavBar
import com.example.pasoaventuras.ui.screens.*
import com.example.pasoaventuras.ui.theme.PasoAventurasTheme

class MainActivity : ComponentActivity() {

    private lateinit var stepSensorManager: StepSensorManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            stepSensorManager.startListening()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize App State & Firebase session
        AppState.init()
        if (com.example.pasoaventuras.data.FirebaseManager.isUserLoggedIn) {
            AppState.isLoggedIn = true
            com.example.pasoaventuras.data.FirebaseManager.currentUser?.let { user ->
                AppState.userEmail = user.email ?: AppState.userEmail
                com.example.pasoaventuras.data.FirebaseManager.loadUserDataFromFirestore(user.uid)
            }
        }

        // Sensor initialization
        stepSensorManager = StepSensorManager(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                stepSensorManager.startListening()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        } else {
            stepSensorManager.startListening()
        }

        setContent {
            PasoAventurasTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

                var isLoggedIn by remember { mutableStateOf(AppState.isLoggedIn) }

                if (!isLoggedIn) {
                    LoginScreen(
                        onLoginSuccess = {
                            isLoggedIn = true
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

    override fun onDestroy() {
        super.onDestroy()
        stepSensorManager.stopListening()
    }
}
