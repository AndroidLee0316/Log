package com.pasc.sample.log.format;

import com.pasc.lib.log.formatter.stacktrace.StackTraceFormatter;

/**
 * Created by lingchun147 on 2018/9/5.
 */
public class MyStackTraceFormatter implements StackTraceFormatter {
  @Override public String format(StackTraceElement[] data) {
    return "MyStack : " + data.toString();
  }
}
