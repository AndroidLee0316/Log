package com.pasc.sample.log;

import com.pasc.lib.log.printer.file.backup.BackupStrategy;
import java.io.File;

/**
 * Created by lingchun147 on 2018/9/5.
 */
class MyBackupStrategy implements BackupStrategy {
  @Override public boolean shouldBackup(File file) {
    return false;
  }
}
