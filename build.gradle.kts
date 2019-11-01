buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()

    }
    dependencies {
        classpath(BuildPlugins.androidGradlePlugin)
        classpath(BuildPlugins.kotlinGradlePlugin)
        classpath(BuildPlugins.safeArgsPlugin)
        classpath(BuildPlugins.kotlinSerializationPlugin)
        classpath(BuildPlugins.jacocoGradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}

tasks.register("clean").configure {
    delete("build")
}