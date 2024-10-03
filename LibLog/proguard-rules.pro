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
-optimizationpasses 5
-useuniqueclassmembernames
-keepattributes SourceFile,LineNumberTable
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-dontoptimize
-verbose
-ignorewarnings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep class com.pasc.lib.log.PascLog {
    public *;
}
-keep class com.pasc.lib.log.LogConfiguration {
    public *;
}
-keep class com.pasc.lib.log.LogConfiguration$* {
    public *;
}
-keep class com.pasc.lib.log.printer.**{public *;}
-keep class ccom.pasc.lib.log.interceptor.*{public *;}
-keep class com.pasc.lib.log.flattener.*{public *;}
-keep class com.pasc.lib.log.formatter.**{public *;}
-keep class com.pasc.lib.log.net.**{public *;}
-keep class com.pasc.lib.log.**{public *;}
-keep public class com.pasc.lib.log.LogLevel {
    public *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
