package com.lee.t2019713.base;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.lee.t2019713.utils.receiver.Receiver;
import com.gyf.immersionbar.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created :  LiZhIX
 * Date :  2019/7/13 10:07
 * Description  :
 * <p>
 * <p>
 * 4）	BasePresenter基类，实现View的绑定和解绑，避免内存泄漏；
 * 5）	封装BaseActivity和BaseFragment基类，实现初始化视图、初始化数据方法，
 * 页面跳转方法、沉浸式状态栏适配（透明色，灰色字体，并做好布局适配），
 * 封装网络状态监听；
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Receiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this)
                .transparentStatusBar()  //透明状态栏，不写默认透明色
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();

        setContentView(setLayoutId());
        initView();
        initData();

        mReceiver = new Receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getInfo(Object object) {
        if (object instanceof Boolean) {
            boolean mNetWorkInfo = (boolean) object;
            if (mNetWorkInfo) {
                Toast.makeText(this, "网络连接", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "网络断开连接", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected abstract int setLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    public void intent(Class mActivity) {
        Intent intent = new Intent(this, mActivity);
        startActivity(intent);
    }

    public void intent(Class mActivity, Bundle bundle) {
        Intent intent = new Intent(this, mActivity);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销EventBus
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        ImmersionBar.destroy(this, null);
        unregisterReceiver(mReceiver);
    }
}
