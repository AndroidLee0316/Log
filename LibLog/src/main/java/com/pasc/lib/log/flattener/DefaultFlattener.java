
package com.pasc.lib.log.flattener;

import com.pasc.lib.log.LogLevel;

/**
 * Simply join the timestamp, log level, tag and message together.
 *
 * @since 1.3.0
 */
public class DefaultFlattener implements Flattener {

  @Override
  public CharSequence flatten(int logLevel, String tag, String message) {
    return Long.toString(System.currentTimeMillis())
        + '|' + LogLevel.getShortLevelName(logLevel)
        + '|' + tag
        + '|' + message;
  }
}
