package com.pasc.sample.log.format;

import com.pasc.lib.log.formatter.message.throwable.ThrowableFormatter;

/**
 * Created by lingchun147 on 2018/9/5.
 */
public class MyThrowableFormatter implements ThrowableFormatter {
  @Override public String format(Throwable data) {
    return "MyThrowable : " + data.getMessage();
  }
}
