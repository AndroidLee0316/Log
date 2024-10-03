package com.pasc.lib.log.net;

import android.content.pm.PackageInfo;
import android.text.TextUtils;
import com.pasc.lib.log.PascLog;
import com.pasc.lib.log.net.resp.InfoAndUploadFileResp;
import com.pasc.lib.log.net.resp.UploadFileResp;
import com.pasc.lib.log.utils.FileUtil;
import com.pasc.lib.log.utils.SDCardUtils;
import com.pasc.lib.net.resp.BaseRespObserver;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.List;
import org.reactivestreams.Publisher;

import static com.pasc.lib.log.PascLog.SDCARD_LOG_FOLDOR;

/**
 * 上传日志和查询是否上传日志
 */
public class PascLogHttp {

  private static final String TAG = "uploadFile";

  /**
   * 上传日志文件
   * @param mobile 用户标识
   * @param uploadType 日志上传方式 0：主动上传（意见反馈）1：被动上传（下发指令）
   * @param listener  网络请求回调
   */
  public static void uploadFile(final String mobile,
          final String uploadType, final OnHttpResultListener listener) {
    if (!PascLog.isOpenReportLog()) {
      return;
    }

    Flowable.create(new FlowableOnSubscribe<String>() {
      @Override public void subscribe(FlowableEmitter<String> emitter) throws Exception {
        emitter.onNext(SDCardUtils.getFileByDir(PascLog.mContext, SDCARD_LOG_FOLDOR).getAbsolutePath());
      }
       }, BackpressureStrategy.DROP)
            .map(new Function<String, File>() {
              @Override public File apply(String filePath) throws Exception {
                return zipReportFile(filePath);
              }
            })

            .flatMap(new Function<File, Publisher<UploadFileResp>>() {
              @Override public Publisher<UploadFileResp> apply(final File logfile) throws Exception {
                return  PascLogBiz.uploadFile(mobile, uploadType, logfile)
                        .doOnSuccess(new Consumer<UploadFileResp>() {
                          @Override public void accept(UploadFileResp uploadFileResp)
                                  throws Exception {
                            FileUtil.deleteAllFiles(logfile.getParentFile().getParentFile());
                          }
                        }).toFlowable();
              }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<UploadFileResp>() {
              @Override public void accept(UploadFileResp uploadFileResps) throws Exception {
                if (listener != null) {
                  listener.onFinish(0x00, "文件上传成功");
                }

              }
            }, new Consumer<Throwable>() {
              @Override public void accept(Throwable throwable) throws Exception {
                if (listener != null) {
                  listener.onFinish(0x00, "文件上传失败");
                }
              }
            });
  }



  private static File zipReportFile(String filePath) {
    File logPath = new File(filePath);
    if (logPath == null || !logPath.exists()) {
      return null;
    }
    String zipFilePath = logPath.getParent() + File.separator + "zip" + File.separator + System.currentTimeMillis() + ".zip";
    FileUtil.zipFiles(logPath, zipFilePath);
    return new File(zipFilePath);
  }

  /**
   * 查询采集用户日志接口，跟据查询结果，直接上传日志，查询和上传地址使用默认的地址
   * @param mobile 查询标识(手机号)
   */
  public static void queryInfoAndUploadFile(final String mobile){
    queryInfoAndUploadFile(mobile, null);
  }
  /**
   * 查询采集用户日志接口，跟据查询结果，直接上传日志，查询和上传地址使用默认的地址
   * @param mobile 查询标识(手机号)
   * @param listener   请求回调
   */
  public static void queryInfoAndUploadFile(final String mobile, final OnHttpResultListener listener){
    queryInfoAndUploadFile(mobile,"1", listener);
  }

  /**
   * 查询采集用户日志接口，跟据查询结果，直接上传日志，查询和上传地址使用默认的地址
   * @param mobile 查询标识(手机号)
   * @param uploadType 上传方式 0：主动上传（意见反馈）1：被动上传（下发指令）
   * @param listener   请求回调
   */
  public static void queryInfoAndUploadFile(final String mobile, final String uploadType, final OnHttpResultListener listener){

    if (TextUtils.isEmpty(mobile)) {
      return;
    }
    PascLogBiz.queryInfoAndUploadFile(mobile)
            .subscribe(new BaseRespObserver<InfoAndUploadFileResp>() {
              @Override public void onSuccess(InfoAndUploadFileResp infoAndUploadFileResp) {
                super.onSuccess(infoAndUploadFileResp);
                if(infoAndUploadFileResp.isCollect()) {
                  PascLog.openReportLog(true);
                  uploadFile(mobile, uploadType, listener);
                }
              }

              @Override public void onError(int i, String s) {
                super.onError(i, s);
              }
            });

  }

  public interface OnHttpResultListener {
    void onStart(String str);

    void onProgress(long pro, double precent);

    void onFinish(int code, String result);
  }

  /**
   * 获取app版本号
   */
  public static String getAppVersion() {
    if (PascLog.mContext == null) {
      return "";
    }
    try {
      PackageInfo info = PascLog.mContext.getPackageManager()
              .getPackageInfo(PascLog.mContext.getPackageName(), 0);
      return info.versionName;
    } catch (Exception e) {
      return "";
    }
  }
}
