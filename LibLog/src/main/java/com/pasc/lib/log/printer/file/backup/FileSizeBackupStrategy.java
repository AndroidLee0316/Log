
package com.pasc.lib.log.printer.file.backup;

import java.io.File;

/**
 * Limit the file size of a max length.
 */
public class FileSizeBackupStrategy implements BackupStrategy {

  private long maxSize;

  /**
   * Constructor.
   *
   * @param maxSize the max size the file can reach
   */
  public FileSizeBackupStrategy(long maxSize) {
    this.maxSize = maxSize;
  }

  @Override
  public boolean shouldBackup(File file) {
    return file.length() > maxSize;
  }
}
