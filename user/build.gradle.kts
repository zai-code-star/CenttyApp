plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.user"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.user"
        minSdk = 21
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Core AndroidX libraries
    implementation("androidx.core:core-ktx:1.12.0") // Kotlin extensions untuk API inti AndroidX.
    implementation("androidx.appcompat:appcompat:1.6.1") // Memastikan aplikasi memiliki tampilan dan perilaku konsisten di berbagai versi Android.
    implementation("com.google.android.material:material:1.11.0") // Komponen UI Material Design dari Google.
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Dukungan untuk ConstraintLayout, membantu dalam mendesain tata letak UI kompleks.
    implementation("androidx.datastore:datastore-core-android:1.1.0-beta01") // Solusi penyimpanan data yang disarankan oleh Android Jetpack untuk menyimpan data terkait aplikasi.

    // Firebase libraries
    implementation(platform("com.google.firebase:firebase-bom:32.7.2")) // Bill of Materials (BOM) untuk mengelola versi-dependensi Firebase.
    implementation("com.google.firebase:firebase-analytics") // Alat analisis yang kuat untuk aplikasi.
    implementation("com.google.firebase:firebase-auth:22.3.1") // Otentikasi pengguna dengan Firebase Authentication.
    implementation("com.google.firebase:firebase-database:20.3.0") // Database Firebase untuk menyimpan dan menyinkronkan data di cloud.
    implementation("com.google.firebase:firebase-storage:20.3.0") // Kemampuan untuk menyimpan file dan data di Firebase Storage.
    implementation("com.google.firebase:firebase-inappmessaging-display:20.4.0") // Menampilkan pesan in-app dengan Firebase In-App Messaging.
    implementation("com.google.firebase:firebase-database-ktx:20.0.0") // Mendukung penggunaan Kotlin untuk Firebase Realtime Database.
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.android.gms:play-services-location:21.2.0") // Mengirim dan menerima pesan push dengan Firebase Cloud Messaging.

    // Testing libraries
    testImplementation("junit:junit:4.13.2") // Framework pengujian unit untuk Java.
    androidTestImplementation("androidx.test.ext:junit:1.1.5") // Memperluas JUnit untuk pengujian Android.
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // Mendukung pengujian antarmuka pengguna (UI) dalam aplikasi Android.

    // Third-party libraries
    implementation("com.facebook.shimmer:shimmer:0.5.0") // Efek shimmer untuk komponen UI.
    implementation("com.airbnb.android:lottie:3.6.0") // Menampilkan animasi dengan format JSON menggunakan Lottie.
    implementation("com.afollestad.material-dialogs:core:3.3.0") // Membuat dialog dengan mudah di aplikasi Anda.
    implementation("com.github.bumptech.glide:glide:4.12.0") // Library untuk memuat dan menampilkan gambar.
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0") // Compiler untuk Glide, digunakan saat menggunakan beberapa fitur khusus Glide.

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.0") // Memfasilitasi navigasi antar-fragment dalam aplikasi Anda.
    implementation("androidx.navigation:navigation-ui-ktx:2.3.0") // Menangani navigasi UI dengan komponen UI AndroidX.

    // SwipeRefreshLayout library
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0") // Widget layout untuk menyegarkan konten dengan menarik ke bawah.

}
