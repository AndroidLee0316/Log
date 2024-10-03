package com.pasc.sample.log.format;

import com.pasc.lib.log.formatter.message.object.ObjectFormatter;
import com.pasc.sample.log.AnyClass;

/**
 * Created by lingchun147 on 2018/9/5.
 */
public class AnyClassObjectFormatter implements ObjectFormatter<AnyClass> {
  @Override public String format(AnyClass data) {
    return data.toString();
  }
}
