# AR Drill Placement App

An Android application built with Jetpack Compose and ARCore that allows users to select a 3D model and place it in the real world using Augmented Reality.

---

## ✨ Core Features

* **Drill Selection:** Choose from a list of different drills.
* **AR Placement:** Place a unique 3D model for each drill onto real-world surfaces.
* **Real-time Lighting:** Uses ARCore's Environmental HDR mode to apply realistic lighting to the virtual objects.
* **Modern UI:** Built entirely with Jetpack Compose and Material 3.

---

## 🛠 Tech Stack

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose
* **AR Framework:** ARCore SDK for Android
* **3D Rendering:** sceneview-android
* **Navigation:** Jetpack Navigation for Compose

---

## 🚀 Setup and Installation

To run this project locally, follow these steps:

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/Sumit99089/Ar.git](https://github.com/Sumit99089/Ar.git)
    ```

2.  **Open in Android Studio:**
    * Open Android Studio (Hedgehog or newer is recommended).
    * Select `File > Open` and navigate to the cloned project folder.
    * Let Gradle sync and build the project.

3.  **Prepare 3D Models:**
    * Ensure your `.glb` model files (`cube.glb`, `cone.glb`, `sphere.glb`) are placed in the `app/src/main/assets/models/` directory.

4.  **Run the App:**
    * Connect an ARCore-compatible Android device.
    * Click the `Run` button in Android Studio.