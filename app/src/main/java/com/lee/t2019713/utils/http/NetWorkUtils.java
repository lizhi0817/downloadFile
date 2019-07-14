package com.lee.t2019713.utils.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created :  LiZhIX
 * Date :  2019/7/13 9:48
 * Description  :
 */
public class NetWorkUtils {
    private static NetWorkUtils instance;
    private Retrofit mRetrofit;

    public NetWorkUtils() {
        init();
    }

    private void init() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://mobile.bwstudent.com/small/")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    private static NetWorkUtils instance() {

        if (null == instance) {
            instance = new NetWorkUtils();
        }

        return instance;
    }

    public <T> T create(Class<T> s) {
        return mRetrofit.create(s);
    }
}
