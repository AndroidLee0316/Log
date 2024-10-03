package com.pasc.sample.log.format;

import com.pasc.lib.log.formatter.thread.ThreadFormatter;

/**
 * Created by lingchun147 on 2018/9/5.
 */
public class MyThreadFormatter implements ThreadFormatter {
  @Override public String format(Thread data) {
    return "MyThread : id = " + data.getId() + ", name = " + data.getName();
  }
}
