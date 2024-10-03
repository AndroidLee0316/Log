package com.pasc.lib.log.net;

import com.pasc.lib.log.net.param.InfoAndUploadFileParams;
import com.pasc.lib.log.net.resp.InfoAndUploadFileResp;
import com.pasc.lib.log.net.resp.UploadFileResp;
import com.pasc.lib.net.param.BaseParam;
import com.pasc.lib.net.resp.BaseResp;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by lingchun147 on 2019/5/20.
 */
public interface PascLogApi {
  @Multipart @POST(HttpURLManager.QUERY_USER_INFO_URL)
  Single<BaseResp<InfoAndUploadFileResp>> queryInfoAndUploadFile(
          @Part("jsonData") BaseParam<InfoAndUploadFileParams> param);

  @Multipart @POST(HttpURLManager.UPLOAD_URL)
  Single<BaseResp<UploadFileResp>> uploadFile(@Part("jsonData") RequestBody param,
          @Part MultipartBody.Part part);
}
