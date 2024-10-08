
package com.pasc.lib.log.internal;

import android.content.Intent;
import android.os.Bundle;
import com.pasc.lib.log.flattener.DefaultFlattener;
import com.pasc.lib.log.flattener.Flattener;
import com.pasc.lib.log.formatter.border.BorderFormatter;
import com.pasc.lib.log.formatter.border.DefaultBorderFormatter;
import com.pasc.lib.log.formatter.message.json.DefaultJsonFormatter;
import com.pasc.lib.log.formatter.message.json.JsonFormatter;
import com.pasc.lib.log.formatter.message.object.BundleFormatter;
import com.pasc.lib.log.formatter.message.object.IntentFormatter;
import com.pasc.lib.log.formatter.message.object.ObjectFormatter;
import com.pasc.lib.log.formatter.message.throwable.DefaultThrowableFormatter;
import com.pasc.lib.log.formatter.message.throwable.ThrowableFormatter;
import com.pasc.lib.log.formatter.message.xml.DefaultXmlFormatter;
import com.pasc.lib.log.formatter.message.xml.XmlFormatter;
import com.pasc.lib.log.formatter.stacktrace.DefaultStackTraceFormatter;
import com.pasc.lib.log.formatter.stacktrace.StackTraceFormatter;
import com.pasc.lib.log.formatter.thread.DefaultThreadFormatter;
import com.pasc.lib.log.formatter.thread.ThreadFormatter;
import com.pasc.lib.log.printer.Printer;
import com.pasc.lib.log.printer.file.FilePrinter;
import com.pasc.lib.log.printer.file.backup.BackupStrategy;
import com.pasc.lib.log.printer.file.backup.FileSizeBackupStrategy;
import com.pasc.lib.log.printer.file.naming.ChangelessFileNameGenerator;
import com.pasc.lib.log.printer.file.naming.FileNameGenerator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for providing default implementation.
 */
public class DefaultsFactory {

  private static final String DEFAULT_LOG_FILE_NAME = "log";

  private static final long DEFAULT_LOG_FILE_MAX_SIZE = 1024 * 1024; // 1M bytes;

  private static final Map<Class<?>, ObjectFormatter<?>> BUILTIN_OBJECT_FORMATTERS;

  static {
    Map<Class<?>, ObjectFormatter<?>> objectFormatters = new HashMap<>();
    objectFormatters.put(Bundle.class, new BundleFormatter());
    objectFormatters.put(Intent.class, new IntentFormatter());
    BUILTIN_OBJECT_FORMATTERS = Collections.unmodifiableMap(objectFormatters);
  }

  /**
   * Create the default JSON formatter.
   */
  public static JsonFormatter createJsonFormatter() {
    return new DefaultJsonFormatter();
  }

  /**
   * Create the default XML formatter.
   */
  public static XmlFormatter createXmlFormatter() {
    return new DefaultXmlFormatter();
  }

  /**
   * Create the default throwable formatter.
   */
  public static ThrowableFormatter createThrowableFormatter() {
    return new DefaultThrowableFormatter();
  }

  /**
   * Create the default thread formatter.
   */
  public static ThreadFormatter createThreadFormatter() {
    return new DefaultThreadFormatter();
  }

  /**
   * Create the default stack trace formatter.
   */
  public static StackTraceFormatter createStackTraceFormatter() {
    return new DefaultStackTraceFormatter();
  }

  /**
   * Create the default border formatter.
   */
  public static BorderFormatter createBorderFormatter() {
    return new DefaultBorderFormatter();
  }

  /**
   * Create the default log flattener.
   */
  public static Flattener createFlattener() {
    return new DefaultFlattener();
  }

  /**
   * Create the default printer.
   */
  public static Printer createPrinter() {
    return Platform.get().defaultPrinter();
  }

  /**
   * Create the default file name generator for {@link FilePrinter}.
   */
  public static FileNameGenerator createFileNameGenerator() {
    return new ChangelessFileNameGenerator(DEFAULT_LOG_FILE_NAME);
  }

  /**
   * Create the default backup strategy for {@link FilePrinter}.
   */
  public static BackupStrategy createBackupStrategy() {
    return new FileSizeBackupStrategy(DEFAULT_LOG_FILE_MAX_SIZE);
  }

  /**
   * Get the builtin object formatters.
   *
   * @return the builtin object formatters
   */
  public static Map<Class<?>, ObjectFormatter<?>> builtinObjectFormatters() {
    return BUILTIN_OBJECT_FORMATTERS;
  }
}
