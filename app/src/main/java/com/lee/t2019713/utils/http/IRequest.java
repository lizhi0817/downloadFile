package com.lee.t2019713.utils.http;

import com.lee.t2019713.model.Result;
import com.lee.t2019713.model.UserInfo;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created :  LiZhIX
 * Date :  2019/7/13 9:46
 * Description  :
 */
public interface IRequest {

    @FormUrlEncoded
    @POST("user/v1/login")
    Observable<Result<UserInfo>> login(@Field("phone") String phone,
                                       @Field("pwd") String pwd);


}
