
package com.pasc.lib.log;

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
import com.pasc.lib.log.internal.SystemCompat;
import com.pasc.lib.log.internal.util.StackTraceUtil;
import com.pasc.lib.log.printer.Printer;
import com.pasc.lib.log.printer.PrinterSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A logger is used to do the real logging work, can use multiple log printers to print the log.
 * <p>
 * A {@link Logger} is always generated and mostly accessed by {@link PascLog}, but for customization
 * purpose, you can configure a {@link Logger} via the {@link Builder} which is returned by
 * {@link PascLog} when you trying to start a customization using {@link PascLog#tag(String)}
 * or other configuration method, and to use the customized {@link Logger}, you should call
 * the {@link Builder#build()} to build a {@link Logger}, and then you can log using
 * the {@link Logger} assuming that you are using the {@link PascLog} directly.
 */
public class Logger {

  /**
   * The log configuration which you should respect to when logging.
   */
  private LogConfiguration logConfiguration;

  /**
   * The log printer used to print the logs.
   */
  private Printer printer;

  /**
   * Construct a logger.
   *
   * @param logConfiguration the log configuration which you should respect to when logging
   * @param printer          the log printer used to print the log
   */
    /*package*/ Logger(LogConfiguration logConfiguration, Printer printer) {
    this.logConfiguration = logConfiguration;
    this.printer = printer;
  }

  public void setPrinter(Printer printer) {
      this.printer = printer;
  }

  /**
   * Construct a logger using builder.
   *
   * @param builder the logger builder
   */
    /*package*/ Logger(Builder builder) {
    LogConfiguration.Builder logConfigBuilder = new LogConfiguration.Builder(
        PascLog.sLogConfiguration);

    if (builder.logLevel != 0) {
      logConfigBuilder.logLevel(builder.logLevel);
    }

    if (builder.tag != null) {
      logConfigBuilder.tag(builder.tag);
    }

    if (builder.threadSet) {
      if (builder.withThread) {
        logConfigBuilder.threadInfoEnable();
      } else {
        logConfigBuilder.threadInfoDisable();
      }
    }
    if (builder.stackTraceSet) {
      if (builder.withStackTrace) {
        logConfigBuilder.stackTraceEnable(builder.stackTraceOrigin, builder.stackTraceDepth);
      } else {
        logConfigBuilder.stackTraceDisable();
      }
    }
    if (builder.borderSet) {
      if (builder.withBorder) {
        logConfigBuilder.borderEnable();
      } else {
        logConfigBuilder.borderDisable();
      }
    }

    if (builder.jsonFormatter != null) {
      logConfigBuilder.jsonFormatter(builder.jsonFormatter);
    }
    if (builder.xmlFormatter != null) {
      logConfigBuilder.xmlFormatter(builder.xmlFormatter);
    }
    if (builder.throwableFormatter != null) {
      logConfigBuilder.throwableFormatter(builder.throwableFormatter);
    }
    if (builder.threadFormatter != null) {
      logConfigBuilder.threadFormatter(builder.threadFormatter);
    }
    if (builder.stackTraceFormatter != null) {
      logConfigBuilder.stackTraceFormatter(builder.stackTraceFormatter);
    }
    if (builder.borderFormatter != null) {
      logConfigBuilder.borderFormatter(builder.borderFormatter);
    }
    if (builder.objectFormatters != null) {
      logConfigBuilder.objectFormatters(builder.objectFormatters);
    }
    if (builder.interceptors != null) {
      logConfigBuilder.interceptors(builder.interceptors);
    }
    logConfiguration = logConfigBuilder.build();

    if (builder.printer != null) {
      printer = builder.printer;
    } else {
      printer = PascLog.getPrintSet();
    }
  }

  /**
   * Log an object with level {@link LogLevel#VERBOSE}.
   *
   * @param object the object to log
   * @see Builder#addObjectFormatter(Class, ObjectFormatter)
   * @since 1.1.0
   */
  public void v(String tag, Object object) {
    println(tag, LogLevel.VERBOSE, object);
  }

  /**
   * Log an array with level {@link LogLevel#VERBOSE}.
   *
   * @param array the array to log
   */
  public void v(String tag, Object[] array) {
    println(tag, LogLevel.VERBOSE, array);
  }

  /**
   * Log a message with level {@link LogLevel#VERBOSE}.
   *
   * @param format the format of the message to log
   * @param args   the arguments of the message to log
   */
  public void v(String tag, String format, Object... args) {
    println(tag, LogLevel.VERBOSE, format, args);
  }

  /**
   * Log a message with level {@link LogLevel#VERBOSE}.
   *
   * @param msg the message to log
   */
  public void v(String tag, String msg) {
    println(tag, LogLevel.VERBOSE, msg);
  }

  /**
   * Log a message and a throwable with level {@link LogLevel#VERBOSE}.
   *
   * @param msg the message to log
   * @param tr  the throwable to be log
   */
  public void v(String tag, String msg, Throwable tr) {
    println(tag, LogLevel.VERBOSE, msg, tr);
  }

  /**
   * Log an object with level {@link LogLevel#DEBUG}.
   *
   * @param object the object to log
   * @see Builder#addObjectFormatter(Class, ObjectFormatter)
   * @since 1.1.0
   */
  public void d(String tag, Object object) {
    println(tag, LogLevel.DEBUG, object);
  }

  /**
   * Log an array with level {@link LogLevel#DEBUG}.
   *
   * @param array the array to log
   */
  public void d(String tag, Object[] array) {
    println(tag, LogLevel.DEBUG, array);
  }

  /**
   * Log a message with level {@link LogLevel#DEBUG}.
   *
   * @param format the format of the message to log, null if just need to concat arguments
   * @param args   the arguments of the message to log
   */
  public void d(String tag, String format, Object... args) {
    println(tag, LogLevel.DEBUG, format, args);
  }

  /**
   * Log a message with level {@link LogLevel#DEBUG}.
   *
   * @param msg the message to log
   */
  public void d(String tag, String msg) {
    println(tag, LogLevel.DEBUG, msg);
  }

  /**
   * Log a message and a throwable with level {@link LogLevel#DEBUG}.
   *
   * @param msg the message to log
   * @param tr  the throwable to be log
   */
  public void d(String tag, String msg, Throwable tr) {
    println(tag, LogLevel.DEBUG, msg, tr);
  }

  /**
   * Log an object with level {@link LogLevel#INFO}.
   *
   * @param object the object to log
   * @see Builder#addObjectFormatter(Class, ObjectFormatter)
   * @since 1.1.0
   */
  public void i(String tag, Object object) {
    println(tag, LogLevel.INFO, object);
  }

  /**
   * Log an array with level {@link LogLevel#INFO}.
   *
   * @param array the array to log
   */
  public void i(String tag, Object[] array) {
    println(tag, LogLevel.INFO, array);
  }

  /**
   * Log a message with level {@link LogLevel#INFO}.
   *
   * @param format the format of the message to log, null if just need to concat arguments
   * @param args   the arguments of the message to log
   */
  public void i(String tag, String format, Object... args) {
    println(tag, LogLevel.INFO, format, args);
  }

  /**
   * Log a message with level {@link LogLevel#INFO}.
   *
   * @param msg the message to log
   */
  public void i(String tag, String msg) {
    println(tag, LogLevel.INFO, msg);
  }

  /**
   * Log a message and a throwable with level {@link LogLevel#INFO}.
   *
   * @param msg the message to log
   * @param tr  the throwable to be log
   */
  public void i(String tag, String msg, Throwable tr) {
    println(tag, LogLevel.INFO, msg, tr);
  }

  /**
   * Log an object with level {@link LogLevel#WARN}.
   *
   * @param object the object to log
   * @see Builder#addObjectFormatter(Class, ObjectFormatter)
   * @since 1.1.0
   */
  public void w(String tag, Object object) {
    println(tag, LogLevel.WARN, object);
  }

  /**
   * Log an array with level {@link LogLevel#WARN}.
   *
   * @param array the array to log
   */
  public void w(String tag, Object[] array) {
    println(tag, LogLevel.WARN, array);
  }

  /**
   * Log a message with level {@link LogLevel#WARN}.
   *
   * @param format the format of the message to log, null if just need to concat arguments
   * @param args   the arguments of the message to log
   */
  public void w(String tag, String format, Object... args) {
    println(tag, LogLevel.WARN, format, args);
  }

  /**
   * Log a message with level {@link LogLevel#WARN}.
   *
   * @param msg the message to log
   */
  public void w(String tag, String msg) {
    println(tag, LogLevel.WARN, msg);
  }

  /**
   * Log a message and a throwable with level {@link LogLevel#WARN}.
   *
   * @param msg the message to log
   * @param tr  the throwable to be log
   */
  public void w(String tag, String msg, Throwable tr) {
    println(tag, LogLevel.WARN, msg, tr);
  }

  /**
   * Log an object with level {@link LogLevel#ERROR}.
   *
   * @param object the object to log
   * @see Builder#addObjectFormatter(Class, ObjectFormatter)
   * @since 1.1.0
   */
  public void e(String tag, Object object) {
    println(tag, LogLevel.ERROR, object);
  }

  /**
   * Log an array with level {@link LogLevel#ERROR}.
   *
   * @param array the array to log
   */
  public void e(String tag, Object[] array) {
    println(tag, LogLevel.ERROR, array);
  }

  /**
   * Log a message with level {@link LogLevel#ERROR}.
   *
   * @param format the format of the message to log, null if just need to concat arguments
   * @param args   the arguments of the message to log
   */
  public void e(String tag, String format, Object... args) {
    println(tag, LogLevel.ERROR, format, args);
  }

  /**
   * Log a message with level {@link LogLevel#ERROR}.
   *
   * @param msg the message to log
   */
  public void e(String tag, String msg) {
    println(tag, LogLevel.ERROR, msg);
  }

  /**
   * Log a message and a throwable with level {@link LogLevel#ERROR}.
   *
   * @param msg the message to log
   * @param tr  the throwable to be log
   */
  public void e(String tag, String msg, Throwable tr) {
    println(tag, LogLevel.ERROR, msg, tr);
  }

  /**
   * Log an object with specific log level.
   *
   * @param logLevel the specific log level
   * @param object   the object to log
   * @see Builder#addObjectFormatter(Class, ObjectFormatter)
   * @since 1.4.0
   */
  public void log(String tag, int logLevel, Object object) {
    println(tag, logLevel, object);
  }

  /**
   * Log an array with specific log level.
   *
   * @param logLevel the specific log level
   * @param array    the array to log
   * @since 1.4.0
   */
  public void log(String tag, int logLevel, Object[] array) {
    println(tag, logLevel, array);
  }

  /**
   * Log a message with specific log level.
   *
   * @param logLevel the specific log level
   * @param format   the format of the message to log, null if just need to concat arguments
   * @param args     the arguments of the message to log
   * @since 1.4.0
   */
  public void log(String tag, int logLevel, String format, Object... args) {
    println(tag, logLevel, format, args);
  }

  /**
   * Log a message with specific log level.
   *
   * @param logLevel the specific log level
   * @param msg      the message to log
   * @since 1.4.0
   */
  public void log(String tag, int logLevel, String msg) {
    println(tag, logLevel, msg);
  }

  /**
   * Log a message and a throwable with specific log level.
   *
   * @param logLevel the specific log level
   * @param msg      the message to log
   * @param tr       the throwable to be log
   * @since 1.4.0
   */
  public void log(String tag, int logLevel, String msg, Throwable tr) {
    println(tag, logLevel, msg, tr);
  }

  /**
   * Log a JSON string, with level {@link LogLevel#DEBUG} by default.
   *
   * @param logLevel the specific log level
   * @param json the JSON string to log
   *
   */
  public void json(String tag, int logLevel, String json) {
    if (logLevel < logConfiguration.logLevel) {
      return;
    }
    try {
      json = logConfiguration.jsonFormatter.format(json);
    }catch (Exception e) {

    }

    printlnInternal(tag, logLevel, json);
  }

  /**
   * Log a XML string, with level {@link LogLevel#DEBUG} by default.
   *
   * @param xml the XML string to log
   */
  public void xml(String tag, String xml) {
    if (LogLevel.DEBUG < logConfiguration.logLevel) {
      return;
    }
    try{
      xml = logConfiguration.xmlFormatter.format(xml);
    }catch (Exception e) {

    }
    printlnInternal(tag, LogLevel.DEBUG, xml);
  }

  /**
   * Print an object in a new line.
   *
   * @param logLevel the log level of the printing object
   * @param object   the object to print
   */
  private <T> void println(String tag, int logLevel, T object) {
    if (logLevel < logConfiguration.logLevel) {
      return;
    }
    String objectString;
    if (object != null) {
      ObjectFormatter<? super T> objectFormatter = logConfiguration.getObjectFormatter(object);
      if (objectFormatter != null) {
        objectString = objectFormatter.format(object);
      } else {
        objectString = object.toString();
      }
    } else {
      objectString = "null";
    }
    printlnInternal(tag, logLevel, objectString);
  }

  /**
   * Print an array in a new line.
   *
   * @param logLevel the log level of the printing array
   * @param array    the array to print
   */
  private void println(String tag, int logLevel, Object[] array) {
    if (logLevel < logConfiguration.logLevel) {
      return;
    }
    printlnInternal(tag, logLevel, Arrays.deepToString(array));
  }

  /**
   * Print a log in a new line.
   *
   * @param logLevel the log level of the printing log
   * @param format   the format of the printing log, null if just need to concat arguments
   * @param args     the arguments of the printing log
   */
  private void println(String tag, int logLevel, String format, Object... args) {
    if (logLevel < logConfiguration.logLevel) {
      return;
    }
    printlnInternal(tag, logLevel, formatArgs(format, args));
  }

  /**
   * Print a log in a new line.
   *
   * @param logLevel the log level of the printing log
   * @param msg      the message you would like to log
   */
    /*package*/ void println(String tag, int logLevel, String msg) {
    if (logLevel < logConfiguration.logLevel) {
      return;
    }
    printlnInternal(tag, logLevel, msg);
  }

  /**
   * Print a log in a new line.
   *
   * @param logLevel the log level of the printing log
   * @param msg      the message you would like to log
   * @param tr       a throwable object to log
   */
  private void println(String tag, int logLevel, String msg, Throwable tr) {
    if (logLevel < logConfiguration.logLevel) {
      return;
    }
    printlnInternal(tag, logLevel, ((msg == null || msg.length() == 0)
        ? "" : (msg + SystemCompat.lineSeparator))
        + logConfiguration.throwableFormatter.format(tr));
  }

  /**
   * Print a log in a new line internally.
   *
   * @param logLevel the log level of the printing log
   * @param msg      the message you would like to log
   */
  private void printlnInternal(String tag, int logLevel, String msg) {
//    if(!Log.isLoggable(logConfiguration.tag, logLevel)){
//      return;
//    }
    tag = TextUtils.isEmpty(tag) ? logConfiguration.tag : tag;
    String thread = logConfiguration.withThread
        ? logConfiguration.threadFormatter.format(Thread.currentThread())
        : null;
    String stackTrace = logConfiguration.withStackTrace
        ? logConfiguration.stackTraceFormatter.format(
        StackTraceUtil.getCroppedRealStackTrack(new Throwable().getStackTrace(),
            logConfiguration.stackTraceOrigin,
            logConfiguration.stackTraceDepth))
        : null;

    if (logConfiguration.interceptors != null) {
      LogItem log = new LogItem(logLevel, tag, thread, stackTrace, msg);
      for (Interceptor interceptor : logConfiguration.interceptors) {
        log = interceptor.intercept(log);
        if (log == null) {
          // Log is eaten, don't print this log.
          return;
        }

        // Check if the log still healthy.
        if (log.tag == null || log.msg == null) {
          throw new IllegalStateException("Interceptor " + interceptor
              + " should not remove the tag or message of a log,"
              + " if you don't want to print this log,"
              + " just return a null when intercept.");
        }
      }

      // Use fields after interception.
      logLevel = log.level;
      tag = log.tag;
      thread = log.threadInfo;
      stackTrace = log.stackTraceInfo;
      msg = log.msg;
    }
    PascLog.sPrintRWL.readLock().lock();
    if (printer != null) {
      printer.println(logLevel, tag, logConfiguration.withBorder
              ? logConfiguration.borderFormatter.format(new String[]{thread, stackTrace, msg})
              : ((thread != null ? (thread + SystemCompat.lineSeparator) : "")
              + (stackTrace != null ? (stackTrace + SystemCompat.lineSeparator) : "")
              + msg));
    }
    PascLog.sPrintRWL.readLock().unlock();
  }

  /**
   * Format a string with arguments.
   *
   * @param format the format string, null if just to concat the arguments
   * @param args   the arguments
   * @return the formatted string
   */
  private String formatArgs(String format, Object... args) {
    if (format != null) {
      return String.format(format, args);
    } else {
      StringBuilder sb = new StringBuilder();
      for (int i = 0, N = args.length; i < N; i++) {
        if (i != 0) {
          sb.append(", ");
        }
        sb.append(args[i]);
      }
      return sb.toString();
    }
  }

  /**
   * Builder for {@link Logger}.
   */
  public static class Builder {

    /**
     * The log level, the logs below of which would not be printed.
     */
    private int logLevel;

    /**
     * The tag string when {@link Logger} log.
     */
    private String tag;

    /**
     * Whether we should log with thread info.
     */
    private boolean withThread;

    /**
     * Whether we have enabled/disabled thread info.
     */
    private boolean threadSet;

    /**
     * Whether we should log with stack trace.
     */
    private boolean withStackTrace;

    /**
     * The origin of stack trace elements from which we should NOT log when logging with stack trace,
     * it can be a package name like "com.elvishew.xlog", a class name like "com.yourdomain.logWrapper",
     * or something else between package name and class name, like "com.yourdomain.".
     * <p>
     * It is mostly used when you are using a logger wrapper.
     */
    private String stackTraceOrigin;

    /**
     * The number of stack trace elements we should log when logging with stack trace,
     * 0 if no limitation.
     */
    private int stackTraceDepth;

    /**
     * Whether we have enabled/disabled stack trace.
     */
    private boolean stackTraceSet;

    /**
     * Whether we should log with border.
     */
    private boolean withBorder;

    /**
     * Whether we have enabled/disabled border.
     */
    private boolean borderSet;

    /**
     * The JSON formatter when {@link Logger} log a JSON string.
     */
    private JsonFormatter jsonFormatter;

    /**
     * The XML formatter when {@link Logger} log a XML string.
     */
    private XmlFormatter xmlFormatter;

    /**
     * The throwable formatter when {@link Logger} log a message with throwable.
     */
    private ThrowableFormatter throwableFormatter;

    /**
     * The thread formatter when {@link Logger} logging.
     */
    private ThreadFormatter threadFormatter;

    /**
     * The stack trace formatter when {@link Logger} logging.
     */
    private StackTraceFormatter stackTraceFormatter;

    /**
     * The border formatter when {@link Logger} logging.
     */
    private BorderFormatter borderFormatter;

    /**
     * The object formatters, used when {@link Logger} logging an object.
     */
    private Map<Class<?>, ObjectFormatter<?>> objectFormatters;

    /**
     * The intercepts, used when {@link Logger} logging.
     */
    private List<Interceptor> interceptors;

    /**
     * The printer used to print the log when {@link Logger} log.
     */
    private Printer printer;
    /**
     * Customize a {@link Logger} to set a tag
     */
    private static volatile Logger cusLogger;

    /**
     * Construct a builder, which will perform the same as the global one by default.
     */
    public Builder() {
      PascLog.assertInitialization();
    }

    /**
     * Set the log level, the logs below of which would not be printed.
     *
     * @param logLevel the log level
     * @return the builder
     * @since 1.3.0
     */
    public Builder logLevel(int logLevel) {
      this.logLevel = logLevel;
      return this;
    }

    /**
     * Set the tag string when {@link Logger} log.
     *
     * @param tag the tag string when {@link Logger} log
     * @return the builder
     */
    public Builder tag(String tag) {
      this.tag = tag;
      return this;
    }

    /**
     * Enable thread info.
     *
     * @return the builder
     */
    public Builder threadEnable() {
      this.withThread = true;
      this.threadSet = true;
      return this;
    }

    /**
     * Disable thread info.
     *
     * @return the builder
     */
    public Builder threadDisable() {
      this.withThread = false;
      this.threadSet = true;
      return this;
    }

    /**
     * Enable stack trace.
     *
     * @param depth the number of stack trace elements we should log, 0 if no limitation
     * @return the builder
     */
    public Builder stackTraceEnable(int depth) {
      this.withStackTrace = true;
      this.stackTraceDepth = depth;
      this.stackTraceSet = true;
      return this;
    }

    /**
     * Enable stack trace.
     *
     * @param stackTraceOrigin the origin of stack trace elements from which we should NOT log when
     *                         logging with stack trace, it can be a package name like
     *                         "com.elvishew.xlog", a class name like "com.yourdomain.logWrapper",
     *                         or something else between package name and class name, like "com.yourdomain.".
     *                         It is mostly used when you are using a logger wrapper
     * @param depth            the number of stack trace elements we should log, 0 if no limitation
     * @return the builder
     * @since 1.4.0
     */
    public Builder stackTraceEnable(String stackTraceOrigin, int depth) {
      this.withStackTrace = true;
      this.stackTraceOrigin = stackTraceOrigin;
      this.stackTraceDepth = depth;
      this.stackTraceSet = true;
      return this;
    }

    /**
     * Disable stack trace.
     *
     * @return the builder
     */
    public Builder stackTraceDisable() {
      this.withStackTrace = false;
      this.stackTraceOrigin = null;
      this.stackTraceDepth = 0;
      this.stackTraceSet = true;
      return this;
    }

    /**
     * Enable border.
     *
     * @return the builder
     */
    public Builder borderEnable() {
      this.withBorder = true;
      this.borderSet = true;
      return this;
    }

    /**
     * Disable border.
     *
     * @return the builder
     */
    public Builder borderDisable() {
      this.withBorder = false;
      this.borderSet = true;
      return this;
    }

    /**
     * Set the JSON formatter when {@link Logger} log a JSON string.
     *
     * @param jsonFormatter the JSON formatter when {@link Logger} log a JSON string
     * @return the builder
     */
    public Builder jsonFormatter(JsonFormatter jsonFormatter) {
      this.jsonFormatter = jsonFormatter;
      return this;
    }

    /**
     * Set the XML formatter when {@link Logger} log a XML string.
     *
     * @param xmlFormatter the XML formatter when {@link Logger} log a XML string
     * @return the builder
     */
    public Builder xmlFormatter(XmlFormatter xmlFormatter) {
      this.xmlFormatter = xmlFormatter;
      return this;
    }

    /**
     * Set the throwable formatter when {@link Logger} log a message with throwable.
     *
     * @param throwableFormatter the throwable formatter when {@link Logger} log a message with
     *                           throwable
     * @return the builder
     */
    public Builder throwableFormatter(ThrowableFormatter throwableFormatter) {
      this.throwableFormatter = throwableFormatter;
      return this;
    }

    /**
     * Set the thread formatter when {@link Logger} logging.
     *
     * @param threadFormatter the thread formatter when {@link Logger} logging
     * @return the builder
     */
    public Builder threadFormatter(ThreadFormatter threadFormatter) {
      this.threadFormatter = threadFormatter;
      return this;
    }

    /**
     * Set the stack trace formatter when {@link Logger} logging.
     *
     * @param stackTraceFormatter the stace trace formatter when {@link Logger} logging
     * @return the builder
     */
    public Builder stackTraceFormatter(StackTraceFormatter stackTraceFormatter) {
      this.stackTraceFormatter = stackTraceFormatter;
      return this;
    }

    /**
     * Set the border formatter when {@link Logger} logging.
     *
     * @param borderFormatter the border formatter when {@link Logger} logging
     * @return the builder
     */
    public Builder borderFormatter(BorderFormatter borderFormatter) {
      this.borderFormatter = borderFormatter;
      return this;
    }

    /**
     * Add an object formatter for specific class of object when {@link Logger} log an object.
     *
     * @param objectClass     the class of object
     * @param objectFormatter the object formatter to add
     * @param <T>             the type of object
     * @return the builder
     * @since 1.1.0
     */
    public <T> Builder addObjectFormatter(Class<T> objectClass,
                                          ObjectFormatter<? super T> objectFormatter) {
      if (objectFormatters == null) {
        objectFormatters = new HashMap<>(DefaultsFactory.builtinObjectFormatters());
      }
      objectFormatters.put(objectClass, objectFormatter);
      return this;
    }

    /**
     * Add an interceptor when {@link Logger} logging.
     *
     * @param interceptor the intercept to add
     * @return the builder
     * @since 1.3.0
     */
    public Builder addInterceptor(Interceptor interceptor) {
      if (interceptors == null) {
        interceptors = new ArrayList<>();
      }
      interceptors.add(interceptor);
      return this;
    }

    /**
     * Set the printers used to print the log when {@link Logger} log.
     *
     * @param printers the printers used to print the log when {@link Logger} log
     * @return the builder
     */
    public Builder printers(Printer... printers) {
      if (printers.length == 0) {
        // Is there anybody want to reuse the Builder? It's not a good idea, but
        // anyway, in case you want to reuse a builder and do not want the custom
        // printers anymore, just do it.
        this.printer = null;
      } else if (printers.length == 1) {
        this.printer = printers[0];
      } else {
        this.printer = new PrinterSet(printers);
      }
      return this;
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#v(String, Object)}.
     *
     * @since 1.1.0
     */
    public void v(Object object) {
      build().v(null, object);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#v(String, Object[])}.
     *
     * @since 1.4.0
     */
    public void v(Object[] array) {
      build().v(null, array);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#v(String, String, Object...)}.
     */
    public void v(String format, Object... args) {
      build().v(null, format, args);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#v(String, String)}.
     */
    public void v(String msg) {
      build().v(null, msg);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#v(String, String, Throwable)}.
     */
    public void v(String msg, Throwable tr) {
      build().v(null, msg, tr);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#d(String, Object)}.
     *
     * @since 1.1.0
     */
    public void d(Object object) {
      build().d(null, object);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#d(String, Object[])}.
     *
     * @since 1.4.0
     */
    public void d(Object[] array) {
      build().d(null, array);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#d(String, String, Object...)}.
     */
    public void d(String format, Object... args) {
      build().d(null, format, args);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#d(String, String)}.
     */
    public void d(String msg) {
      build().d(null, msg);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#d(String, String, Throwable)}.
     */
    public void d(String msg, Throwable tr) {
      build().d(null, msg, tr);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#i(String, Object)}.
     *
     * @since 1.1.0
     */
    public void i(Object object) {
      build().i(null, object);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#i(String, Object[])}.
     *
     * @since 1.4.0
     */
    public void i(Object[] array) {
      build().i(null, array);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#i(String, String, Object...)}.
     */
    public void i(String format, Object... args) {
      build().i(null, format, args);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#i(String, String)}.
     */
    public void i(String msg) {
      build().i(null, msg);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#i(String, String, Throwable)}.
     */
    public void i(String msg, Throwable tr) {
      build().i(null, msg, tr);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#w(String, Object)}.
     *
     * @since 1.1.0
     */
    public void w(Object object) {
      build().w(null, object);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#w(String, Object[])}.
     *
     * @since 1.4.0
     */
    public void w(Object[] array) {
      build().w(null, array);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#w(String, String, Object...)}.
     */
    public void w(String format, Object... args) {
      build().w(null, format, args);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#w(String, String)}.
     */
    public void w(String msg) {
      build().w(null, msg);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#w(String, String, Throwable)}.
     */
    public void w(String msg, Throwable tr) {
      build().w(null, msg, tr);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#e(String, Object)}.
     *
     * @since 1.1.0
     */
    public void e(Object object) {
      build().e(null, object);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#e(String, Object[])}.
     *
     * @since 1.4.0
     */
    public void e(Object[] array) {
      build().e(null, array);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#e(String, String, Object...)}.
     */
    public void e(String format, Object... args) {
      build().e(null, format, args);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#e(String, String)}.
     */
    public void e(String msg) {
      build().e(null, msg);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#e(String, String, Throwable)}.
     */
    public void e(String msg, Throwable tr) {
      build().e(null, msg, tr);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#log(String, int, Object)}.
     *
     * @since 1.4.0
     */
    public void log(int logLevel, Object object) {
      build().log(null, logLevel, object);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#log(String, int, Object[])}.
     *
     * @since 1.4.0
     */
    public void log(int logLevel, Object[] array) {
      build().log(null, logLevel, array);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#log(String, int, String, Object...)}.
     *
     * @since 1.4.0
     */
    public void log(int logLevel, String format, Object... args) {
      build().log(null, logLevel, format, args);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#log(String, int, String)}.
     *
     * @since 1.4.0
     */
    public void log(int logLevel, String msg) {
      build().log(null, logLevel, msg);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#log(String, int, String, Throwable)}.
     *
     * @since 1.4.0
     */
    public void log(int logLevel, String msg, Throwable tr) {
      build().log(null, logLevel, msg, tr);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger #json(String)}.
     */
    public void json(String json) {
      build().json(null, LogLevel.DEBUG, json);
    }

    /**
     * Convenience of {@link #build()} and {@link Logger#xml(String, String)}.
     */
    public void xml(String xml) {
      build().xml(null, xml);
    }

    /**
     * Builds configured {@link Logger} object.
     *
     * @return the built configured {@link Logger} object
     */
    public Logger build() {
      return new Logger(this);
    }
  }
}
