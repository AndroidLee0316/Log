
package com.pasc.lib.log;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import com.pasc.lib.log.formatter.border.BorderFormatter;
import com.pasc.lib.log.formatter.message.json.JsonFormatter;
import com.pasc.lib.log.formatter.message.object.ObjectFormatter;
import com.pasc.lib.log.formatter.message.throwable.ThrowableFormatter;
import com.pasc.lib.log.formatter.message.xml.XmlFormatter;
import com.pasc.lib.log.formatter.stacktrace.StackTraceFormatter;
import com.pasc.lib.log.formatter.thread.ThreadFormatter;
import com.pasc.lib.log.interceptor.Interceptor;
import com.pasc.lib.log.internal.DefaultsFactory;
import com.pasc.lib.log.internal.Platform;
import com.pasc.lib.log.internal.util.StackTraceUtil;
import com.pasc.lib.log.net.HttpURLManager;
import com.pasc.lib.log.printer.AndroidPrinter;
import com.pasc.lib.log.printer.Printer;
import com.pasc.lib.log.printer.PrinterSet;
import com.pasc.lib.log.printer.file.FilePrinter;
import com.pasc.lib.log.utils.CrashHandler;
import com.pasc.lib.log.utils.SDCardUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A log tool which can be used in android or java, the most important feature is it can print the
 * logs to multiple place in the same time, such as android shell, console and file, you can
 * even print the log to the remote server if you want, all of these can be done just within one
 * calling.
 * <br>Also, PascLog is very flexible, almost every component is replaceable.
 * <p>
 * <b>How to use in a general way:</b>
 * <p>
 * <b>1. Initial the log system.</b>
 * <br>Using one of
 * <br>{@link PascLog#init(Context, String)}
 * <br>{@link PascLog#init(Context, String, int)},
 * <br>{@link PascLog#init(Context, String, LogConfiguration)}
 * <br>{@link PascLog#init(Context, String, Printer...)},
 * <br>{@link PascLog#init(Context, String, int, Printer...)},
 * <br>{@link PascLog#init(Context, String, LogConfiguration, Printer...)},
 * <br>that will setup a {@link Logger} for a global usage.
 * If you want to use a customized configuration instead of the global one to log something, you can
 * start a customization logging.
 * <p>
 * For android, a best place to do the initialization is {@link Application#onCreate()}.
 * <p>
 * <b>2. Start to log.</b>
 * <br>{@link #v(String, Object...)}, {@link #v(String)} and {@link #v(String, Throwable)} are for
 * logging a {@link LogLevel#INFO} message.
 * <br>{@link #d(String, Object...)}, {@link #d(String)} and {@link #d(String, Throwable)} are for
 * logging a {@link LogLevel#DEBUG} message.
 * <br>{@link #i(String, Object...)}, {@link #i(String)} and {@link #i(String, Throwable)} are for
 * logging a {@link LogLevel#INFO} message.
 * <br>{@link #w(String, Object...)}, {@link #w(String)} and {@link #w(String, Throwable)} are for
 * logging a {@link LogLevel#WARN} message.
 * <br>{@link #e(String, Object...)}, {@link #e(String)} and {@link #e(String, Throwable)} are for
 * logging a {@link LogLevel#ERROR} message.
 * <br>{@link #log(int, String, Object...)}, {@link #log(int, String)} and
 * {@link #log(int, String, Throwable)} are for logging a specific level message.
 * <br>{@link #json(String)} is for logging a {@link LogLevel#DEBUG} JSON string.
 * <br>{@link #xml(String)} is for logging a {@link LogLevel#DEBUG} XML string.
 * <br> Also, you can directly log any object with specific log level, like {@link #v(Object)},
 * and any object array with specific log level, like {@link #v(Object[])}.
 * <p>
 * <b>How to use in a dynamically customizing way after initializing the log system:</b>
 * <p>
 * <b>1. Start a customization.</b>
 * <br>Call any of
 * <br>{@link #logLevel(int)}
 * <br>{@link #tag(String)},
 * <br>{@link #threadEnable()},
 * <br>{@link #threadDisable()},
 * <br>{@link #stackTraceEnable(int)},
 * <br>{@link #stackTraceDisable()},
 * <br>{@link #borderEnable()},
 * <br>{@link #borderDisable()},
 * <br>{@link #jsonFormatter(JsonFormatter)},
 * <br>{@link #xmlFormatter(XmlFormatter)},
 * <br>{@link #threadFormatter(ThreadFormatter)},
 * <br>{@link #stackTraceFormatter(StackTraceFormatter)},
 * <br>{@link #throwableFormatter(ThrowableFormatter)}
 * <br>{@link #borderFormatter(BorderFormatter)}
 * <br>{@link #addObjectFormatter(Class, ObjectFormatter)}
 * <br>{@link #addInterceptor(Interceptor)}
 * <br>{@link #printers(Printer...)},
 * <br>it will return a {@link Logger.Builder} object.
 * <p>
 * <b>2. Finish the customization.</b>
 * <br>Continue to setup other fields of the returned {@link Logger.Builder}.
 * <p>
 * <b>3. Build a dynamically generated {@link Logger}.</b>
 * <br>Call the {@link Logger.Builder#build()} of the returned {@link Logger.Builder}.
 * <p>
 * <b>4. Start to log.</b>
 * <br>The logging methods of a {@link Logger} is completely same as that ones in {@link PascLog}.
 * <br>As a convenience, you can ignore the step 3, just call the logging methods of
 * {@link Logger.Builder}, it will automatically build a {@link Logger} and call the target
 * logging method.
 * <p>
 * <b>Compatibility:</b>
 * <p>
 * In order to be compatible with {@link android.util.Log}, all the methods of
 * {@link android.util.Log} are supported here.
 * See:
 * <br>{@link Log#v(String, String)}, {@link Log#v(String, String, Throwable)}
 * <br>{@link Log#d(String, String)}, {@link Log#d(String, String, Throwable)}
 * <br>{@link Log#i(String, String)}, {@link Log#i(String, String, Throwable)}
 * <br>{@link Log#w(String, String)}, {@link Log#w(String, String, Throwable)}
 * <br>{@link Log#wtf(String, String)}, {@link Log#wtf(String, String, Throwable)}
 * <br>{@link Log#e(String, String)}, {@link Log#e(String, String, Throwable)}
 * <br>{@link Log#println(int, String, String)}
 * <br>{@link Log#isLoggable(String, int)}
 * <br>{@link Log#getStackTraceString(Throwable)}
 * <p>
 */
public class PascLog {

    /**
     * Global logger for all direct logging via {@link PascLog}.
     */
    private static Logger sLogger;
    private static final int LOG_FILE_SAVE_TIME = 7;
    public static final String SDCARD_LOG_FILE_DIR = "Smart/log";
    private static final String SDCARD_CRASH_LOG_FILE_DIR = "Smart/crash_log";
    public static final  String SDCARD_LOG_FOLDOR = "Smart/";
    private static final String LOG_KEY = "pasc_log";
    public static String sReportUrl;
    public static String sQueryUrl;
    public static boolean IS_DEBUG = false;

    /**
     * Global log configuration.
     */
    static LogConfiguration sLogConfiguration;

    /**
     * Global log printer.
     */
    static List<Printer> sPrinterList = new ArrayList<>();
    //    static Printer sPrinter;
    static FilePrinter sFilePrinter;
    static AndroidPrinter sAndroidPrinter;

    static volatile boolean sIsInitialized;
    public static Context mContext;
    public static String mSystemId;
    public static ReentrantReadWriteLock rwl;
    static ReentrantReadWriteLock sPrintRWL;
    private static boolean sIsPrintFile = false;
    private static boolean sIsPrintAndroid = false;
    private static boolean sIsReportLog = false;
    private static boolean sIsCrashLog = true;

    /**
     * Prevent instance.
     */
    private PascLog() {
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @since 1.3.0
     */
    public static void init(Context context, String systemId) {
        init(context, systemId, new LogConfiguration.Builder().build(),
                DefaultsFactory.createPrinter());
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logLevel the log level, logs with a lower level than which would not be printed
     */
    public static void init(Context context, String systemId, int logLevel) {
        init(context, systemId, new LogConfiguration.Builder().logLevel(logLevel).build(),
                DefaultsFactory.createPrinter());
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logLevel         the log level, logs with a lower level than which would not be printed
     * @param logConfiguration the log configuration
     * @deprecated the log level is part of log configuration now, use {@link #init(Context, String, LogConfiguration)}
     * instead, since 1.3.0
     */
    @Deprecated
    public static void init(Context context, String systemId, int logLevel,
                            LogConfiguration logConfiguration) {
        init(context, systemId,
                new LogConfiguration.Builder(logConfiguration).logLevel(logLevel).build());
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logConfiguration the log configuration
     * @since 1.3.0
     */
    public static void init(Context context, String systemId, LogConfiguration logConfiguration) {
        init(context, systemId, logConfiguration, DefaultsFactory.createPrinter());
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param printers the printers, each log would be printed by all of the printers
     * @since 1.3.0
     */
    public static void init(Context context, String systemId, Printer... printers) {
        init(context, systemId, new LogConfiguration.Builder().build(), printers);
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logLevel the log level, logs with a lower level than which would not be printed
     * @param printers the printers, each log would be printed by all of the printers
     */
    public static void init(Context context, String systemId, int logLevel, Printer... printers) {
        init(context, systemId, new LogConfiguration.Builder().logLevel(logLevel).build(),
                printers);
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logLevel         the log level, logs with a lower level than which would not be printed
     * @param logConfiguration the log configuration
     * @param printers         the printers, each log would be printed by all of the printers
     * @deprecated the log level is part of log configuration now,
     * use {@link #init(Context, String, LogConfiguration, Printer...)} instead, since 1.3.0
     */
    @Deprecated
    public static void init(Context context, String systemId, int logLevel,
                            LogConfiguration logConfiguration, Printer... printers) {
        init(context, systemId,
                new LogConfiguration.Builder(logConfiguration).logLevel(logLevel).build(),
                printers);
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logConfiguration the log configuration
     * @param printers         the printers, each log would be printed by all of the printers
     * @since 1.3.0
     */
    public static void init(Context context, String systemId, LogConfiguration logConfiguration,
                            Printer... printers) {
        if (sIsInitialized) {
            Platform.get().warn("PascLog is already initialized, do not initialize again");

        }else {
            mContext = context;
            mSystemId = systemId;
            rwl = new ReentrantReadWriteLock();
            sPrintRWL = new ReentrantReadWriteLock();
            sPrinterList.clear();
            if (printers != null) {
                sPrinterList.addAll(Arrays.asList(printers));
                for (Printer printer : printers) {
                    if (printer instanceof FilePrinter) {
                        sFilePrinter = (FilePrinter) printer;
                    } else if (printer instanceof AndroidPrinter) {
                        sAndroidPrinter = (AndroidPrinter) printer;
                    }
                }
            }

            if (sAndroidPrinter != null && !sIsPrintAndroid) {
                sPrinterList.remove(sAndroidPrinter);
            } else if (sAndroidPrinter == null && sIsPrintAndroid) {
                sAndroidPrinter = new AndroidPrinter();
                sPrinterList.add(sAndroidPrinter);
            }

            if (sFilePrinter != null && !sIsPrintFile) {
                sPrinterList.remove(sFilePrinter);
            } else if (sFilePrinter == null && sIsPrintFile) {
                sFilePrinter = createFilePrinter(context);
                sPrinterList.add(sFilePrinter);
            }
            if (sIsCrashLog) {
                CrashHandler.getInstance()
                        .init(context, sFilePrinter == null ? LOG_FILE_SAVE_TIME : sFilePrinter.getFileSaveTime(), SDCARD_CRASH_LOG_FILE_DIR);
            }

            if (logConfiguration == null) {
                throw new IllegalArgumentException("Please specify a LogConfiguration");
            }
            resetLogLevel(logConfiguration);
            sLogger = new Logger(sLogConfiguration, getPrintSet());
            sIsInitialized = true;
        }

        if (logConfiguration != null && !TextUtils.isEmpty(logConfiguration.baseUrl)) {
            HttpURLManager.setHOST(logConfiguration.baseUrl);
        }
        if (logConfiguration != null) {
            IS_DEBUG = logConfiguration.isDebug;
        }
    }

    private static void resetLogLevel(LogConfiguration configuration) {
        int level;
        if (android.util.Log.isLoggable(LOG_KEY, android.util.Log.VERBOSE)) {
            level = LogLevel.VERBOSE;
        } else if (android.util.Log.isLoggable(LOG_KEY, android.util.Log.DEBUG)) {
            level = LogLevel.DEBUG;
        } else if (android.util.Log.isLoggable(LOG_KEY, android.util.Log.INFO)) {
            level = configuration.logLevel;
        } else if (android.util.Log.isLoggable(LOG_KEY, android.util.Log.WARN)) {
            level = LogLevel.WARN;
        } else if (android.util.Log.isLoggable(LOG_KEY, android.util.Log.ERROR)) {
            level = LogLevel.ERROR;
        } else if (android.util.Log.isLoggable(LOG_KEY, android.util.Log.ASSERT)) {
            level = LogLevel.NONE;
        } else {
            level = configuration.logLevel;
        }
        if (configuration.logLevel != level) {
            sLogConfiguration = new LogConfiguration.Builder(configuration).logLevel(level).build();
        } else {
            sLogConfiguration = configuration;
        }
    }

    private static FilePrinter createFilePrinter(Context context) {
        return new FilePrinter
                .Builder(SDCardUtils.getAppDir(context, SDCARD_LOG_FILE_DIR))
                .fileSaveTime(LOG_FILE_SAVE_TIME)
                .build();
    }

    public static String getSdcardLogFileDir() {
        if (sFilePrinter != null) {
            return sFilePrinter.getFloderPath();
        }
        return null;
    }
    public static PrinterSet getPrintSet() {
        sPrintRWL.readLock().lock();
        int size = sPrinterList == null ? 0 : sPrinterList.size();
        Printer[] printers = null;
        if (size > 0) {
            printers = new Printer[size];
            sPrinterList.toArray(printers);
        }
        sPrintRWL.readLock().unlock();
        if (printers != null) {
            return new PrinterSet(printers);
        }
        return null;
    }

    /**
     * Throw an IllegalStateException if not initialized.
     */
    static void assertInitialization() {
        if (!sIsInitialized) {
            throw new IllegalStateException("Do you forget to initialize PascLog?");
        }
    }

    public static void openAndroidPrinter(boolean isOpen) {
        sIsPrintAndroid = isOpen;
        if (sIsInitialized) {
            boolean isChanged = false;
            sPrintRWL.writeLock().lock();
            if (isOpen) {
                if (sAndroidPrinter == null) {
                    sAndroidPrinter = new AndroidPrinter();
                    isChanged = sPrinterList.add(sAndroidPrinter);
                } else if (!sPrinterList.contains(sAndroidPrinter)) {
                    isChanged = sPrinterList.add(sAndroidPrinter);
                }
            } else if (sAndroidPrinter != null) {
                isChanged = sPrinterList.remove(sAndroidPrinter);
            }
            sPrintRWL.writeLock().unlock();
            if (isChanged) {
                if (sLogger != null) {
                    sLogger.setPrinter(getPrintSet());
                } else {
                    sLogger = new Logger(sLogConfiguration, getPrintSet());
                }
            }
        }
    }

    public static void openFilePrinter(boolean isOpen) {
        sIsPrintFile = isOpen;
        if (sIsInitialized) {
            boolean isChanged = false;
            sPrintRWL.writeLock().lock();
            if (isOpen) {
                if (sFilePrinter == null) {
                    sFilePrinter = createFilePrinter(mContext);
                    isChanged = sPrinterList.add(sFilePrinter);
                } else if (!sPrinterList.contains(sFilePrinter)) {
                    isChanged = sPrinterList.add(sFilePrinter);
                }
            } else if (sFilePrinter != null) {
                isChanged = sPrinterList.remove(sFilePrinter);
            }
            sPrintRWL.writeLock().unlock();
            if (isChanged) {
                if (sLogger != null) {
                    sLogger.setPrinter(getPrintSet());
                } else {
                    sLogger = new Logger(sLogConfiguration, getPrintSet());
                }
            }
        }
    }

    public static void openReportLog(boolean isReportLog) {
        sIsReportLog = isReportLog;
    }

    public static boolean isOpenReportLog() {
        return sIsReportLog;
    }

    public static void setReportUrl(String reportUrl, String queryUrl) {
        HttpURLManager.HOST = "";
        HttpURLManager.setUploadUrl(reportUrl);
        HttpURLManager.setQueryUserInfoUrl(queryUrl);
    }

    public static void openCatchCrash(boolean isCrashLog) {
        sIsCrashLog = isCrashLog;
        if (sIsInitialized) {
            if (isCrashLog) {
                CrashHandler.getInstance()
                        .init(mContext, sFilePrinter == null ? LOG_FILE_SAVE_TIME : sFilePrinter.getFileSaveTime(),
                                SDCardUtils.getAppDir(mContext, SDCARD_CRASH_LOG_FILE_DIR));
            } else {
                CrashHandler.getInstance().closeCatchCrash();
            }
        }
    }

    /**
     * Start to customize a {@link Logger} and set the log level.
     *
     * @param logLevel the log level to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     * @since 1.3.0
     */
    public static Logger.Builder logLevel(int logLevel) {
        return new Logger.Builder().logLevel(logLevel);
    }

    /**
     * Start to customize a {@link Logger} and set the tag.
     *
     * @param tag the tag to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder tag(String tag) {
        return new Logger.Builder().tag(tag);
    }

    /**
     * Start to customize a {@link Logger} and enable thread info.
     *
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder threadEnable() {
        return new Logger.Builder().threadEnable();
    }

    /**
     * Start to customize a {@link Logger} and disable thread info.
     *
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder threadDisable() {
        return new Logger.Builder().threadDisable();
    }

    /**
     * Start to customize a {@link Logger} and enable stack trace.
     *
     * @param depth the number of stack trace elements we should log, 0 if no limitation
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder stackTraceEnable(int depth) {
        return new Logger.Builder().stackTraceEnable(depth);
    }

    /**
     * Start to customize a {@link Logger} and enable stack trace.
     *
     * @param stackTraceOrigin the origin of stack trace elements from which we should NOT log,
     *                         it can be a package name like "com.elvishew.xlog", a class name
     *                         like "com.yourdomain.logWrapper", or something else between
     *                         package name and class name, like "com.yourdomain.".
     *                         It is mostly used when you are using a logger wrapper
     * @param depth            the number of stack trace elements we should log, 0 if no limitation
     * @return the {@link Logger.Builder} to build the {@link Logger}
     * @since 1.4.0
     */
    public static Logger.Builder stackTraceEnable(String stackTraceOrigin, int depth) {
        return new Logger.Builder().stackTraceEnable(stackTraceOrigin, depth);
    }

    /**
     * Start to customize a {@link Logger} and disable stack trace.
     *
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder stackTraceDisable() {
        return new Logger.Builder().stackTraceDisable();
    }

    /**
     * Start to customize a {@link Logger} and enable border.
     *
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder borderEnable() {
        return new Logger.Builder().borderEnable();
    }

    /**
     * Start to customize a {@link Logger} and disable border.
     *
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder borderDisable() {
        return new Logger.Builder().borderDisable();
    }

    /**
     * Start to customize a {@link Logger} and set the {@link JsonFormatter}.
     *
     * @param jsonFormatter the {@link JsonFormatter} to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder jsonFormatter(JsonFormatter jsonFormatter) {
        return new Logger.Builder().jsonFormatter(jsonFormatter);
    }

    /**
     * Start to customize a {@link Logger} and set the {@link XmlFormatter}.
     *
     * @param xmlFormatter the {@link XmlFormatter} to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder xmlFormatter(XmlFormatter xmlFormatter) {
        return new Logger.Builder().xmlFormatter(xmlFormatter);
    }

    /**
     * Start to customize a {@link Logger} and set the {@link ThrowableFormatter}.
     *
     * @param throwableFormatter the {@link ThrowableFormatter} to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder throwableFormatter(ThrowableFormatter throwableFormatter) {
        return new Logger.Builder().throwableFormatter(throwableFormatter);
    }

    /**
     * Start to customize a {@link Logger} and set the {@link ThreadFormatter}.
     *
     * @param threadFormatter the {@link ThreadFormatter} to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder threadFormatter(ThreadFormatter threadFormatter) {
        return new Logger.Builder().threadFormatter(threadFormatter);
    }

    /**
     * Start to customize a {@link Logger} and set the {@link StackTraceFormatter}.
     *
     * @param stackTraceFormatter the {@link StackTraceFormatter} to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder stackTraceFormatter(StackTraceFormatter stackTraceFormatter) {
        return new Logger.Builder().stackTraceFormatter(stackTraceFormatter);
    }

    /**
     * Start to customize a {@link Logger} and set the {@link BorderFormatter}.
     *
     * @param borderFormatter the {@link BorderFormatter} to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder borderFormatter(BorderFormatter borderFormatter) {
        return new Logger.Builder().borderFormatter(borderFormatter);
    }

    /**
     * Start to customize a {@link Logger} and add an object formatter for specific class of object.
     *
     * @param objectClass     the class of object
     * @param objectFormatter the object formatter to add
     * @param <T>             the type of object
     * @return the {@link Logger.Builder} to build the {@link Logger}
     * @since 1.1.0
     */
    public static <T> Logger.Builder addObjectFormatter(Class<T> objectClass,
                                                        ObjectFormatter<? super T> objectFormatter) {
        return new Logger.Builder().addObjectFormatter(objectClass, objectFormatter);
    }

    /**
     * Start to customize a {@link Logger} and add an interceptor.
     *
     * @param interceptor the interceptor to add
     * @return the {@link Logger.Builder} to build the {@link Logger}
     * @since 1.3.0
     */
    public static Logger.Builder addInterceptor(Interceptor interceptor) {
        return new Logger.Builder().addInterceptor(interceptor);
    }

    /**
     * Start to customize a {@link Logger} and set the {@link Printer} array.
     *
     * @param printers the {@link Printer} array to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder printers(Printer... printers) {
        return new Logger.Builder().printers(printers);
    }

    /**
     * Log an object with level {@link LogLevel#VERBOSE}.
     *
     * @param object the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.1.0
     */
    public static void v(Object object) {
        assertInitialization();
        sLogger.v(null, object);
    }

    /**
     * Log an object with level {@link LogLevel#VERBOSE}.
     *
     * @param tag    tag
     * @param object the object to log
     */
    public static void v(String tag, Object object) {
        assertInitialization();
        sLogger.v(tag, object);
    }

    /**
     * Log an array with level {@link LogLevel#VERBOSE}.
     *
     * @param array the array to log
     */
    public static void v(Object[] array) {
        assertInitialization();
        sLogger.v(null, array);
    }

    /**
     * Log an array with level {@link LogLevel#VERBOSE}.
     *
     * @param tag   the tag
     * @param array the array to log
     */
    public static void v(String tag, Object[] array) {
        assertInitialization();
        sLogger.v(tag, array);
    }

    /**
     * Log a message with level {@link LogLevel#VERBOSE}.
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void v(Object args, String format) {
        assertInitialization();
        sLogger.v(null, format, args);
    }

    /**
     * Log a message with level {@link LogLevel#VERBOSE}.
     *
     * @param tag    the tag
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void v(String tag, String format, Object... args) {
        assertInitialization();
        sLogger.v(tag, format, args);
    }

    /**
     * Log a message with level {@link LogLevel#VERBOSE}.
     *
     * @param msg the message to log
     */
    public static void v(String msg) {
        assertInitialization();
        sLogger.v(null, msg);
    }

    /**
     * Log a message with level {@link LogLevel#VERBOSE}.
     *
     * @param tag the tag
     * @param msg the message to log
     */
    public static void v(String tag, String msg) {
        assertInitialization();
        sLogger.v(tag, msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#VERBOSE}.
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void v(String msg, Throwable tr) {
        assertInitialization();
        sLogger.v(null, msg, tr);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#VERBOSE}.
     *
     * @param tag the tag
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void v(String tag, String msg, Throwable tr) {
        assertInitialization();
        sLogger.v(tag, msg, tr);
    }

    /**
     * Log an object with level {@link LogLevel#DEBUG}.
     *
     * @param object the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.1.0
     */
    public static void d(Object object) {
        assertInitialization();
        sLogger.d(null, object);
    }

    /**
     * Log an object with level {@link LogLevel#DEBUG}.
     *
     * @param tag    the tag
     * @param object the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.1.0
     */
    public static void d(String tag, Object object) {
        assertInitialization();
        sLogger.d(tag, object);
    }

    /**
     * Log an array with level {@link LogLevel#DEBUG}.
     *
     * @param array the array to log
     */
    public static void d(Object[] array) {
        assertInitialization();
        sLogger.d(null, array);
    }

    /**
     * Log an array with level {@link LogLevel#DEBUG}.
     *
     * @param tag   the tag
     * @param array the array to log
     */
    public static void d(String tag, Object[] array) {
        assertInitialization();
        sLogger.d(tag, array);
    }

    /**
     * Log a message with level {@link LogLevel#DEBUG}.
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void d(Object[] args, String format) {
        assertInitialization();
        sLogger.d(null, format, args);
    }

    /**
     * Log a message with level {@link LogLevel#DEBUG}.
     *
     * @param tag    the tag
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void d(String tag, String format, Object... args) {
        assertInitialization();
        sLogger.d(tag, format, args);
    }

    /**
     * Log a message with level {@link LogLevel#DEBUG}.
     *
     * @param msg the message to log
     */
    public static void d(String msg) {
        assertInitialization();
        sLogger.d(null, msg);
    }

    /**
     * Log a message with level {@link LogLevel#DEBUG}.
     *
     * @param tag the tag
     * @param msg the message to log
     */
    public static void d(String tag, String msg) {
        assertInitialization();
        sLogger.d(tag, msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#DEBUG}.
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void d(String msg, Throwable tr) {
        assertInitialization();
        sLogger.d(null, msg, tr);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#DEBUG}.
     *
     * @param tag the tag
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void d(String tag, String msg, Throwable tr) {
        assertInitialization();
        sLogger.d(tag, msg, tr);
    }

    /**
     * Log an object with level {@link LogLevel#INFO}.
     *
     * @param object the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.1.0
     */
    public static void i(Object object) {
        assertInitialization();
        sLogger.i(null, object);
    }

    /**
     * Log an object with level {@link LogLevel#INFO}.
     *
     * @param tag    the tag
     * @param object the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.1.0
     */
    public static void i(String tag, Object object) {
        assertInitialization();
        sLogger.i(tag, object);
    }

    /**
     * Log an array with level {@link LogLevel#INFO}.
     *
     * @param array the array to log
     */
    public static void i(Object[] array) {
        assertInitialization();
        sLogger.i(null, array);
    }

    /**
     * Log an array with level {@link LogLevel#INFO}.
     *
     * @param tag   the tag
     * @param array the array to log
     */
    public static void i(String tag, Object[] array) {
        assertInitialization();
        sLogger.i(tag, array);
    }

    /**
     * Log a message with level {@link LogLevel#INFO}.
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void i(Object[] args, String format) {
        assertInitialization();
        sLogger.i(null, format, args);
    }

    /**
     * Log a message with level {@link LogLevel#INFO}.
     *
     * @param tag    the tag
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void i(String tag, String format, Object... args) {
        assertInitialization();
        sLogger.i(tag, format, args);
    }


    /**
     * Log a message with level {@link LogLevel#INFO}.
     *
     * @param msg the message to log
     */
    public static void i(String msg) {
        assertInitialization();
        sLogger.i(null, msg);
    }

    /**
     * Log a message with level {@link LogLevel#INFO}.
     *
     * @param tag the tag
     * @param msg the message to log
     */
    public static void i(String tag, String msg) {
        assertInitialization();
        sLogger.i(tag, msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#INFO}.
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void i(String msg, Throwable tr) {
        assertInitialization();
        sLogger.i(null, msg, tr);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#INFO}.
     *
     * @param tag the tag
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void i(String tag, String msg, Throwable tr) {
        assertInitialization();
        sLogger.i(tag, msg, tr);
    }

    /**
     * Log an object with level {@link LogLevel#WARN}.
     *
     * @param object the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.1.0
     */
    public static void w(Object object) {
        assertInitialization();
        sLogger.w(null, object);
    }

    /**
     * Log an object with level {@link LogLevel#WARN}.
     *
     * @param tag    the tag
     * @param object the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.1.0
     */
    public static void w(String tag, Object object) {
        assertInitialization();
        sLogger.w(tag, object);
    }

    /**
     * Log an array with level {@link LogLevel#WARN}.
     *
     * @param array the array to log
     */
    public static void w(Object[] array) {
        assertInitialization();
        sLogger.w(null, array);
    }

    /**
     * Log an array with level {@link LogLevel#WARN}.
     *
     * @param tag   the tag
     * @param array the array to log
     */
    public static void w(String tag, Object[] array) {
        assertInitialization();
        sLogger.w(tag, array);
    }


    /**
     * Log a message with level {@link LogLevel#WARN}.
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void w(Object[] args, String format) {
        assertInitialization();
        sLogger.w(null, format, args);
    }

    /**
     * Log a message with level {@link LogLevel#WARN}.
     *
     * @param tag    the tag
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void w(String tag, String format, Object... args) {
        assertInitialization();
        sLogger.w(tag, format, args);
    }

    /**
     * Log a message with level {@link LogLevel#WARN}.
     *
     * @param msg the message to log
     */
    public static void w(String msg) {
        assertInitialization();
        sLogger.w(null, msg);
    }

    /**
     * Log a message with level {@link LogLevel#WARN}.
     *
     * @param tag the tag
     * @param msg the message to log
     */
    public static void w(String tag, String msg) {
        assertInitialization();
        sLogger.w(tag, msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#WARN}.
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void w(String msg, Throwable tr) {
        assertInitialization();
        sLogger.w(null, msg, tr);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#WARN}.
     *
     * @param tag the tag
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void w(String tag, String msg, Throwable tr) {
        assertInitialization();
        sLogger.w(tag, msg, tr);
    }

    /**
     * Log an object with level {@link LogLevel#ERROR}.
     *
     * @param object the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.1.0
     */
    public static void e(Object object) {
        assertInitialization();
        sLogger.e(null, object);
    }

    /**
     * Log an object with level {@link LogLevel#ERROR}.
     *
     * @param tag    the tag
     * @param object the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.1.0
     */
    public static void e(String tag, Object object) {
        assertInitialization();
        sLogger.e(tag, object);
    }

    /**
     * Log an array with level {@link LogLevel#ERROR}.
     *
     * @param array the array to log
     */
    public static void e(Object[] array) {
        assertInitialization();
        sLogger.e(null, array);
    }

    /**
     * Log an array with level {@link LogLevel#ERROR}.
     *
     * @param tag   the tag
     * @param array the array to log
     */
    public static void e(String tag, Object[] array) {
        assertInitialization();
        sLogger.e(tag, array);
    }

    /**
     * Log a message with level {@link LogLevel#ERROR}.
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void e(Object[] args, String format) {
        assertInitialization();
        sLogger.e(null, format, args);
    }

    /**
     * Log a message with level {@link LogLevel#ERROR}.
     *
     * @param tag
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void e(String tag, String format, Object... args) {
        assertInitialization();
        sLogger.e(tag, format, args);
    }

    /**
     * Log a message with level {@link LogLevel#ERROR}.
     *
     * @param msg the message to log
     */
    public static void e(String msg) {
        assertInitialization();
        sLogger.e(null, msg);
    }

    /**
     * Log a message with level {@link LogLevel#ERROR}.
     *
     * @param tag the tag
     * @param msg the message to log
     */
    public static void e(String tag, String msg) {
        assertInitialization();
        sLogger.e(tag, msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#ERROR}.
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void e(String msg, Throwable tr) {
        assertInitialization();
        sLogger.e(null, msg, tr);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#ERROR}.
     *
     * @param tag the tag
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void e(String tag, String msg, Throwable tr) {
        assertInitialization();
        sLogger.e(tag, msg, tr);
    }

    /**
     * Log an object with specific log level.
     *
     * @param logLevel the specific log level
     * @param object   the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.4.0
     */
    public static void log(int logLevel, Object object) {
        assertInitialization();
        sLogger.log(null, logLevel, object);
    }

    /**
     * Log an object with specific log level.
     *
     * @param tag      the tag
     * @param logLevel the specific log level
     * @param object   the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.4.0
     */
    public static void log(String tag, int logLevel, Object object) {
        assertInitialization();
        sLogger.log(tag, logLevel, object);
    }

    /**
     * Log an array with specific log level.
     *
     * @param logLevel the specific log level
     * @param array    the array to log
     * @since 1.4.0
     */
    public static void log(int logLevel, Object[] array) {
        assertInitialization();
        sLogger.log(null, logLevel, array);
    }

    /**
     * Log an array with specific log level.
     *
     * @param tag      the tag
     * @param logLevel the specific log level
     * @param array    the array to log
     * @since 1.4.0
     */
    public static void log(String tag, int logLevel, Object[] array) {
        assertInitialization();
        sLogger.log(tag, logLevel, array);
    }

    /**
     * Log a message with specific log level.
     *
     * @param logLevel the specific log level
     * @param format   the format of the message to log
     * @param args     the arguments of the message to log
     * @since 1.4.0
     */
    public static void log(int logLevel, String format, Object... args) {
        assertInitialization();
        sLogger.log(null, logLevel, format, args);
    }

    /**
     * Log a message with specific log level.
     *
     * @param tag      the tag
     * @param logLevel the specific log level
     * @param format   the format of the message to log
     * @param args     the arguments of the message to log
     * @since 1.4.0
     */
    public static void log(String tag, int logLevel, String format, Object... args) {
        assertInitialization();
        sLogger.log(tag, logLevel, format, args);
    }

    /**
     * Log a message with specific log level.
     *
     * @param logLevel the specific log level
     * @param msg      the message to log
     * @since 1.4.0
     */
    public static void log(int logLevel, String msg) {
        assertInitialization();
        sLogger.log(null, logLevel, msg);
    }

    /**
     * Log a message with specific log level.
     *
     * @param tag      the tag
     * @param logLevel the specific log level
     * @param msg      the message to log
     * @since 1.4.0
     */
    public static void log(String tag, int logLevel, String msg) {
        assertInitialization();
        sLogger.log(tag, logLevel, msg);
    }

    /**
     * Log a message and a throwable with specific log level.
     *
     * @param logLevel the specific log level
     * @param msg      the message to log
     * @param tr       the throwable to be log
     * @since 1.4.0
     */
    public static void log(int logLevel, String msg, Throwable tr) {
        assertInitialization();
        sLogger.log(null, logLevel, msg, tr);
    }

    /**
     * Log a message and a throwable with specific log level.
     *
     * @param tag      the tag
     * @param logLevel the specific log level
     * @param msg      the message to log
     * @param tr       the throwable to be log
     * @since 1.4.0
     */
    public static void log(String tag, int logLevel, String msg, Throwable tr) {
        assertInitialization();
        sLogger.log(tag, logLevel, msg, tr);
    }

    /**
     * Log a JSON string, with level {@link LogLevel#DEBUG} by default.
     *
     * @param json the JSON string to log
     */
    public static void json(String json) {
        assertInitialization();
        sLogger.json(null, LogLevel.DEBUG, json);
    }

    /**
     * Log a JSON string, with level {@link LogLevel#DEBUG} by default.
     *
     * @param tag  the tag
     * @param json the JSON string to log
     */
    public static void json(String tag, String json) {
        assertInitialization();
        sLogger.json(tag, LogLevel.DEBUG, json);
    }

    /**
     * Log a XML string, with level {@link LogLevel#DEBUG} by default.
     *
     * @param xml the XML string to log
     */
    public static void xml(String xml) {
        assertInitialization();
        sLogger.xml(null, xml);
    }

    /**
     * Log a XML string, with level {@link LogLevel#DEBUG} by default.
     *
     * @param tag the tag
     * @param xml the XML string to log
     */
    public static void xml(String tag, String xml) {
        assertInitialization();
        sLogger.xml(tag, xml);
    }

    /**
     * Compatible class with {@link android.util.Log}.
     *
     * @deprecated please use {@link PascLog} instead
     */
    public static class Log {

        /**
         * @deprecated compatible with {@link android.util.Log#v(String, String)}
         */
        public static void v(String tag, String msg) {
            tag(tag).build().v(tag, msg);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#v(String, String, Throwable)}
         */
        public static void v(String tag, String msg, Throwable tr) {
            tag(tag).build().v(tag, msg, tr);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#d(String, String)}
         */
        public static void d(String tag, String msg) {
            tag(tag).build().d(tag, msg);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#d(String, String, Throwable)}
         */
        public static void d(String tag, String msg, Throwable tr) {
            tag(tag).build().d(tag, msg, tr);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#i(String, String)}
         */
        public static void i(String tag, String msg) {
            tag(tag).build().i(tag, msg);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#i(String, String, Throwable)}
         */
        public static void i(String tag, String msg, Throwable tr) {
            tag(tag).build().i(tag, msg, tr);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#w(String, String)}
         */
        public static void w(String tag, String msg) {
            tag(tag).build().w(tag, msg);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#w(String, String, Throwable)}
         */
        public static void w(String tag, String msg, Throwable tr) {
            tag(tag).build().w(tag, msg, tr);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#w(String, Throwable)}
         */
        public static void w(String tag, Throwable tr) {
            tag(tag).build().w(tag, "", tr);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#e(String, String)}
         */
        public static void e(String tag, String msg) {
            tag(tag).build().e(tag, msg);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#e(String, String, Throwable)}
         */
        public static void e(String tag, String msg, Throwable tr) {
            tag(tag).build().e(tag, msg, tr);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#wtf(String, String)}
         */
        public static void wtf(String tag, String msg) {
            e(tag, msg);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#wtf(String, Throwable)}
         */
        public static void wtf(String tag, Throwable tr) {
            wtf(tag, "", tr);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#wtf(String, String, Throwable)}
         */
        public static void wtf(String tag, String msg, Throwable tr) {
            e(tag, msg, tr);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#println(int, String, String)}
         */
        public static void println(int logLevel, String tag, String msg) {
            tag(tag).build().println(tag, logLevel, msg);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#isLoggable(String, int)}
         */
        public static boolean isLoggable(String tag, int level) {
            return sLogConfiguration.isLoggable(level);
        }

        /**
         * @deprecated compatible with {@link android.util.Log#getStackTraceString(Throwable)}
         */
        public static String getStackTraceString(Throwable tr) {
            return StackTraceUtil.getStackTraceString(tr);
        }
    }
}
