plugins {
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
}

android {
    compileSdkVersion(Android.compileSdk)
    defaultConfig {
        minSdkVersion(Android.minSdk)
        targetSdkVersion(Android.targetSdk)
    }
}

dependencies {
    implementation(project(Modules.core))

    implementation(Libraries.kotlinStdLib)

    implementation(Libraries.appCompat)
    implementation(Libraries.material)

    implementation(Libraries.lifecycleKtx)
    implementation(Libraries.lifecycle)

    implementation(Libraries.coroutines)

    implementation(Libraries.map)
    implementation(Libraries.mapUtil)
}
