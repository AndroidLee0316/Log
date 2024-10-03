package com.pasc.lib.log.net.param;

import com.google.gson.annotations.SerializedName;
import com.pasc.lib.log.PascLog;
import com.pasc.lib.log.net.PascLogHttp;


/**
 * Created by lingchun147 on 2019/5/20.
 */
public class UploadFileParams {
  @SerializedName("systemId") public String systemId;
  @SerializedName("userId") public String userId;
  @SerializedName("osType") public String osType;
  @SerializedName("appVersion") public String appVersion;
  @SerializedName("uploadType") public String uploadType;
  @SerializedName("phoneMode") public String phoneMode;
  @SerializedName("phoneVersion") public String phoneVersion;

  public UploadFileParams(String userId, String uploadType) {
    this.systemId = PascLog.mSystemId;
    this.userId = userId;
    this.osType = "android";
    this.appVersion = PascLogHttp.getAppVersion();
    this.uploadType = uploadType;
    this.phoneMode = android.os.Build.MODEL;
    this.phoneVersion = android.os.Build.VERSION.RELEASE;
  }
}
