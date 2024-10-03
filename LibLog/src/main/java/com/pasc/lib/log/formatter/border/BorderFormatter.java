
package com.pasc.lib.log.formatter.border;

import com.pasc.lib.log.formatter.Formatter;

/**
 * The border formatter used to wrap string segments with borders when logging.
 * <p>
 * e.g:
 * <br>
 * <br>╔════════════════════════════════════════════════════════════════════════════
 * <br>║Thread: main
 * <br>╟────────────────────────────────────────────────────────────────────────────
 * <br>║	├ com.elvishew.xlog.SampleClassB.sampleMethodB(SampleClassB.java:100)
 * <br>║	└ com.elvishew.xlog.SampleClassA.sampleMethodA(SampleClassA.java:50)
 * <br>╟────────────────────────────────────────────────────────────────────────────
 * <br>║Here is a simple message
 * <br>╚════════════════════════════════════════════════════════════════════════════
 */
public interface BorderFormatter extends Formatter<String[]> {
}
