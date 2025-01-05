plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.google.gms.google.services)
}

android {
  namespace = "app.cardnest"
  compileSdk = 35

  defaultConfig {
    applicationId = "app.cardnest"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      resValue("string", "app_name", "@string/app_name_release")
      proguardFiles(getDefaultProguardFile("proguard-android.txt"))
    }

    debug {
      isMinifyEnabled = false
      applicationIdSuffix = ".debug"
      resValue("string", "app_name", "@string/app_name_debug")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs += "-Xcontext-receivers"
  }

  buildFeatures {
    compose = true
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.datastore)
  implementation(libs.kotlinx.immutable)
  implementation(libs.kotlinx.serialization)
  implementation(libs.nanoid)
  implementation(libs.voyager.navigation)
  implementation(libs.voyager.tab.navigator)
  implementation(libs.voyager.bottom.sheet.navigation)
  implementation(libs.voyager.transitions)
  implementation(libs.voyager.screenmodel)
  implementation(libs.koin.androidx.compose)
  implementation(libs.koin.androidx.startup)
  implementation(libs.androidx.biometric)
  implementation(libs.firebase.auth)
  implementation(libs.firebase.database)
  implementation(libs.googleid)

  runtimeOnly(libs.androidx.material)
}
