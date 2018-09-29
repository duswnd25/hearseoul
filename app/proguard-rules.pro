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

# OKHTTP
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# GLIDE
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep class de.hdodenhof.**  { *; }
-keep class com.skt.** { *; }
-keep class com.crashlytics.** { *; }
-keep class com.android.** { *; }
-keep class me.grantland.** { *; }
-keep class gun0912.** { *; }
-keep class com.muddzdev.** { *; }
-keep class me.relex.** { *; }
-keep class com.google.** { *; }
-keep class com.github.linger1216.** { *; }

-dontwarn de.hdodenhof.**
-dontwarn com.skt.**
-dontwarn com.crashlytics.**
-dontwarn com.android.**
-dontwarn me.grantland.**
-dontwarn gun0912.**
-dontwarn com.muddzdev.**
-dontwarn me.relex.**
-dontwarn com.google.**
-dontwarn com.github.linger1216.**

#파일 생성 옵션
-printmapping proguard/map.txt
-printseeds proguard/seed.txt
-printusage proguard/usage.txt
-printconfiguration proguard/config.txt