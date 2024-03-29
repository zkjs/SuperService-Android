 # 指定代码的压缩级别
-optimizationpasses 5

 # 是否使用大小写混合
-dontusemixedcaseclassnames

 # 是否混淆第三方jar
-dontskipnonpubliclibraryclasses

 # 混淆时是否做预校验
-dontpreverify

 # 混淆时是否记录日志
-verbose

 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

 # 保持四大组件以及android原生类和派生类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

# 忽略警告
-ignorewarning

 # 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

 # 保持自定义控件重载方法不被混淆
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

 # 保持自定义控件另一重载方法不被混淆
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

 # 保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

 # 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

 # 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

 # 保持签名类不被混淆
-keepattributes Signature

 # 保持注解不被混淆
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.guosen.crm.common.ui.launcher.LauncherPageGridView.** {*;}

# 防止R被混淆，R类反射混淆，找不到资源ID
-keep class **.R$* {
    *;
}

#保留网络请求的实体不被混淆
-keep class com.zkjinshi.superservice.bean.** { *; }
-keep class com.zkjinshi.superservice.entity.** { *; }
-keep class com.zkjinshi.superservice.vo.** { *; }
-keep class com.zkjinshi.superservice.response.** { *; }
-keep class com.zkjinshi.superservice.net.** { *; }
-keep class com.zkjinshi.superservice.fragment.**{ *; }
-keep class com.zkjinshi.superservice.activity.**{ *; }
-keep class io.codetail.animation.arcanimator.** { *; }
-keep class com.zkjinshi.superservice.ext.**{ *; }

#保留扩展控件不被混淆
-keep class android.support.**{ *; }
-dontwarn android.support.**

#---根据 Android Dependencies 配置library工程包start---#

 #1 自定义日期选择器
-dontwarn com.andexert.calendarlistview.**
-keep class com.andexert.calendarlistview.** { *; }

 #2 自定义标签控件
-dontwarn me.kaede.tagviewr.**
-keep class me.kaede.tagview.** { *; }

 #3 自定义文件选择器
-dontwarn com.zkjinshi.filechoser.**
-keep class com.zkjinshi.filechoser.** { *; }

 #4 自定义图片选择器
-dontwarn me.nereo.multi_image_selector.**
-keep class me.nereo.multi_image_selector.** { *; }

 #5 中科金石基类
-dontwarn com.zkjinshi.base.**
-keep class com.zkjinshi.base.** { *; }

-keep class com.facebook.**{*;}

#---配置library工程包over---#

#----根据 Android Private Libraries 配置lib jar包start---#

 #1 短信验证包
-dontwarn com.cloopen.rest.**
-keep class com.cloopen.rest.** { *; }
-dontwarn de.mindpipe.android.logging.log4j.**
-keep class de.mindpipe.android.logging.log4j.** { *; }
-dontwarn org.**
-keep class org.** { *; }
-dontwarn ytx.org.apache.http.**
-keep class ytx.org.apache.http.** { *; }

 #2 mime网络框架包
-dontwarn org.apache.http.entity.mime.**
-keep class org.apache.http.entity.mime.**{ *; }

 #3 xls/xlsx电子表格解析包
-dontwarn jxl.**
-keep class jxl.**{ *; }

 #4 云测包
-dontwarn com.testin.agent.**
-keep class com.testin.agent.** { *; }

#云巴
-dontwarn io.yunba.android.**
-keep class io.yunba.android.**{ *; }
-dontwarn org.eclipse.paho.client.mqttv3.**
-keep class org.eclipse.paho.client.mqttv3.**{ *; }

 #Pyxis sdk
-keep class com.zkjinshi.pyxis.bluetooth.** { *; }
-keep class com.zkjinshi.pyxis.utils.** { *; }
-keep class org.altbeacon.**{ *; }

 #5 环信app打包混淆
-keep class com.easemob.** {*;}
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}
-dontwarn  com.easemob.**
#2.0.9后的不需要加下面这个keep
#-keep class org.xbill.DNS.** {*;}
#另外，demo中发送表情的时候使用到反射，需要keep SmileUtils
-keep class com.easemob.chatuidemo.utils.SmileUtils {*;}
#注意前面的包名，如果把这个类复制到自己的项目底下，比如放在com.example.utils底下，应该这么写（实际要去掉#）
#-keep class com.example.utils.SmileUtils {*;}
#如果使用EaseUI库，需要这么写
-keep class com.easemob.easeui.utils.EaseSmileUtils {*;}

#2.0.9后加入语音通话功能，如需使用此功能的API，加入以下keep
-dontwarn ch.imvs.**
-dontwarn org.slf4j.**
-keep class org.ice4j.** {*;}
-keep class net.java.sip.** {*;}
-keep class org.webrtc.voiceengine.** {*;}
-keep class org.bitlet.** {*;}
-keep class org.slf4j.** {*;}
-keep class ch.imvs.** {*;}

 # 微信sdk不混淆
 -keep class com.tencent.** {*;}

 -dontwarn org.apache.commons.**
 -keep class org.apache.http.impl.client.**
 -dontwarn org.apache.commons.**
 -keep class com.blueware.** { *; }
 -dontwarn com.blueware.**
 -keepattributes Exceptions, Signature, InnerClasses
 -keepattributes SourceFile ,LineNumberTable

#---配置lib jar包over---#


