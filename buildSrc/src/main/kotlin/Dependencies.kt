const val kotlinVersion = "1.3.50"
const val coroutinesVersion = "1.3.2"
const val koinVersion = "2.0.1"

object BuildPlugins {

    object Versions {
        const val androidGradlePlugin = "3.5.0"
        const val jacocoAndroid = "0.1.4"
    }

    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val kotlinSerializationPlugin = "org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion"
    const val jacocoGradlePlugin = "com.dicedmelon.gradle:jacoco-android:${Versions.jacocoAndroid}"

    const val androidLibrary = "com.android.library"
    const val kotlinSerialization = "kotlinx-serialization"
    const val androidApplication = "com.android.application"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
    const val jacoco = "jacoco-android"

}

object Android {
    const val minSdk = 21
    const val compileSdk = 29
    const val targetSdk = 29
}

object Libraries {

    private object Versions {
        const val appCompat = "1.1.0"
        const val lifecycle = "2.2.0-rc01"
        const val retrofit = "2.6.0"
        const val retrofitSerialization = "0.4.0"
        const val loggingInterceptor = "4.0.1"

        const val material = "1.1.0-beta01"

        const val constraintLayout = "1.1.3"

        const val kotlinSerializationRuntime = "0.11.1"

        const val navigation = "2.2.0-rc01"

        const val map = "17.0.0"
        const val mapUtil = "0.5"
        const val location = "17.0.0"

        const val runtimePermission = "1.1.1"
    }

    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val runtimePermission =
        "com.github.florent37:runtime-permission-kotlin:${Versions.runtimePermission}"

    const val navigation = "androidx.navigation:navigation-fragment:${Versions.navigation}"
    const val navigationKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"

    const val koinCore = "org.koin:koin-core:$koinVersion"
    const val koinViewModel = "org.koin:koin-android-viewmodel:$koinVersion"

    const val lifecycle = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    const val lifecycleKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val kotlinSerializationRuntime =
        "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinSerializationRuntime}"

    const val material = "com.google.android.material:material:${Versions.material}"

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitSerialization =
        "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.retrofitSerialization}"

    const val okHttpLogging =
        "com.squareup.okhttp3:logging-interceptor:${Versions.loggingInterceptor}"

    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"

    const val coroutines =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion"

    const val map = "com.google.android.gms:play-services-maps:${Versions.map}"
    const val mapUtil = "com.google.maps.android:android-maps-utils:${Versions.mapUtil}"
    const val location = "com.google.android.gms:play-services-location:${Versions.location}"
}

object Testing {
    private object Versions {
        const val junit = "5.5.2"
        const val mockito = "3.1.0"
        const val mockitoKotlin = "2.2.0"
        const val mockitoInline = "2.8.47"
    }

    const val junit = "org.junit.jupiter:junit-jupiter-api:${Versions.junit}"
    const val junitEngine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit}"
    const val mockito = "org.mockito:mockito-core:${Versions.mockito}"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"
    const val mockitoInline = "org.mockito:mockito-inline:${Versions.mockitoInline}"
    const val koinTest = "org.koin:koin-test:$koinVersion"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion"
}

object Modules {
    const val core = ":core"
    const val location = ":location"
    const val map = ":map"
}