
package com.pasc.lib.log.flattener;

/**
 * The log flattener used to flatten log elements(log level, tag and message) to a
 * single Charsequence.
 *
 * @since 1.3.0
 */
public interface Flattener {

  /**
   * Flatten the log.
   *
   * @param logLevel the level of log
   * @param tag      the tag of log
   * @param message  the message of log
   * @return the formatted final log Charsequence
   */
  CharSequence flatten(int logLevel, String tag, String message);
}
