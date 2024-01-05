plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "id.psw.qsbadapple"
    compileSdk = 33

    defaultConfig {
        applicationId = "id.psw.qsbadapple"
        minSdk = 33
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    tasks.create<BakeAssetTask>("bakeAssets"){
        badApple  = File(project.projectDir, "../raw_asset/badapple.mp4")
        videoFile = File(project.projectDir, "src/main/assets/video.bad")
        audioFile = File(project.projectDir, "src/main/assets/audio.bad")
        group = "assets"
    }

}

dependencies {
}