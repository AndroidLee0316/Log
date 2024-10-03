package com.pasc.sample.log.format;

import com.pasc.lib.log.formatter.message.json.JsonFormatter;

/**
 * Created by lingchun147 on 2018/9/5.
 */
public class MyJsonFormatter implements JsonFormatter {
  @Override public String format(String data) {
    return "MyJson : " + data;
  }
}
