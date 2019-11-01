import com.android.build.gradle.internal.dsl.TestOptions
import com.android.build.gradle.internal.tasks.JacocoTask

plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.safeArgs)
    id(BuildPlugins.jacoco)
    id(BuildPlugins.kotlinAndroidExtensions)
    id(BuildPlugins.kotlinSerialization)
}

android {
    compileSdkVersion(Android.compileSdk)
    defaultConfig {
        applicationId = "com.regin.starving"
        minSdkVersion(Android.minSdk)
        targetSdkVersion(Android.targetSdk)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"https://api.foursquare.com/v2/\"")
        buildConfigField(
            "String",
            "CLIENT_ID",
            "\"1VLMQ0WAEG1HVOHITUL0TSJJUTSL4V1YNLU0IZXBUESPZB1M\""
        )
        buildConfigField(
            "String",
            "CLIENT_SECRET",
            "\"Z2T2CFLQEX0LYUYTAADT3F4JYFWAV21JPG3MSN3VXFSB2XWR\""
        )
        buildConfigField("String", "API_VERSION", "\"20180323\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        animationsDisabled = true
        unitTests(delegateClosureOf<TestOptions.UnitTestOptions> {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
            all(KotlinClosure1<Any, Test>({
                (this as Test).also { testTask ->
                    testTask.extensions
                        .getByType(JacocoTaskExtension::class.java)
                        .isIncludeNoLocationClasses = true
                }
            }, this))
        })
    }
}

dependencies {
    implementation(project(Modules.core))
    implementation(project(Modules.map))
    implementation(project(Modules.location))

    implementation(Libraries.kotlinStdLib)
    implementation(Libraries.coroutines)

    implementation(Libraries.appCompat)
    implementation(Libraries.constraintLayout)
    implementation(Libraries.material)
    implementation(Libraries.runtimePermission)

    implementation(Libraries.navigation)
    implementation(Libraries.navigationKtx)

    implementation(Libraries.koinCore)
    implementation(Libraries.koinViewModel)

    implementation(Libraries.lifecycle)
    implementation(Libraries.lifecycleKtx)

    implementation(Libraries.retrofit)
    implementation(Libraries.okHttpLogging)
    implementation(Libraries.retrofitSerialization)
    implementation(Libraries.kotlinSerializationRuntime)

    testImplementation(Testing.coroutinesTest)
    testImplementation(Testing.koinTest)
    testImplementation(Testing.junit)
    testImplementation(Testing.junitEngine)
    testImplementation(Testing.mockito)
    testImplementation(Testing.mockitoKotlin)
    testImplementation(Testing.mockitoInline)
}

jacoco {
    toolVersion = "0.8.3"
}

tasks.withType<Test> {
    useJUnitPlatform()
}