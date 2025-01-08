import java.io.FileInputStream
import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.google.gms.google.services)
}

val keystore = Properties().apply {
  load(FileInputStream(rootProject.file("keystore.properties")))
}

android {
  namespace = "app.cardnest"
  compileSdk = 35

  defaultConfig {
    applicationId = "app.cardnest"
    minSdk = 24
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"
  }

  signingConfigs {
    create("release") {
      storeFile = file(keystore["storeFile"] as String)
      storePassword = keystore["storePassword"] as String
      keyAlias = keystore["keyAlias"] as String
      keyPassword = keystore["keyPassword"] as String
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      signingConfig = signingConfigs.getByName("release")
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      resValue("string", "app_name", "@string/app_name_release")
    }

    debug {
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

tasks.register("printVersionName") {
  doFirst {
    println(android.defaultConfig.versionName)
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
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.datastore)
  implementation(libs.kotlinx.immutable)
  implementation(libs.kotlinx.serialization)
  implementation(libs.nanoid)
  implementation(libs.voyager.navigation)
  implementation(libs.voyager.bottom.sheet.navigation)
  implementation(libs.voyager.transitions)
  implementation(libs.koin.androidx.compose)
  implementation(libs.koin.androidx.startup)
  implementation(libs.androidx.biometric)
  implementation(libs.firebase.auth)
  implementation(libs.firebase.database)
  implementation(libs.googleid)

  runtimeOnly(libs.androidx.material)
}
