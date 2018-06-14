# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in retrofit.gradle.
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

# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain service method parameters.
# Ignore annotation used for retrofit tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase


-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *

#FOR APPCOMPAT 23.1.1:
# Samsung Android 4.2 bug workaround
-keep class android.support.v7.widget.** { *; }
-keep interface android.support.v7.widget.** { *; }
-keep class !android.support.v7.view.menu.*MenuBuilder*, android.support.v7.** { *; }
-keep interface android.support.v7.* { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

# Support desing library
-dontwarn android.support.design.**
#-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# Models
-keep class info.czekanski.bet.model.** { *; }
-keep class info.czekanski.bet.network.model.** { *; }
-keep class info.czekanski.bet.network.firebase.model.** { *; }
########### RxJava, Retrolambda ############
# RxJava
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

########## Analytics #############
# Google ADS
-keepattributes JavascriptInterface
-keep public class com.google.android.gms.ads.identifier.** { *; }
-keep public class com.google.android.gms.gcm.** { *; }
-keep public class com.google.android.gms.common.** { *; }
-keep public class com.google.android.gms.internal.** { *; }

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-dontwarn java.beans.ConstructorProperties

# Kotlin
-keep class kotlin.internal.annotations.AvoidUninitializedObjectCopyingCheck { *; }
-dontwarn kotlin.internal.annotations.AvoidUninitializedObjectCopyingCheck


-dontwarn durdinapps.rxfirebase2.**
-keepattributes Signature