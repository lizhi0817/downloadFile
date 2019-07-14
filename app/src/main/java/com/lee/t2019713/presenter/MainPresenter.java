package com.lee.t2019713.presenter;

import com.lee.t2019713.base.BasePresenter;
import com.lee.t2019713.model.DataCall;
import com.lee.t2019713.utils.http.IRequest;
import com.lee.t2019713.utils.http.NetWorkUtils;

import io.reactivex.Observable;

/**
 * Created :  LiZhIX
 * Date :  2019/7/14 14:39
 * Description  :
 */
public class MainPresenter extends BasePresenter {


    public MainPresenter(DataCall dataCall) {
        super(dataCall);
    }

    @Override
    protected Observable mObservable(Object... args) {
        IRequest iRequest = new NetWorkUtils().create(IRequest.class);
        return iRequest.login((String) args[0], (String) args[1]);
    }
}
