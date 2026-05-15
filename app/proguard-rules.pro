# Add project specific ProGuard rules here.
-keep class com.kumbara.kala.data.model.** { *; }
-keep class com.kumbara.kala.data.db.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn okhttp3.**
-dontwarn retrofit2.**
