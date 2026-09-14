# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep SDK classes
-keep class com.apptestershub.sdk.** { *; }
-keepclassmembers class com.apptestershub.sdk.** { *; }

# Keep Gson classes
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

# Keep OkHttp classes
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Keep Android classes
-keep class android.** { *; }
-keep interface android.** { *; }

