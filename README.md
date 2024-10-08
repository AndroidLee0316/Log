1.在App工程Application中进行初始化
  private static void initXLog(Context context) {
      LogConfiguration config = new LogConfiguration.Builder()
              .tag(DEFAULT_LOG_TAG)                                  // 指定 TAG，默认为 "X-LOG"
              .threadInfoEnable()                                    // 允许打印线程信息，默认禁止
              .stackTraceEnable(2)                                   // 允许打印深度为2的调用栈信息，默认禁止
              .borderEnable()                                        // 允许打印日志边框，默认禁止
              .jsonFormatter(new MyJsonFormatter())                  // 指定 JSON 格式化器，默认为 DefaultJsonFormatter
              .xmlFormatter(new MyXmlFormatter())                    // 指定 XML 格式化器，默认为 DefaultXmlFormatter
              .throwableFormatter(new MyThrowableFormatter())        // 指定可抛出异常格式化器，默认为 DefaultThrowableFormatter
              .threadFormatter(new MyThreadFormatter())              // 指定线程信息格式化器，默认为 DefaultThreadFormatter
              .stackTraceFormatter(new MyStackTraceFormatter())      // 指定调用栈信息格式化器，默认为 DefaultStackTraceFormatter
              .borderFormatter(new MyBoardFormatter())               // 指定边框格式化器，默认为 DefaultBorderFormatter
              .addObjectFormatter(AnyClass.class,                    // 为指定类添加格式化器
                      new AnyClassObjectFormatter())                 // 默认使用 Object.toString()
              .build();
      Printer printer;
      if(BuildConfig.DEBUG){                                         //测试环境直接打印
        printer = new AndroidPrinter();                              // 通过 android.util.Log 打印日志的打印器
      }else {                                                        //正式环境文件保存
        String logFileDir = SDCardUtils.getAppDir(context, SDCARD_LOG_FILE_DIR);
        printer = new FilePrinter                                    // 打印日志到文件的打印器
                .Builder(logFileDir)                                 // 指定保存日志文件的路径
                .fileNameGenerator(new DateFileNameGenerator())      // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
                .fileSaveTime(7)                                     //文件保存时间
                .backupStrategy(new MyBackupStrategy())              // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
                .logFlattener(new MyLogFlattener())                  // 指定日志平铺器，默认为 DefaultLogFlattener
                .build();
      }
      PascLog.init(context, SYSTEM_ID, config, printer);
    }

2.封装支持默认Log不同类型的日志，如
   PascLog.d() 调试日志
   PascLog.e() 错误日志
   PascLog.i() 信息日志
   PascLog.w() 提醒日志
   PascLog.json() 将字符串按json格式输出
   PascLog.xml()  将字符串按xml格式输出

   打印日志允许自定义TAG，PascLog.tag()

3.与服务器交互
  (1) 主动上传: 调用 PascLogHttp.uploadFile(final String mobile, final String uploadUrl,
                                  final String uploadType, final OnHttpResultListener listener);
        参数说明: mobile      用户标识（目前是手机号）
                 uploadUrl   上传日志URL
                 uploadType  上传类型（0:主动 1:被动）
                 listener    网络请求回调（可不传）

 （2）被动上传(服务器下发收集指令): 调用 PascLogHttp.queryInfoAndUploadFile(final String mobile, final String queryInfoUrl,
                              final String uploadUrl, final String uploadType, final OnHttpResultListener listener)
                              这个是查询到需要上传时，直接将日志上传，这个过程一般对于用户是透明的，所以直接将查询和上传操作合到一起

        参数说明: mobile        用户标识（目前是手机号)
                 queryInfoUrl  查询是否需要上传日志URL
                 uploadUrl     上传日志URL
                 uploadType    上传类型（0:主动 1:被动）
                 listener      网络请求回调（可不传）[config](..%2F..%2Fpascnet%2F131%2F.git%2Fconfig)


 4.局部用法
     Logger partial = PascLog.tag("PARTIAL-LOG")
          ... // 其他配置
          .build();
     然后对该 Logger 进行局部范围的使用，所有打印日志的相关方法都跟 PascLog 类里的一模一样。
     partial.d("Simple message 1");
     partial.d("Simple message 2");

#   L o g  
 