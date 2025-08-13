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

#noinspection ShrinkerUnresolvedReference

-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keep class com.google.firebase.quickstart.database.viewholder.** {
    *;
}

-keepclassmembers class com.google.firebase.quickstart.database.models.** {
    *;
}
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.*
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.*

-keep class okio.** { *; }
-keepnames class okio.** { *; }
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

# Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
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
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Flowable
-keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Maybe
-keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Observable
-keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Single

# okhttp3
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# json
-keep class com.fasterxml.** { *; }
-dontwarn java.beans.ConstructorProperties
-dontwarn java.beans.Transient
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

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
    #noinspection ShrinkerUnresolvedReference
    long producerNode;
    long consumerNode;
}

-keep class com.orhanobut.hawk.** { *; }
-keepnames class com.orhanobut.hawk.** { *; }

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
