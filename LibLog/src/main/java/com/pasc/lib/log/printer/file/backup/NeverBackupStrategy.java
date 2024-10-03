
package com.pasc.lib.log.printer.file.backup;

import java.io.File;

/**
 * Never backup the log file.
 *
 * @since 1.3.0
 */
public class NeverBackupStrategy implements BackupStrategy {

  @Override
  public boolean shouldBackup(File file) {
    return false;
  }
}
