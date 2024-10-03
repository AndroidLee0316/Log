package com.pasc.lib.log.utils;

import android.util.Log;
import com.pasc.lib.log.PascLog;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
    private final static String TAG = "FileUtil";
    private final static int BUFF_SIZE = 2048;

    /**
     * 压缩文件
     *
     * @param parentFile  需要压缩的文件
     * @param zipFilePath 被压缩后存放的路径
     * @return 成功返回 true，否则 false
     */
    public static synchronized boolean zipFiles(File parentFile, String zipFilePath) {
        if (parentFile == null) {
            throw new NullPointerException("file == null");
        }
        File[] list = parentFile.listFiles();
        if (list == null || list.length == 0) {
            return false;
        }

        File zipFile = new File(zipFilePath);
        if(PascLog.IS_DEBUG)
            Log.v(TAG, "" + zipFilePath);
        if (zipFile.exists()) {
            zipFile.delete();
        }
        boolean result = false;
        ZipOutputStream zos = null;
        try {
            createFile(zipFile);
            zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFilePath)));
            for (File file : list) {
                if (file == null || !file.exists()) {
                    continue;
                }
                if (file.isDirectory()) {
                    recursionZip(zos, file, file.getName() + File.separator);
                } else {
                    recursionZip(zos, file, "");
                }
            }
            result = true;
            zos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            if(PascLog.IS_DEBUG)
                Log.e(TAG, "zip file failed err: " + e.getMessage());
        } finally {
            try {
                if (zos != null) {
                    zos.closeEntry();
                    zos.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }

    private static void createFile(File file) {
        if (file == null || file.exists()) {
            return;
        }
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.toString();
        }
    }

    private static void recursionZip(ZipOutputStream zipOut, File file, String baseDir)
            throws Exception {
        if (file.isDirectory()) {
            if(PascLog.IS_DEBUG)
                Log.i(TAG, "the file is dir name -->>" + file.getName() + " the baseDir-->>>" + baseDir);
            File[] files = file.listFiles();
            for (File fileSec : files) {
                if (fileSec == null) {
                    continue;
                }
                if (fileSec.isDirectory()) {
                    baseDir = file.getName() + File.separator + fileSec.getName() + File.separator;
                    if(PascLog.IS_DEBUG)
                        Log.i(TAG, "basDir111-->>" + baseDir);
                    recursionZip(zipOut, fileSec, baseDir);
                } else {
                    if(PascLog.IS_DEBUG)
                        Log.i(TAG, "basDir222-->>" + baseDir);
                    recursionZip(zipOut, fileSec, baseDir);
                }
            }
        } else {
            if(PascLog.IS_DEBUG)
                Log.i(TAG, "the file name is -->>" + file.getName() + " the base dir -->>" + baseDir);
            byte[] buf = new byte[BUFF_SIZE];
            InputStream input = new BufferedInputStream(new FileInputStream(file));
            zipOut.putNextEntry(new ZipEntry(baseDir + file.getName()));
            int len;
            while ((len = input.read(buf)) != -1) {
                zipOut.write(buf, 0, len);
            }
            input.close();
        }
    }


    /**
     * 删除目录下所有的文件(保存子目录)
     *
     * @param root
     */
    public static void deleteAllFiles(File root) {
        PascLog.rwl.readLock().lock();
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                } else {
                    if (f.exists()) { // 判断是否存在
                        try {
                            f.delete();
                        } catch (Exception e) {

                        }
                    }
                }
            }
        PascLog.rwl.readLock().unlock();
    }

    /**
     * 删除目录下的所有文件包括子目录
     *
     * @param root
     */
    public static void deleteAllFilesAndDirectory(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {

                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        try {
                            f.delete();
                        } catch (Exception e) {

                        }
                    }
                }
            }
    }

}
