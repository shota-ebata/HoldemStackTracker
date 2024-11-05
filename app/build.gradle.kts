import java.io.FileInputStream
import java.util.Base64
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.devtools.ksp)
}

android {
    namespace = "com.ebata_shota.holdemstacktracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ebata_shota.holdemstacktracker"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        val signFileBase = file("./../key_store_info.properties")
//        getByName("debug") {
//            if (signFileBase.exists()) {
//                val signingProps = Properties()
//                signingProps.load(FileInputStream(signFileBase))
//                storeFile = file(signingProps["debugStoreFilePathValue"] as String)
//                storePassword = signingProps["debugKeyStorePassword"] as String
//                keyAlias = signingProps["debugKeyAliasValue"] as String
//                keyPassword = signingProps["debugKeyPasswordValue"] as String
//            } else {
//                val debugKeystoreFileName = "debug-keystore.keystore"
//                System.getenv("DEBUG_KEY_STORE_BASE64")?.let { base64 ->
//                    val decoder = Base64.getMimeDecoder()
//                    File(debugKeystoreFileName).also { file ->
//                        file.createNewFile()
//                        file.writeBytes(decoder.decode(base64))
//                    }
//                }
//                storeFile = rootProject.file(debugKeystoreFileName)
//                storePassword = System.getenv("DEBUG_KEY_STORE_PASSWORD")
//                keyAlias = System.getenv("DEBUG_KEY_ALIAS_VALUE")
//                keyPassword = System.getenv("DEBUG_KEY_PASSWORD_VALUE")
//            }
//        }
        create("release") {
            if (signFileBase.exists()) {
                val signingProps = Properties()
                signingProps.load(FileInputStream(signFileBase))
                storeFile = file(signingProps["storeFilePathValue"] as String)
                storePassword = signingProps["keyStorePassword"] as String
                keyAlias = signingProps["keyAliasValue"] as String
                keyPassword = signingProps["keyPasswordValue"] as String
            } else {
                val releaseKeystoreFileName = "release-keystore.keystore"
                System.getenv("RELEASE_KEY_STORE_BASE64")?.let { base64 ->
                    val decoder = Base64.getMimeDecoder()
                    File(releaseKeystoreFileName).also { file ->
                        file.createNewFile()
                        file.writeBytes(decoder.decode(base64))
                    }
                }
                storeFile = rootProject.file(releaseKeystoreFileName)
                storePassword = System.getenv("RELEASE_KEY_STORE_PASSWORD")
                keyAlias = System.getenv("RELEASE_KEY_ALIAS_VALUE")
                keyPassword = System.getenv("RELEASE_KEY_PASSWORD_VALUE")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            isShrinkResources = false
            isMinifyEnabled = false
            isDebuggable =  true
            applicationIdSuffix = ".debug"
//            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true // TODO: trueかどうか確認する
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
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
        buildConfig = true
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // coroutines
    implementation(libs.org.jetbrains.kotlinx.coroutine.android)
    testImplementation(libs.org.jetbrains.kotlinx.coroutine.test)

    // hilt
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)

    // hiltWork
    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)

    // work
    implementation(libs.andriodx.work.runtime.ktx)

    // PreferencesDataStore
    implementation(libs.androidx.datastore.preferences)

    // mockk
    testImplementation(libs.io.mockk.mockk)

    // firebaseBom
    implementation(platform(libs.firebase.bom))
    // - crashlytics
    implementation(libs.firebase.crashlytics.ktx)
    // - database
    implementation(libs.firebase.database.ktx)

}
