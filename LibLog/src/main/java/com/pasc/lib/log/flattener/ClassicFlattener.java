
package com.pasc.lib.log.flattener;

import com.pasc.lib.log.LogLevel;

/**
 * The classic flattener, flatten the log with pattern "{@value #DEFAULT_PATTERN}".
 * <p>
 * Imagine there is a log, with {@link LogLevel#DEBUG} level, "my_tag" tag and "Simple message"
 * message, the flattened log would be: "2016-11-30 13:00:00.000 D/my_tag: Simple message"
 *
 * @since 1.3.0
 */
public class ClassicFlattener extends PatternFlattener {

  private static final String DEFAULT_PATTERN = "{d} {l}/{t}: {m}";

  public ClassicFlattener() {
    super(DEFAULT_PATTERN);
  }
}
