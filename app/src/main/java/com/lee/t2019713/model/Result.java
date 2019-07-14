package com.lee.t2019713.model;

/**
 * Created :  LiZhIX
 * Date :  2019/7/13 10:02
 * Description  :
 */
public class Result<T> {
    public String status = "-1";
    public String message = "请求失败";
    public T result;

    public Result(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
