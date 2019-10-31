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

    implementation(Libraries.location)

    implementation(Libraries.coroutines)
}
