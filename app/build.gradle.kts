plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.dashboard_mobile_redes_industriais"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.dashboard_mobile_redes_industriais"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // MQTT — Eclipse Paho Android
//    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
//    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")

//    implementation("com.github.hannesa2:paho.mqtt.android:3.3.5")

    // Remova a versão 3.3.5 antiga e adicione esta atualizada:
    implementation("com.github.hannesa2:paho.mqtt.android:4.5")

    // JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // Gráficos históricos
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // ViewModel + LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Material Design
    implementation("com.google.android.material:material:1.11.0")

    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Extensão KTX para habilitar o 'by viewModels()'
    implementation("androidx.activity:activity-ktx:1.9.0")

}