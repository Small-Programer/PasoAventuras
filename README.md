# PasoAventuras

Aplicacion movil Android de gamificacion del ejercicio fisico, desarrollada con Jetpack Compose y Firebase.

---

## Descripcion

PasoAventuras es una app de salud y bienestar que convierte caminar en una aventura. Los usuarios acumulan pasos diarios para ganar monedas virtuales (ChoyoCoins), desbloquear tarjetas coleccionables mediante un sistema de Gacha, completar misiones y subir de nivel.

---

## Caracteristicas principales

| Pantalla | Descripcion |
|----------|-------------|
| Dashboard | Contador de pasos en tiempo real, anillo de progreso, estadisticas del dia |
| Misiones | Misiones diarias y semanales con recompensas en ChoyoCoins |
| Gacha | Sistema de invocacion de tarjetas con rareza (1 a 5 estrellas) |
| Album | Coleccion completa de 15 tarjetas Choyo desbloqueables |
| Perfil | Estadisticas del usuario, avatar personalizable y configuracion |
| Login | Autenticacion con Firebase (correo, contrasena o modo invitado) |

---

## Tecnologias utilizadas

- Lenguaje: Kotlin
- UI: Jetpack Compose + Material Design 3
- Navegacion: Navigation Compose
- Backend: Firebase Authentication + Cloud Firestore
- Sensor: Android Step Counter (pedometro fisico)
- Arquitectura: Single Activity + Composables
- Control de versiones: Git (GitLab + GitHub)

---

## Firebase

La app esta conectada a Firebase para:
- Authentication: Login con correo/contrasena y sesion de invitado anonima
- Cloud Firestore: Sincronizacion del progreso del usuario en la nube
- Persistencia de sesion (no requiere iniciar sesion cada vez que se abre la app)

---

## Flujo de ramas (Git Flow)

`
main
 |-- develop
      |-- feature/firebase-setup
`

| Rama | Proposito |
|------|-----------|
| main | Version estable y lista para produccion |
| develop | Integracion de nuevas funcionalidades |
| feature/firebase-setup | Integracion de Firebase Auth y Firestore |

---

## Como ejecutar el proyecto

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

3. Ejecutar con Shift + F10 o el boton verde de ejecucion

### Compilar APK de debug:
`
.\gradlew assembleDebug
`

El APK se genera en: app/build/outputs/apk/debug/app-debug.apk

---

## Estructura del proyecto

`
app/src/main/java/com/example/pasoaventuras/
|-- MainActivity.kt
|-- data/
|   |-- Models.kt
|   |-- AppState.kt
|   |-- CardRepository.kt
|   |-- FirebaseManager.kt
|   |-- StepSensorManager.kt
|-- ui/
    |-- components/
    |-- screens/
        |-- LoginScreen.kt
        |-- DashboardScreen.kt
        |-- MisionesScreen.kt
        |-- GachaScreen.kt
        |-- AlbumScreen.kt
        |-- PerfilScreen.kt
`

---

## Autor

Carlos Andrey Gomez Alonso
GitLab: andrey-engineering-group
GitHub: Small-Programer

---

Proyecto academico - Desarrollo de Aplicaciones Moviles
