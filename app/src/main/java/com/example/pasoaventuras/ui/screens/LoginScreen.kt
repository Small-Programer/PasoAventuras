package com.example.pasoaventuras.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pasoaventuras.data.FirebaseManager
import com.example.pasoaventuras.ui.components.ChoyoMascot
import com.example.pasoaventuras.ui.theme.*

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current

    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("carlos@correo.com") }
    var password by remember { mutableStateOf("123456") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF312E81),
                        Color(0xFF4338CA),
                        Color(0xFF6366F1)
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Hero Section (Top)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 36.dp, start = 24.dp, end = 24.dp, bottom = 12.dp)
        ) {
            ChoyoMascot(size = 96.dp, animate = true)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "PasoAventuras",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Camina, colecciona tarjetas y gana recompensas",
                fontSize = 13.sp,
                color = Color(0xFFC7D2FE),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        // Card Bottom (Con Firebase Auth)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White,
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cabecera del formulario
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isRegisterMode) "Crear Cuenta" else "Iniciar Sesión",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isRegisterMode) "Únete y guarda tu avance en la nube." else "Ingresa a tu cuenta de explorador.",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }

                    TextButton(
                        onClick = { isRegisterMode = !isRegisterMode }
                    ) {
                        Text(
                            text = if (isRegisterMode) "¿Ya tienes cuenta?" else "Registrarse",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Indigo500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Campo Nombre (solo en registro)
                AnimatedVisibility(visible = isRegisterMode) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "NOMBRE DE AVENTURERO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Slate500,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("Ej. Carlos Explorador", fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Indigo500,
                                unfocusedBorderColor = Slate200,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Slate50
                            )
                        )
                    }
                }

                // Input Correo
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CORREO ELECTRÓNICO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate500,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Indigo500,
                            unfocusedBorderColor = Slate200,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Slate50
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Input Contraseña
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CONTRASEÑA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate500,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = "Alternar contraseña",
                                    tint = Slate400
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Indigo500,
                            unfocusedBorderColor = Slate200,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Slate50
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Primario: Iniciar Sesión / Registrarse
                Button(
                    onClick = {
                        isLoading = true
                        if (isRegisterMode) {
                            FirebaseManager.registerWithEmail(
                                email = email,
                                pass = password,
                                name = name.ifBlank { "Explorador" },
                                onSuccess = {
                                    isLoading = false
                                    Toast.makeText(context, "¡Cuenta creada con éxito! 🎉", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                },
                                onError = { msg ->
                                    isLoading = false
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            )
                        } else {
                            FirebaseManager.loginWithEmail(
                                email = email,
                                pass = password,
                                onSuccess = {
                                    isLoading = false
                                    Toast.makeText(context, "¡Bienvenido de vuelta! 🧭", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                },
                                onError = { msg ->
                                    isLoading = false
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isRegisterMode) "Crear mi Cuenta" else "Acceder a la Aventura",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "➔", fontSize = 14.sp, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Separador O BIEN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Slate200)
                    Text(
                        text = "O BIEN",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Slate200)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Botón Secundario: Entrar como Invitado
                Button(
                    onClick = {
                        isLoading = true
                        FirebaseManager.loginAnonymously(
                            onSuccess = {
                                isLoading = false
                                Toast.makeText(context, "Sesión de invitado iniciada 🚀", Toast.LENGTH_SHORT).show()
                                onLoginSuccess()
                            },
                            onError = {
                                isLoading = false
                                onLoginSuccess()
                            }
                        )
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Slate100)
                ) {
                    Text(
                        text = "🚀 Entrar como Invitado",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )
                }
            }
        }
    }
}
