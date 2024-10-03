package com.pasc.lib.log.net;

import com.google.gson.Gson;
import com.pasc.lib.log.PascLog;
import com.pasc.lib.log.net.param.InfoAndUploadFileParams;
import com.pasc.lib.log.net.param.UploadFileParams;
import com.pasc.lib.log.net.resp.InfoAndUploadFileResp;
import com.pasc.lib.log.net.resp.UploadFileResp;
import com.pasc.lib.net.ApiGenerator;
import com.pasc.lib.net.param.BaseParam;
import com.pasc.lib.net.transform.RespTransformer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by lingchun147 on 2019/5/20.
 */
public class PascLogBiz {


  /**
   * 查询是否需要上传日志
   */
  public static Single<InfoAndUploadFileResp> queryInfoAndUploadFile (String mobile) {
    InfoAndUploadFileParams params = new InfoAndUploadFileParams();
    params.systemId = PascLog.mSystemId;
    params.userId = mobile;
    return ApiGenerator.createApi(PascLogApi.class)
            .queryInfoAndUploadFile(new BaseParam<>(params))
            .compose(RespTransformer.<InfoAndUploadFileResp>newInstance())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
  }

  /**
   * 上传日志
   * @param mobile
   * @param uploadType
   * @param logFile
   * @return
   */
  public static Single<UploadFileResp> uploadFile(String mobile,
          String uploadType, File logFile) {

    RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), logFile);
    MultipartBody.Part filePart =
            MultipartBody.Part.createFormData("smt_uploadAttr", logFile.getName(),
                    fileBody);//"smt_uploadAttr" 后台接收图片流的参数名

    UploadFileParams params = new UploadFileParams(mobile, uploadType);
    RequestBody paramBody =
            RequestBody.create(null, new Gson().toJson(new BaseParam<>(params)));

    return ApiGenerator.createApi(PascLogApi.class)
            .uploadFile(paramBody, filePart)
            .compose(RespTransformer.<UploadFileResp>newInstance())
            .subscribeOn(Schedulers.io());
  }



}
