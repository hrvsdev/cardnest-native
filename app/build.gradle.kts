plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
}

android {
  namespace = "com.hrvs.cardnest"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.hrvs.cardnest"
    minSdk = 24
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
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.1"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  val voyagerVersion = "1.0.0"

  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
  implementation("androidx.activity:activity-compose:1.9.2")
  implementation(platform("androidx.compose:compose-bom:2024.09.02"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3")

  implementation("androidx.core:core-splashscreen:1.0.1")

  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")

  // Data
  implementation("androidx.datastore:datastore:1.1.1")
  implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

  // Id
  implementation("io.viascom.nanoid:nanoid:1.0.1")

  // Voyager
  implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
  implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")
  implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator:$voyagerVersion")
  implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
  implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
}
