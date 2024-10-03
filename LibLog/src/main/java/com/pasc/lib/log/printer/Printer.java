
package com.pasc.lib.log.printer;

import com.pasc.lib.log.printer.file.FilePrinter;

/**
 * A printer is used for printing the log to somewhere, like android shell, terminal
 * or file system.
 * <p>
 * There are 4 main implementation of Printer.
 * <br>{@link AndroidPrinter}, print log to android shell terminal.
 * <br>{@link ConsolePrinter}, print log to console via System.out.
 * <br>{@link FilePrinter}, print log to file system.
 * <br>{@link RemotePrinter}, print log to remote server, this is empty implementation yet.
 */
public interface Printer {

  /**
   * Print log in new line.
   *
   * @param logLevel the level of log
   * @param tag      the tag of log
   * @param msg      the msg of log
   */
  void println(int logLevel, String tag, String msg);
}
