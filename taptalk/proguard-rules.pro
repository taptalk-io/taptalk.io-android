# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Fadhlan/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# RETROFIT
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn retrofit2.Platform.**
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

# keep everything in this package from being removed or renamed
-keep class retrofit2. { *; }

# keep everything in this package from being renamed only
-keepnames class retrofit2. { *; }

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions


#OKHTTP
#-dontwarn okio.**
#-dontwarn javax.annotation.Nullable
#-dontwarn javax.annotation.ParametersAreNonnullByDefault
-keepattributes Annotation
-keep class okhttp3. { *; }
-keep interface okhttp3. { *; }
-keepnames class okhttp3. { *; }
-dontwarn okhttp3.
-dontwarn okio.

-keepattributes Signature
-keepattributes Annotation
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# GLIDE
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule


-keepattributes *Annotation*,EnclosingMethod,Signature
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**
-keep class org.codehaus.** { *; }
-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
    public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }
-keep public class your.class.** {*;}

-dontwarn org.freemarker.**

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

-keep class com.google.firebase.quickstart.database.viewholder. {
    *;
}

-keepclassmembers class com.google.firebase.quickstart.database.models. {
    *;
}

-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.

-keep class rx. { *; }
-keepnames class rx. { *; }

-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-printmapping mapping.txt
-keep class com.crashlytics. { *; }
-dontwarn com.crashlytics.

-keep class com.orhanobut.hawk. { *; }
-keepnames class com.orhanobut.hawk. { *; }

-keep class com.orhanobut.hawk. { *; }
-keepnames class com.orhanobut.hawk. { *; }


#-keep class io. { *; }
#-keepnames class io. { *; }

-keep class okio. { *; }
-keepnames class okio. { *; }

-dontwarn okio.**

-ignorewarnings -keep class * { public private *; }

#RxJava
-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-dontnote rx.internal.util.PlatformDependent


-keep public class * {
    public <methods>;
    public <fields>;
}
