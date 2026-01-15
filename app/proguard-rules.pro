# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /files/proguard-android-optimize.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# Soften the shrinking to avoid removing necessary classes
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep generic classes if needed
# -keep class com.example.app.** { *; }

# If using Room
-keep class androidx.room.RoomMasterTable {
    public static java.lang.String createInsertQuery(java.lang.String);
}
