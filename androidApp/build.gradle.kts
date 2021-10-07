plugins {
    id("com.android.application")
    kotlin("android")
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.compose.ui:ui:1.0.3")
    implementation("androidx.compose.material:material:1.0.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.0.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.4.0-beta01")
    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.3")
    debugImplementation("androidx.compose.ui:ui-tooling:1.0.3")

}


android {
    compileSdk = 31
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    defaultConfig {
        applicationId = "tom.wayne.mvi.android"
        versionCode = 1
        minSdk =28
        targetSdk =31
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.3"
        kotlinCompilerVersion = "1.5.30"
    }
}