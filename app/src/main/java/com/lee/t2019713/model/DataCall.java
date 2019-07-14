package com.lee.t2019713.model;

/**
 * Created :  LiZhIX
 * Date :  2019/7/13 9:58
 * Description  :
 */
public interface DataCall<T> {

    void onSuccess(T data);

    void onError(String message);


}
