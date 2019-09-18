# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Fadhlan/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html


# RETROFIT
#-dontwarn okio.**
#-dontwarn javax.annotation.**
#-dontwarn retrofit2.Platform.**
#-dontwarn retrofit2.Platform$Java8
#-keepattributes Signature
#-keepattributes Exceptions
#
## keep everything in this package from being removed or renamed
#-keep class retrofit2. { *; }
#
## keep everything in this package from being renamed only
#-keepnames class retrofit2. { *; }
#
## Platform calls Class.forName on types which do not exist on Android to determine platform.
#-dontnote retrofit2.Platform
## Platform used when running on Java 8 VMs. Will not be used at runtime.
#-dontwarn retrofit2.Platform$Java8
## Retain generic type information for use by reflection by converters and adapters.
#-keepattributes Signature
## Retain declared checked exceptions for use by a Proxy instance.
#-keepattributes Exceptions
#
#
##OKHTTP
##-dontwarn okio.**
##-dontwarn javax.annotation.Nullable
##-dontwarn javax.annotation.ParametersAreNonnullByDefault
#-keepattributes Annotation
#-keep class okhttp3. { *; }
#-keep interface okhttp3. { *; }
#-keepnames class okhttp3. { *; }
#-dontwarn okhttp3.
#-dontwarn okio.
#
#-keepattributes Signature
#-keepattributes Annotation
#-keep class okhttp3.** { *; }
#-keep interface okhttp3.** { *; }
#-dontwarn okhttp3.**
#-dontwarn okio.**
#
## GLIDE
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.AppGlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#  **[] $VALUES;
#  public *;
#}
#-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule
#
#
#-keepattributes *Annotation*,EnclosingMethod,Signature
#-keepnames class com.fasterxml.jackson.** { *; }
#-dontwarn com.fasterxml.jackson.databind.**
#-keep class org.codehaus.** { *; }
#-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
#    public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }
#-keep public class your.class.** {*;}
#
#-dontwarn org.freemarker.**
#
## for DexGuard only
##-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
#
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
-keep public class * extends java.lang.Exception
-keep class com.crashlytics. { *; }
-dontwarn com.crashlytics.

-keep class okio. { *; }
-keepnames class okio. { *; }
-dontwarn okio.**

#-ignorewarnings -keep class * { public private *; }





# Add *one* of the following rules to your Proguard configuration file.
# Alternatively, you can annotate classes and class members with @android.support.annotation.Keep
#
#-keep class io.taptalk.TapTalk.Helper.TapTalk { *; }
#-keep class io.taptalk.TapTalk.Helper.TapTalk.** { *; }
#-keep class io.taptalk.TapTalk.Listener.TapListener { *; }
#
#-keep class io.taptalk.TapTalk.Manager.TapCoreMessageManager { *; }
#-keep class io.taptalk.TapTalk.Manager.TapCoreContactManager { *; }
#-keep class io.taptalk.TapTalk.Manager.TapCoreChatRoomManager { *; }
#-keep class io.taptalk.TapTalk.Manager.TapCoreProjectConfigsManager { *; }
#-keep class io.taptalk.TapTalk.Manager.TapCoreRoomListManager { *; }
#
#-keepclassmembers class ** {
#    public void on*(***);
#}
#
#-keepclassmembers enum * { *; }


# Proguard for Taptalk apps Class
-keep class io.** { *; }

##-keep class io.taptalk.TapTalk.View.** { *; }
#-keepnames class io.taptalk.TapTalk.View.** { *; }
##-keep class io.taptalk.TapTalk.Helper.** { *; }
#-keepnames class io.taptalk.TapTalk.Helper.** { *; }
##-keep class io.taptalk.TapTalk.Manager.** { *; }
#-keepnames class io.taptalk.TapTalk.Manager.** { *; }
##-keep class io.taptalk.TapTalk.Model.** { *; }
#-keepnames class io.taptalk.TapTalk.Model.** { *; }
##-keep class io.taptalk.TapTalk.Data.** { *; }
#-keepnames class io.taptalk.TapTalk.Data.** { *; }
##-keep class io.taptalk.TapTalk.ViewModel.** { *; }
#-keepnames class io.taptalk.TapTalk.ViewModel.** { *; }
##-keep class io.taptalk.TapTalk.API.** { *; }
#-keepnames class io.taptalk.TapTalk.API.** { *; }
##-keep class io.taptalk.TapTalk.Interface.** { *; }
#-keepnames class io.taptalk.TapTalk.Interface.** { *; }
##-keep class io.taptalk.TapTalk.Listener.** { *; }
#-keepnames class io.taptalk.TapTalk.Listener.** { *; }
##-keep class io.taptalk.TapTalk.Const.** { *; }
#-keepnames class io.taptalk.TapTalk.Const.** { *; }
##-keep class io.taptalk.TapTalk.DiffCallback.** { *; }
#-keepnames class io.taptalk.TapTalk.DiffCallback.** { *; }
##-keep class io.taptalk.TapTalk.Exception.** { *; }
#-keepnames class io.taptalk.TapTalk.Exception.** { *; }
##-keep class io.taptalk.TapTalk.Firebase.** { *; }
#-keepnames class io.taptalk.TapTalk.Firebase.** { *; }
##-keep class io.taptalk.TapTalk.BroadcastReceiver.** { *; }
#-keepnames class io.taptalk.TapTalk.BroadcastReceiver.** { *; }




# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# okhttp3
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform


# rxjava
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

-keep class com.orhanobut.hawk. { *; }
-keepnames class com.orhanobut.hawk. { *; }