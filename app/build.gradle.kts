@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.kapt)
  alias(libs.plugins.parcelize)
  alias(libs.plugins.dagger.hilt)
}

android {
  namespace = "com.thejufo.chat"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.thejufo.chat"
    minSdk = 22
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.4.3"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {

  // Core
  implementation(libs.core.ktx)


  // Ui
  implementation(platform(libs.compose.bom))
  implementation(libs.activity.compose)
  implementation(libs.navigation.compose)
  implementation(libs.ui)
  implementation(libs.ui.graphics)
  implementation(libs.ui.tooling.preview)
  implementation(libs.material)

  // Lifecyle
  implementation(libs.lifecycle.runtime.ktx)
  implementation(libs.lifecycle.runtime.compose)
  implementation(libs.lifecycle.viewmodel.compose)
  implementation(libs.lifecycle.viewmodel.ktx)

  // Datastore
  implementation(libs.datastore.preferences)

  // Room
  implementation(libs.room.runtime)
  implementation(libs.room.ktx)
  kapt(libs.room.compiler)

  // Hilt
  implementation(libs.dagger.hilt.android)
  implementation(libs.hilt.navigation.compose)
  kapt(libs.dagger.hilt.compiler)
  kapt(libs.hilt.compiler)

  // WorkManager
  implementation(libs.work.runtime.ktx)

  // Smack
  implementation(libs.smack.tcp)
  implementation(libs.smack.android)

  // Utils
  implementation(libs.gson)

  // Debug
  debugImplementation(libs.ui.tooling)
  debugImplementation(libs.ui.test.manifest)
}

configurations {
  all {
    exclude(group = "xpp3", module = "xpp3")
  }
}

kapt {
  correctErrorTypes = true
}