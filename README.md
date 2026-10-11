# 🐾 PasoAventuras

> Aplicación móvil Android de gamificación del ejercicio físico, desarrollada con **Jetpack Compose** y **Firebase**.

---

## 📱 Descripción

**PasoAventuras** es una app de salud y bienestar que convierte caminar en una aventura. Los usuarios acumulan pasos diarios para ganar monedas virtuales (**ChoyoCoins**), desbloquear tarjetas coleccionables mediante un sistema de Gacha, completar misiones y subir de nivel.

La app cuenta con la mascota oficial **Choyo** 🐾, un personaje kawaii que acompaña al usuario en cada aventura.

---

## ✨ Características principales

| Pantalla | Descripción |
|----------|-------------|
| 🏠 **Dashboard** | Contador de pasos en tiempo real, anillo de progreso, estadísticas del día |
| 🎯 **Misiones** | Misiones diarias y semanales con recompensas en ChoyoCoins |
| 🎰 **Gacha** | Sistema de invocación de tarjetas con rareza (1⭐ a 5⭐) |
| 📚 **Álbum** | Colección completa de 15 tarjetas Choyo desbloqueables |
| 👤 **Perfil** | Estadísticas del usuario, avatar personalizable y configuración |
| 🔐 **Login** | Autenticación con Firebase (correo, contraseña o modo invitado) |

---

## 🛠️ Tecnologías utilizadas

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material Design 3
- **Navegación:** Navigation Compose
- **Backend:** Firebase Authentication + Cloud Firestore
- **Sensor:** Android Step Counter (pedómetro físico)
- **Arquitectura:** Single Activity + Composables
- **Control de versiones:** Git (GitLab + GitHub)

---

## 🔥 Firebase

La app está conectada a **Firebase** para:
- ✅ **Authentication:** Login con correo/contraseña y sesión de invitado anónima
- ✅ **Cloud Firestore:** Sincronización del progreso del usuario en la nube
- ✅ Persistencia de sesión (no requiere iniciar sesión cada vez)

---

## 🌿 Flujo de ramas (Git Flow)

`
main
 └── develop
      └── feature/firebase-setup
`

| Rama | Propósito |
|------|-----------|
| main | Versión estable y lista para producción |
| develop | Integración de nuevas funcionalidades |
| feature/firebase-setup | Integración de Firebase Auth y Firestore |

---

## 🚀 Cómo ejecutar el proyecto

### Requisitos previos
- Android Studio Flamingo o superior
- JDK 11
- Android SDK 26+

### Pasos

1. Clonar el repositorio:
`
git clone https://gitlab.com/andrey-engineering-group/pasoaventuras.git
`

2. Abrir en Android Studio y sincronizar Gradle

3. Ejecutar con Shift + F10 o el botón verde

### Compilar APK de debug:
`
.\gradlew assembleDebug
`

---

## 🏗️ Estructura del proyecto

`
app/src/main/java/com/example/pasoaventuras/
├── MainActivity.kt
├── data/
│   ├── Models.kt
│   ├── AppState.kt
│   ├── CardRepository.kt
│   ├── FirebaseManager.kt
│   └── StepSensorManager.kt
├── ui/
│   ├── components/
│   └── screens/
│       ├── LoginScreen.kt
│       ├── DashboardScreen.kt
│       ├── MisionesScreen.kt
│       ├── GachaScreen.kt
│       ├── AlbumScreen.kt
│       └── PerfilScreen.kt
`

---

## 👤 Autor

**Carlos Andrey Gómez Alonso**
GitLab: andrey-engineering-group
GitHub: Small-Programer

---

Proyecto académico — Desarrollo de Aplicaciones Móviles
