package com.pasc.lib.log.net.resp;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lingchun147 on 2019/5/20.
 */
public class InfoAndUploadFileResp {
  @SerializedName("isCollect") public String collect;
  public boolean isCollect(){

    return "Y".equalsIgnoreCase(collect);
  }
}
