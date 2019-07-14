package com.lee.t2019713.base;

import com.lee.t2019713.model.DataCall;
import com.lee.t2019713.model.Result;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created :  LiZhIX
 * Date :  2019/7/13 9:48
 * Description  :
 */
public abstract class BasePresenter {

    private DataCall mDataCall;

    public BasePresenter(DataCall dataCall) {
        mDataCall = dataCall;
    }

    protected abstract Observable mObservable(Object... args);

    public void request(Object... args) {
        mObservable(args)
                .compose(new ObservableTransformer() {
                    @Override
                    public ObservableSource apply(Observable upstream) {
                        return upstream.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribe(new Consumer<Result>() {
                    @Override
                    public void accept(Result o) throws Exception {
                        if (o.status.equals("0000")) {
                            mDataCall.onSuccess(o.result);
                        } else {
                            mDataCall.onError(o.message);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mDataCall.onError(throwable.getMessage());
                    }
                });
    }

    public void unbind() {
        mDataCall = null;
    }
}
