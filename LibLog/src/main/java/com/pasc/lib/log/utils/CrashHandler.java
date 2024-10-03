package com.pasc.lib.log.utils;

/**
 * 异常捕获
 * Created by lingchun147 on 2018/3/24.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import com.pasc.lib.log.PascLog;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.pasc.lib.log.PascLog.SDCARD_LOG_FILE_DIR;
import static com.pasc.lib.log.PascLog.mContext;

public class CrashHandler implements UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private static final String SP_LAST_CRASH = "last_crash";
    //存储根目录
    public static String CRASH_LOG_PATH;
    // CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    // 是否已经初始化
    private static boolean isInit = false;
    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    // 程序的Context对象
    private Context mContext;
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<>();
    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private int fileSaveTime = 7;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     */
    public void init(final Context context, int fileSaveTime, String specDir) {
        if (context == null) {
            return;
        }
        mContext = context;
        // Android M will check permission dynamically, avoid crush here.
        if (!isInit) {
            this.fileSaveTime = fileSaveTime;
            CRASH_LOG_PATH = specDir;
            // 获取系统默认的UncaughtException处理器
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            // 设置该CrashHandler为程序的默认处理器
            Thread.setDefaultUncaughtExceptionHandler(CrashHandler.this);
            isInit = true;
        }
    }

    public void closeCatchCrash() {
        if (isInit && mDefaultHandler != null) {
            Thread.setDefaultUncaughtExceptionHandler(mDefaultHandler);
            isInit = false;
        }
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        SharedPreferences preferences = mContext.getSharedPreferences("log_sp", Context.MODE_PRIVATE);
        String lastCrash = preferences.getString(SP_LAST_CRASH, "");
        if (lastCrash.equals(ex.getMessage())) {
            return true;
        }
        preferences.edit().putString(SP_LAST_CRASH, ex.getMessage());
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        if (Thread.currentThread().getName().equals("main")) {
            new Thread() {
                @Override
                public void run() {
                    // 保存日志文件
                    /* String fileName = */
                    saveCrashInfo2File(ex);
                }
            }.start();
        } else {
            // 保存日志文件
            saveCrashInfo2File(ex);
        }

        return true;
    }

    /**
     * 收集设备参数信息
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("版本名称", versionName);
                infos.put("版本code", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {
        PascLog.rwl.writeLock().lock();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        // Log.d(WModel.CrashUpload, result);
        sb.append(result);
        BufferedWriter bw = null;
        try {
            // long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date()) + "";

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = SDCardUtils.getFileByDir(mContext, CRASH_LOG_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                //TODO 规避java.io.FileNotFoundException,暂时这样处理
                if(dir.exists()) {
                    //FileOutputStream fos = new FileOutputStream();
                    File folder = SDCardUtils.getFileByDir(mContext, CRASH_LOG_PATH);
                    File file = new File(folder + File.separator + time);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    bw = new BufferedWriter(new FileWriter(file, true));
                    bw.write(sb.toString());
                    bw.newLine();
                    bw.flush();
                    onClearFile();
                }
            }
            return time;
        } catch (Exception e) {
            PascLog.e(TAG, "an error occured while writing file...", e);

        }finally {
            if(bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            PascLog.rwl.writeLock().unlock();
        }
        return null;
    }

    public void onClearFile() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(CRASH_LOG_PATH);
            if (file.exists()) {
                File[] files = file.listFiles();
                if (files.length > fileSaveTime) {
                    int index = 0;
                    for (File temp : files) {
                        long time = System.currentTimeMillis() - temp.lastModified();
                        if (time / 1000 > 3600 * 24 * fileSaveTime) {
                            temp.delete();
                            index++;
                        }
                        if (index >= file.listFiles().length - fileSaveTime) break;
                    }
                }
            }
        }
    }
}