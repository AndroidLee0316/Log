package com.pasc.sample.log.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.pasc.lib.log.PascLog;
import com.pasc.lib.log.net.PascLogHttp;
import com.pasc.sample.log.R;
import com.pasc.sample.log.view.CircleView;

/**
 * Created by lingchun147 on 2018/9/3.
 */
public class MainActivity extends AppCompatActivity {
  private static final String HOST = "https://smt-app-stg.pingan.com.cn:10019/";
  private static final String QUERY_INFO_URL = HOST + "smtapp/logCollection/queryUserInfo.do";
  private static final String UPLOAD_FILE_URL = HOST + "smtapp/logCollection/upload.do";
  private CircleView mCircleView;
  private EditText etPhone;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mCircleView = findViewById(R.id.tasks_view);
    etPhone = findViewById(R.id.et_phone);
    PascLog.v("0 onCreate");
    PascLog.v("AAAAA", "1 onCreate");
    PascLog.v("3 onCreate");
    PascLog.v("BBBBB", "2 onCreate");
    PascLog.v("ddddd", "2 onCreate");
    PascLog.i("CCCCC","sasdaa", "aaassa","aaaaas");
    PascLog.d("{\"aaaa\":aaaa}");
    PascLog.v("{\"aaaa\":aaaa}");
    PascLog.tag("AAA").v("aaaa");
  }

  public void onClick(View v) {
    String mobile = etPhone.getText().toString();
    switch (v.getId()) {
      case R.id.tv_queryInfo:
        long curTime = System.currentTimeMillis();
        for(int i = 0; i<1000; i++){
          PascLog.v("ddddd", "2 onCreate");
        }
        System.out.println("user time : " + (System.currentTimeMillis() - curTime));
        if (mobile == null || TextUtils.isEmpty(mobile)) {
          showToast("用户标识不能为空");
          return;
        }
        PascLogHttp.queryInfoAndUploadFile(mobile, "1",
                new PascLogHttp.OnHttpResultListener() {
                  @Override public void onStart(String str) {
                    showToast(str);
                  }

                  @Override public void onProgress(long pro, double precent) {
                    if(precent == 0){
                      mCircleView.setVisibility(View.VISIBLE);
                    }
                    mCircleView.setProgress((int) precent);
                  }

                  @Override public void onFinish(int code, String result) {
                    showToast(result);
                    mCircleView.setProgress(100);
                  }
                });
        break;
      case R.id.tv_upload:
        if (mobile == null || TextUtils.isEmpty(mobile)) {
          showToast("用户标识不能为空");
          return;
        }
        PascLog.openReportLog(true);
        PascLogHttp.uploadFile(mobile, "0",
                new PascLogHttp.OnHttpResultListener() {
                  @Override public void onStart(String str) {
                    showToast(str);
                  }

                  @Override public void onProgress(long pro, double precent) {
                    if(precent == 0){
                      mCircleView.setVisibility(View.VISIBLE);
                    }
                    mCircleView.setProgress((int) precent);
                  }

                  @Override public void onFinish(int code, String result) {
                    showToast(result);
                    mCircleView.setProgress(100);
                  }
                });
        break;
      case R.id.tv_native_log:
        long nativeTime = System.currentTimeMillis();
        for(int i = 0; i < 1000; i++){
          Log.v("native Log", i + "");
        }
        System.out.println("native log : " + (System.currentTimeMillis() - nativeTime));
        break;
      case R.id.tv_tag_pascLog:
        long pascTagTime = System.currentTimeMillis();
        for(int i = 0; i < 1000; i++){
          PascLog.v("pasctagLog", i + "");
        }
        System.out.println("pasc tag log : " + (System.currentTimeMillis() - pascTagTime));
        break;
      case R.id.tv_no_tag_pascLog:
        long pascNoTagTime = System.currentTimeMillis();
        for(int i = 0; i < 1000; i++){
          PascLog.v(i + "");
        }
        System.out.println("pasc no tag time log : " + (System.currentTimeMillis() - pascNoTagTime));
        break;
    }
  }

  @SuppressLint("IncorrectToast") private void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }
}
