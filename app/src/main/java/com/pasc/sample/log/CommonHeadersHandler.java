package com.pasc.sample.log;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.pasc.lib.net.HttpCommonParams;
import java.util.Map;

public class CommonHeadersHandler implements HttpCommonParams.InjectCommonHeadersHandler {
    private String deviceId;
    private String versionName;
    private String packageName;
    private String channel;
    private String platform;
    private String screenDpi;
    private static volatile boolean isInit;
    private Context context;

    @Override
    public void onInjectCommonHeaders(Map<String, String> map) {
        try {
            if (!isInit) {
                initValue(context);
                isInit = true;
            }
            map.put("x-device-id", deviceId);
            map.put("x-os-type", "2");
            map.put("x-os-version", Build.VERSION.SDK_INT + "");
            map.put("x-app-version", versionName);
            map.put("x-app-name", packageName);
            map.put("timestamp", System.currentTimeMillis() + "");
            map.put("x-channel", !TextUtils.isEmpty(channel) ? channel : "");
            map.put("x-screen-dpi", screenDpi);
            map.put("x-app-platform", platform);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initValue(Context context) {
        //deviceId = DeviceUtils.getDeviceId(context);
        packageName = context.getApplicationInfo().packageName;
        platform = Build.BRAND;
    }

    public CommonHeadersHandler(Context context) {
        this.context = context;
    }
}
