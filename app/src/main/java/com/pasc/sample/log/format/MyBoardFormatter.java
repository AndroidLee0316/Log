package com.pasc.sample.log.format;

import com.pasc.lib.log.formatter.border.BorderFormatter;

/**
 * Created by lingchun147 on 2018/9/5.
 */
public class MyBoardFormatter implements BorderFormatter {
  @Override public String format(String[] data) {
    return "MyBoard : " + data.toString();
  }
}
