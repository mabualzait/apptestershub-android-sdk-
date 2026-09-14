# Consumer ProGuard rules for library consumers
# These rules will be merged with the consumer's ProGuard configuration

# Keep SDK classes
-keep class com.apptestershub.sdk.** { *; }
-keepclassmembers class com.apptestershub.sdk.** { *; }

