package com.pasc.sample.log;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.os.StrictMode;
import com.pasc.lib.log.LogConfiguration;
import com.pasc.lib.log.LogLevel;
import com.pasc.lib.log.PascLog;
import com.pasc.lib.log.flattener.Flattener;
import com.pasc.lib.log.printer.AndroidPrinter;
import com.pasc.lib.log.printer.Printer;
import com.pasc.lib.log.printer.file.FilePrinter;
import com.pasc.lib.log.printer.file.naming.DateFileNameGenerator;
import com.pasc.lib.net.NetConfig;
import com.pasc.lib.net.NetManager;
import com.pasc.sample.log.format.AnyClassObjectFormatter;
import com.pasc.sample.log.format.MyBoardFormatter;
import com.pasc.sample.log.format.MyJsonFormatter;
import com.pasc.sample.log.format.MyStackTraceFormatter;
import com.pasc.sample.log.format.MyThreadFormatter;
import com.pasc.sample.log.format.MyThrowableFormatter;
import com.pasc.sample.log.format.MyXmlFormatter;

/**
 * Created by lingchun147 on 2018/9/3.
 */
public class App extends Application {

  private static final String TAG = "App";

  private static String SDCARD_LOG_FILE_DIR = "Smart/log";//日志保存目录
  private static String DEFAULT_LOG_TAG = "smt";//日志tag
  private static String SYSTEM_ID = "wdsz";//日志搜集app系统标识

  @Override public void onCreate() {
    super.onCreate();
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectCustomSlowCalls() //API等级11，使用StrictMode.noteSlowCode
        .detectDiskReads()
        .detectDiskWrites()
        .detectNetwork()   // or .detectAll() for all detectable problems
        .penaltyLog() //在Logcat 中打印违规异常信息
        .penaltyFlashScreen() //API等级11
        .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectLeakedSqlLiteObjects()
        .detectLeakedClosableObjects() //API等级11
        .penaltyLog()
        .penaltyDeath()
        .build());
    initPascLog(this);
  }
  private void initPascLog(Context context) {
    LogConfiguration config = new LogConfiguration.Builder()
            .tag(DEFAULT_LOG_TAG)                                  // 指定 TAG，默认为 "X-LOG"
            .threadInfoEnable()                                    // 允许打印线程信息，默认禁止
            .stackTraceEnable(2)                                   // 允许打印深度为2的调用栈信息，默认禁止
            .borderEnable()            // 指定边框格式化器，默认为 DefaultBorderFormatter
            .addObjectFormatter(AnyClass.class,                    // 为指定类添加格式化器
                    new AnyClassObjectFormatter())                 // 默认使用 Object.toString()
            //.jsonFormatter(new MyJsonFormatter())                  // 指定 JSON 格式化器，默认为 DefaultJsonFormatter
            //.xmlFormatter(new MyXmlFormatter())                    // 指定 XML 格式化器，默认为 DefaultXmlFormatter
            //.throwableFormatter(new MyThrowableFormatter())        // 指定可抛出异常格式化器，默认为 DefaultThrowableFormatter
            //.threadFormatter(new MyThreadFormatter())              // 指定线程信息格式化器，默认为 DefaultThreadFormatter
            //.stackTraceFormatter(new MyStackTraceFormatter())      // 指定调用栈信息格式化器，默认为 DefaultStackTraceFormatter
            //.borderFormatter(new MyBoardFormatter())               // 指定边框格式化器，默认为 DefaultBorderFormatter
            //.addObjectFormatter(AnyClass.class,                    // 为指定类添加格式化器
            //        new AnyClassObjectFormatter())                 // 默认使用 Object.toString()
            .logLevel(LogLevel.ALL)
            .setBaseUrl(BuildConfig.HOST)
            .setReportLog(true)
            .setDebug(BuildConfig.DEBUG)
            .build();
    Printer printer;
    //if(BuildConfig.DEBUG){                                         //测试环境直接打印
    //  printer = new AndroidPrinter();                              // 通过 android.util.Log 打印日志的打印器
    //PascLog.openAndroidPrinter(true);
    //}else {                                                        //正式环境文件保存
      String logFileDir = SDCardUtils.getAppDir(context, SDCARD_LOG_FILE_DIR);
      printer = new FilePrinter                                    // 打印日志到文件的打印器
              .Builder(logFileDir)                                 // 指定保存日志文件的路径
              .fileNameGenerator(new DateFileNameGenerator())      // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
              .fileSaveTime(7)                                     //文件保存时间
              .backupStrategy(new MyBackupStrategy())              // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
              .logFlattener(new MyLogFlattener())                  // 指定日志平铺器，默认为 DefaultLogFlattener
              .build();
      PascLog.openFilePrinter(true);
    //}

    PascLog.init(context, SYSTEM_ID, config, printer);

    NetConfig.Builder builder = new NetConfig.Builder(this).baseUrl(BuildConfig.HOST);
    com.pasc.lib.net.HttpCommonParams.getInstance()
            .setInjectHandler(new CommonHeadersHandler(this));
    PinnerProxy.addCertPinner(builder);
    builder.isDebug(BuildConfig.DEBUG);
    NetConfig netConfig = builder.build();
    NetManager.init(netConfig);
  }

}
