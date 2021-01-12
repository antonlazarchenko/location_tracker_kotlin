# General description of this repo
This is an educational android project build in kotlin with clean architecture.
Purpose of this app: realtime tracking someone's location, a child for example.

[Download APK from Google Drive](https://drive.google.com/file/d/1UiszmYvo6fxRh3PFdSnw9HlQGJQIUnEI/view?usp=sharing)

# Tech stack
* Kotlin/MVVM
* Gradle multi-module architecture with Daggger2
* Android Service, Broadcast, Worker
* Firebase Cloud Firestore
* Firebase Authentication
* GMS and Google Maps


# Functionality description

After install on android device appears two icons "Tracker" and "Watcher".
You can install this app on two devices and login using your phone or email.
On first device launch "Tracker" and manage location status (enable/disable) which will send location periodically into Cloud Firestore.
On second device launch "Watcher" and watch in realtime where the first one located and use calendar to see any available day before today.

Tracking service running continuosly (if enabled) and it will restart after device reboot and app relaunch.
If no internet available service will collect all available location data and send it later in background asynchronously.

# Modules structure
- /app
  - /authfire
  - /base
  - /map
  - /service
