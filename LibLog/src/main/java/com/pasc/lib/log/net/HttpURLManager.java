package com.pasc.lib.log.net;

public class HttpURLManager {
  public static String HOST;
  //上传日志URL
  public static final String UPLOAD_URL = "smtapp/logCollection/upload.do";
  //查询是否要上传日志URL
  public static final String QUERY_USER_INFO_URL = "smtapp/logCollection/queryUserInfo.do";

  public static void setHOST(String host) {
    HOST = host;
  }

  public static String getHOST() {
    return HOST;
  }

  /**
   * 弃用
   * @param uploadUrl
   */
  public static void setUploadUrl(String uploadUrl) {

  }

  /**
   * 弃用
   * @param queryUserInfoUrl
   */
  public static void setQueryUserInfoUrl(String queryUserInfoUrl) {

  }
}
