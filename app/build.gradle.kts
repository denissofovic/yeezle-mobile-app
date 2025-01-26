
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.yeezlemobileapp"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
        dataBinding = true
    }




    defaultConfig {
        applicationId = "com.example.yeezlemobileapp"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "ARTIST_ID", "\"${project.findProperty("ARTIST_ID")}\"")
        buildConfigField("String", "SUPABASE_URL", "\"${project.findProperty("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${project.findProperty("SUPABASE_KEY")}\"")
        buildConfigField("String", "SPOTIFY_BASE_URL", "\"${project.findProperty("SPOTIFY_BASE_URL")}\"")
        buildConfigField("String", "CLIENT_ID", "\"${project.findProperty("CLIENT_ID")}\"")
        buildConfigField("String", "CLIENT_SECRET", "\"${project.findProperty("CLIENT_SECRET")}\"")
        buildConfigField("String", "REDIRECT_URI", "\"${project.findProperty("REDIRECT_URI")}\"")
        buildConfigField("String", "DATABASE_NAME", "\"${project.findProperty("DATABASE_NAME")}\"")
        buildConfigField("String", "DATABASE_VERSION", "\"${project.findProperty("DATABASE_VERSION")}\"")
        buildConfigField("String", "REDIRECT_URI_AUTH", "\"${project.findProperty("REDIRECT_URI_AUTH")}\"")
        buildConfigField("String", "REDIRECT_URI_RESET", "\"${project.findProperty("REDIRECT_URI_RESET")}\"")
        buildConfigField("String", "REDIRECT_URI_PLAYBACK", "\"${project.findProperty("REDIRECT_URI_PLAYBACK")}\"")
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
}

dependencies {


    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // Supabase dependencies
    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.1"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")

    // Ktor client dependency
    implementation("io.ktor:ktor-client-okhttp:3.0.0-rc-1")
    implementation("io.github.jan-tennert.supabase:serializer-jackson:3.0.1")

    //Bcrypt dependency
    implementation("org.mindrot:jbcrypt:0.4")

    // Room Database dependencies
    implementation("androidx.room:room-runtime:2.5.1") // Room runtime
    kapt("androidx.room:room-compiler:2.5.1") // Room annotation processor

    // Room Kotlin Extensions (for Kotlin users)
    implementation("androidx.room:room-ktx:2.5.1") // Kotlin extensions

    // For testing Room
    testImplementation("androidx.room:room-testing:2.5.1")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    // Event bus
    implementation("org.greenrobot:eventbus:3.2.0")


    // Worker
    implementation ("androidx.work:work-runtime-ktx:2.8.0")

    // Exo player
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // Konfetti animation
    implementation ("nl.dionsegijn:konfetti-xml:2.0.4")


    // Testing dependencies

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")


}
