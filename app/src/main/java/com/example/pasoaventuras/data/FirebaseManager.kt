package com.example.pasoaventuras.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object FirebaseManager {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    /**
     * Iniciar sesión con Correo y Contraseña
     */
    fun loginWithEmail(
        email: String,
        pass: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || pass.isBlank()) {
            onError("Por favor completa todos los campos.")
            return
        }

        auth.signInWithEmailAndPassword(email.trim(), pass)
            .addOnSuccessListener { result ->
                result.user?.let { user ->
                    AppState.isLoggedIn = true
                    AppState.userEmail = user.email ?: email
                    loadUserDataFromFirestore(user.uid)
                    onSuccess(user)
                } ?: onError("No se pudo obtener información del usuario.")
            }
            .addOnFailureListener { exception ->
                onError(exception.localizedMessage ?: "Error al iniciar sesión.")
            }
    }

    /**
     * Registro con Correo y Contraseña
     */
    fun registerWithEmail(
        email: String,
        pass: String,
        name: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || pass.isBlank()) {
            onError("Por favor completa todos los campos.")
            return
        }

        auth.createUserWithEmailAndPassword(email.trim(), pass)
            .addOnSuccessListener { result ->
                result.user?.let { user ->
                    AppState.isLoggedIn = true
                    AppState.userEmail = user.email ?: email
                    if (name.isNotBlank()) AppState.userName = name
                    saveUserDataToFirestore()
                    onSuccess(user)
                } ?: onError("No se pudo registrar el usuario.")
            }
            .addOnFailureListener { exception ->
                onError(exception.localizedMessage ?: "Error al registrarse.")
            }
    }

    /**
     * Iniciar sesión de forma anónima (Invitado)
     */
    fun loginAnonymously(
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInAnonymously()
            .addOnSuccessListener { result ->
                result.user?.let { user ->
                    AppState.isLoggedIn = true
                    AppState.userName = "Invitado"
                    AppState.userEmail = "invitado@pasoaventuras.app"
                    loadUserDataFromFirestore(user.uid)
                    onSuccess(user)
                } ?: onError("No se pudo iniciar modo invitado.")
            }
            .addOnFailureListener { exception ->
                // Si la autenticación anónima no está habilitada en la consola, fallback a offline
                AppState.isLoggedIn = true
                AppState.userName = "Invitado"
                AppState.userEmail = "invitado@pasoaventuras.app"
                onError("Modo sin conexión activado.")
            }
    }

    /**
     * Cerrar sesión
     */
    fun logout(onComplete: () -> Unit = {}) {
        auth.signOut()
        AppState.reset()
        onComplete()
    }

    /**
     * Guardar el estado de juego y tarjetas en Cloud Firestore
     */
    fun saveUserDataToFirestore(onComplete: ((Boolean) -> Unit)? = null) {
        val uid = currentUser?.uid ?: return

        val userDoc = hashMapOf(
            "userName" to AppState.userName,
            "userEmail" to AppState.userEmail,
            "userAvatar" to AppState.userAvatar,
            "level" to AppState.level,
            "xp" to AppState.xp,
            "streak" to AppState.streak,
            "steps" to AppState.steps,
            "totalSteps" to AppState.totalSteps,
            "dailyGoal" to AppState.dailyGoal,
            "coins" to AppState.coins,
            "ownedCardIds" to AppState.ownedCards.map { it.card.id },
            "claimedMissionIds" to AppState.missions.filter { it.claimed }.map { it.id },
            "updatedAt" to System.currentTimeMillis()
        )

        firestore.collection("users").document(uid)
            .set(userDoc, SetOptions.merge())
            .addOnSuccessListener { onComplete?.invoke(true) }
            .addOnFailureListener { onComplete?.invoke(false) }
    }

    /**
     * Cargar los datos del usuario desde Cloud Firestore
     */
    fun loadUserDataFromFirestore(uid: String, onFinished: (() -> Unit)? = null) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    doc.getString("userName")?.let { AppState.userName = it }
                    doc.getString("userAvatar")?.let { AppState.userAvatar = it }
                    doc.getLong("level")?.let { AppState.level = it.toInt() }
                    doc.getLong("xp")?.let { AppState.xp = it.toInt() }
                    doc.getLong("streak")?.let { AppState.streak = it.toInt() }
                    doc.getLong("steps")?.let { AppState.steps = it.toInt() }
                    doc.getLong("totalSteps")?.let { AppState.totalSteps = it.toInt() }
                    doc.getLong("dailyGoal")?.let { AppState.dailyGoal = it.toInt() }
                    doc.getLong("coins")?.let { AppState.coins = it.toInt() }

                    // Tarjetas
                    @Suppress("UNCHECKED_CAST")
                    val cardIds = doc.get("ownedCardIds") as? List<String>
                    if (!cardIds.isNullOrEmpty()) {
                        AppState.ownedCards.clear()
                        cardIds.forEach { id ->
                            CardRepository.allCards.find { it.id == id }?.let { card ->
                                AppState.ownedCards.add(CardInstance(card))
                            }
                        }
                    }

                    // Misiones
                    @Suppress("UNCHECKED_CAST")
                    val claimedIds = doc.get("claimedMissionIds") as? List<String>
                    if (claimedIds != null) {
                        AppState.missions.forEachIndexed { index, mission ->
                            if (claimedIds.contains(mission.id)) {
                                AppState.missions[index] = mission.copy(claimed = true)
                            }
                        }
                    }
                } else {
                    // Documento nuevo: guardamos el estado base inicial
                    saveUserDataToFirestore()
                }
                onFinished?.invoke()
            }
            .addOnFailureListener {
                onFinished?.invoke()
            }
    }
}
