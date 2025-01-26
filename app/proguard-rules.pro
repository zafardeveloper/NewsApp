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

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Room Database
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(android.content.Context);
}
-keepclassmembers class * extends androidx.room.RoomDatabase {
    static **.getDatabase(android.content.Context);
}

# Room Entities and DAOs
-keep @androidx.room.* class *
-keep @androidx.room.* interface *
-keepclassmembers class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}

# Keep the Parcelable implementation
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# Retain serialized field names for Room entities
-keepattributes *Annotation*

# Ensure that Kotlin metadata is preserved
-keep class kotlin.Metadata { *; }