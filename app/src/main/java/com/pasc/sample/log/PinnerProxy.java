package com.pasc.sample.log;

import com.pasc.lib.net.NetConfig;
import okhttp3.CertificatePinner;

public class PinnerProxy {

  public static void addCertPinner(NetConfig.Builder builder) {
    builder.certificatePinner(
        new CertificatePinner.Builder().add("*.sz.gov.cn", BuildConfig.CERT_FINGERS)
            .build());
  }
}
