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

#EventBus
-keep class de.greenrobot.event.**{*;}
-dontwarn de.greenrobot.event.**
-keepclassmembers class ** {
public void onEvent*(**);
}

#commons
-keep class org.apache.commons.**{*;}
-dontwarn org.apache.commons.**

#fastJson
-keep class com.alibaba.fastjson.**{*;}
-dontwarn com.alibaba.fastjson.**

#netty
-keep class io.netty.** { *; }
-dontwarn io.netty.**

#mqtt
-keep class org.eclipse.paho.client.mqttv3.** { *; }
-dontwarn org.eclipse.paho.client.mqttv3.**
#remove from 1.13.13
#-keep class org.eclipse.paho.android.service.** { *; }
#-dontwarn org.eclipse.paho.android.service.**

-dontwarn okio.**
-dontwarn rx.**
-dontwarn javax.annotation.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class okio.** { *; }
-dontwarn com.squareup.okhttp.**

-keep class com.tuya.smart.**{*;}
-dontwarn com.tuya.smart.**

#Orbivo
#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
 #优化  不优化输入的类文件
-dontoptimize
 #预校验
-dontpreverify
 #混淆时是否记录日志
-verbose
 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*
# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
#忽略警告
-ignorewarnings
#####################记录生成的日志数据,gradle build时在本项目根目录输出################
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt
#####################记录生成的日志数据，gradle build时 在本项目根目录输出-end################
################<span></span>混淆保护自己项目的部分代码以及引用的第三方jar包library#########################

####不混淆第三方lib包中的类
-dontwarn com.loopj.android.http.**
-keep class com.loopj.android.http.** { *; }

-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.leakcanary.** { *; }

-dontwarn de.greenrobot.event.**
-keep class de.greenrobot.event.** { *; }

-dontwarn com.google.gson.**
-keep class com.google.gson.** { *; }

-dontwarn org.apache.**
-keep class org.apache.** { *; }
-dontwarn org.apache.commons.lang.**
-keep class org.apache.commons.lang.** { *; }

-dontwarn org.apache.mina.**
-keep class org.apache.mina.** {*;}

-dontwarn org.slf4j.**
-keep class org.slf4j.** {*;}

#如果引用了v4或者v7包
-dontwarn android.support.**
-keep class android.support.v7.** { *; }
-keep class android.support.v4.** { *; }

#-keep class com.orvibo.homemate.application.** { *; }
#-keep class com.orvibo.homemate.api.** { *; }
#-keep class com.orvibo.homemate.api.*$* { *; }
#-keep class com.orvibo.homemate.data.** { *; }
#-keep class com.orvibo.homemate.util.MD5{*;}
-keepattributes Exceptions,InnerClasses,...
#-keep class com.orvibo.homemate.api.DeviceApi$* { *;}
#-keep class com.orvibo.homemate.api.HouseApi$* { *;}
#-keep class com.orvibo.homemate.api.IrApi$* { *;}
#-keep class com.orvibo.homemate.api.PushApi$* { *;}
#-keep class com.orvibo.homemate.api.SmartSceneApi$* { *;}
#-keep class com.orvibo.homemate.api.TimerApi$* { *;}
#-keep class com.orvibo.homemate.api.UserApi$* { *;}
-keep class com.p2p.**{ *; }
-keep class com.utility.**{ *; }

#coco配置相关类不混淆
#-keep class com.orvibo.homemate.ap.**{*;}
#-keep class com.orvibo.homemate.socket.MinaSocket{*;}
#-keep class com.orvibo.homemate.core.Reconnect{*;}
#-keep class com.orvibo.homemate.core.Load{*;}
#-keep class com.orvibo.homemate.model.**{*;}
#-keep class com.orvibo.homemate.core.load$* {
    *;
}
#-keep class com.orvibo.homemate.core.product$* {
#    *;
#}

#部分类不进行混淆
#-keep class com.orvibo.homemate.event.BaseEvent{ *; }
#-keep class com.orvibo.homemate.common.lib.log.MyLogger{ *; }
#-keep class com.orvibo.homemate.sharedPreferences.UserCache{ *; }
#-keep class com.orvibo.homemate.util.SdkCompat{*;}


#eventbus中回调不混淆
-keepclassmembers class ** {
    public void onEvent*(**);
}
-keepclassmembers class ** {
public void xxxxxx(**);
}


-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}
-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}
##不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}
#避免混淆泛型 如果混淆报错建议关掉
-keepattributes Signature

-dontwarn com.danale.oss.**

-dontwarn com.danale.video.**

-dontwarn com.ezviz.hcnetsdk.**

-dontwarn com.videogo.**

-dontwarn com.momentum.video.device.engine.task.**

#-dontwarn com.orvibo.homemate.api.MagicCubeApi
#
#-dontwarn com.orvibo.homemate.bo.InfoPushPropertyReportInfo
#
#-dontwarn com.orvibo.homemate.camera.**
#
#-dontwarn com.orvibo.homemate.core.g
#
#-dontwarn com.orvibo.homemate.model.CameraSearch
#
#-dontwarn com.orvibo.homemate.model.push.InfoPushManager

