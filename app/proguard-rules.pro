# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# 1. ATURAN DASAR UNTUK PACKAGE UTAMA ANDA
# Menjaga agar ViewModel dan Data Class tidak rusak
-keep class com.indonesiaemas.note.viewmodel.** { *; }
-keep class com.indonesiaemas.note.ui.screens.** { *; }

# PENTING: Menjaga class Biometric agar fitur kunci sidik jari tetap jalan
-keep class com.indonesiaemas.note.ui.Biometric** { *; }

# 2. ANDROIDX & COMPOSE (Wajib untuk UI Anda)
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keep class androidx.navigation.** { *; }

# 3. BIOMETRIC & FRAGMENT (Karena Anda pakai FragmentActivity)
-keep class androidx.biometric.** { *; }
-keep class androidx.fragment.app.** { *; }

# 4. JETPACK NAVIGATION (Agar navigasi antar screen tidak error)
-keepnames class com.indonesiaemas.note.ui.Screen { *; }

# 5. OPTIMASI UMUM
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepattributes SourceFile, LineNumberTable