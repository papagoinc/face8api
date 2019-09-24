package com.papagoinc.face8api.Utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

//     https://www.jianshu.com/p/adc05195417b
public class MyOkHttpUtils {
    private static final String TAG = "MyOkHttp";

    public static final long DEFAULT_READ_TIMEOUT_MILLIS = 10 * 1000;
    public static final long DEFAULT_WRITE_TIMEOUT_MILLIS = 10 * 1000;
    public static final long DEFAULT_CONNECT_TIMEOUT_MILLIS = 10 * 1000;
    private static final long HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;
    private static volatile MyOkHttpUtils  sInstance;
    private OkHttpClient  mOkHttpClient;

    @SuppressLint("TrulyRandom")
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * @param context
     */
    private MyOkHttpUtils(Context context) {
        /*
        X509TrustManager trustManager = null;
        SSLSocketFactory sslSocketFactory = null;
        try {
            final InputStream inputStream;
            inputStream = context.getAssets().open("yuzetianxia.com.crt"); // 得到證書的輸入流


            trustManager = trustManagerForCertificates(inputStream);//以流的方式讀入證書
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            sslSocketFactory = sslContext.getSocketFactory();

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
            //    Log.d(TAG, message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)

                .addInterceptor(loggingInterceptor)

                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new TrustAllHostnameVerifier())

                //.sslSocketFactory(sslSocketFactory, trustManager)
                .build();


    }

    private MyOkHttpUtils() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
           //     Log.d(TAG, message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)

                .addInterceptor(loggingInterceptor)

                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new TrustAllHostnameVerifier())

                .build();
    }

    public static MyOkHttpUtils getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MyOkHttpUtils.class) {
                if (sInstance == null) {
                    sInstance = new MyOkHttpUtils(context);
                }
            }
        }
        return sInstance;
    }

    public static MyOkHttpUtils getInstance() {
        if (sInstance == null) {
            synchronized (MyOkHttpUtils.class) {
                if (sInstance == null) {
                    sInstance = new MyOkHttpUtils();
                }
            }
        }
        return sInstance;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public void setCache(Context appContext) {
        final File baseDir = appContext.getApplicationContext().getCacheDir();
        if (baseDir != null) {
            final File cacheDir = new File(baseDir, "HttpResponseCache");
            mOkHttpClient.newBuilder().cache((new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE)));
        }
    }

    public static void getAsyn(Context context,String url, Callback callBack){
        OkHttpClient okHttpClient = MyOkHttpUtils.getInstance(context).getOkHttpClient();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(callBack);
    }

    public static void postAsyn(Context context,String url, RequestBody body, Callback callBack){
        OkHttpClient okHttpClient = MyOkHttpUtils.getInstance(context).getOkHttpClient();
        Request request = new Request.Builder().url(url).post(body).build();
        okHttpClient.newCall(request).enqueue(callBack);
    }

    public static void getAsyn(String url, Callback callBack){
        OkHttpClient okHttpClient = MyOkHttpUtils.getInstance().getOkHttpClient();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(callBack);
    }

    public static void postAsyn(String url, RequestBody body, Callback callBack){
        OkHttpClient okHttpClient = MyOkHttpUtils.getInstance().getOkHttpClient();
        Request request = new Request.Builder().url(url).post(body).build();
        okHttpClient.newCall(request).enqueue(callBack);
    }

    public static String post(String url, RequestBody body) {
        String result = "";

        OkHttpClient okHttpClient = MyOkHttpUtils.getInstance().getOkHttpClient();
        Request request = new Request.Builder().url(url).post(body).build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                result = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}

