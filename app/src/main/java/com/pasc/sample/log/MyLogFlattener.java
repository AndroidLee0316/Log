package com.pasc.sample.log;

import com.pasc.lib.log.LogLevel;
import com.pasc.lib.log.flattener.Flattener;

/**
 * Created by lingchun147 on 2018/9/5.
 */
public class MyLogFlattener implements Flattener {
  @Override public CharSequence flatten(int logLevel, String tag, String message) {
    return Long.toString(System.currentTimeMillis())
            + '|' + LogLevel.getShortLevelName(logLevel)
            + '|' + tag
            + '|' + message;
  }
}
