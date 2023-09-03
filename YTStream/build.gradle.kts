@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.serialization)
    `maven-publish`
}

android {
    namespace = "com.nvvi9.ytstream"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
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

    kotlinOptions {
        jvmTarget = "1.8"
    }

    publishing {
        multipleVariants {
            allVariants()
            withJavadocJar()
            withSourcesJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("mavenRelease") {
                groupId = "com.nvvi9"
                artifactId = "ytstream"
                version = "0.1.3"

                from(components["release"])
            }
            create<MavenPublication>("mavenDebug") {
                groupId = "com.nvvi9"
                artifactId = "ytstream"
                version = "0.1.3"

                from(components["debug"])
            }
        }
    }
}

dependencies {
    implementation(libs.org.jetbrains.kotlinx.coroutines.android)
    implementation(libs.org.jetbrains.kotlinx.serialization.json)
    implementation(libs.androidx.javascriptengine)
    implementation(libs.guava)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
