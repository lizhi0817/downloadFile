package com.lee.t2019713.view;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lee.t2019713.R;
import com.lee.t2019713.base.BaseActivity;
import com.lee.t2019713.model.DataCall;
import com.lee.t2019713.model.UserInfo;
import com.lee.t2019713.presenter.MainPresenter;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIvPhone;
    /**
     * 手机号
     */
    private EditText mTvPhone;
    private ImageView mIvPwd;
    /**
     * 登陆密码
     */
    private EditText mTvPwd;
    private ImageView mIvShow;
    /**
     * 登录
     */
    private Button mBtLogin;

    boolean isShowPWD = false;
    String PHONE_NUMBER_REG = "^(1[3-9])\\d{9}$";
    private MainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

        mIvPhone = (ImageView) findViewById(R.id.iv_phone);
        mTvPhone = (EditText) findViewById(R.id.tv_phone);
        mIvPwd = (ImageView) findViewById(R.id.iv_pwd);
        mTvPwd = (EditText) findViewById(R.id.tv_pwd);
        mIvShow = (ImageView) findViewById(R.id.iv_show);
        mIvShow.setOnClickListener(this);
        mBtLogin = (Button) findViewById(R.id.bt_login);
        mBtLogin.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mMainPresenter = new MainPresenter(new MainDataCall());
    }

    @Override
    public void onClick(View v) {
        String phone = mTvPhone.getText().toString();
        String pwd = mTvPwd.getText().toString();
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_show:
                if (isShowPWD) {
                    mTvPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShowPWD = false;
                } else {
                    mTvPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPWD = true;
                }
                break;
            case R.id.bt_login:
                mMainPresenter.request(phone, pwd);
                break;
        }
    }

    class MainDataCall implements DataCall<UserInfo> {

        @Override
        public void onSuccess(UserInfo data) {
            Log.d("MainDataCall", data.toString());
            intent(Main2Activity.class);
        }

        @Override
        public void onError(String message) {
            Log.d("MainDataCall", message);
        }
    }
}
