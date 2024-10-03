package com.pasc.sample.log.format;

import com.pasc.lib.log.formatter.message.xml.XmlFormatter;

/**
 * Created by lingchun147 on 2018/9/5.
 */
public class MyXmlFormatter implements XmlFormatter {
  @Override public String format(String data) {
    return "MyXml : " + data;
  }
}
