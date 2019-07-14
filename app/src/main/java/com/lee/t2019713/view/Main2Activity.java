package com.lee.t2019713.view;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.t2019713.R;
import com.lee.t2019713.base.BaseActivity;
import com.lee.t2019713.utils.DownLoadFile;

public class Main2Activity extends BaseActivity implements View.OnClickListener {

    private ProgressBar mProgress;
    /**
     * 当前下载进度：
     */
    private TextView mProgressTv;
    /**
     * 下载
     */
    private Button mDownload;
    /**
     * 暂停
     */
    private Button mPause;
    /**
     * 继续
     */
    private Button mStart;

    private String loadUrl = "http://gdown.baidu.com/data/wisegame/d2fbbc8e64990454/wangyiyunyinle_87.apk";
    private String filePath = Environment.getExternalStorageDirectory() + "/" + "网易云音乐.apk";
    private DownLoadFile mLoadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadFile != null) {
            mLoadFile.onDestroy();
        }
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main2;
    }

    @Override
    protected void initView() {

        mProgress = (ProgressBar) findViewById(R.id.progress);
        mProgressTv = (TextView) findViewById(R.id.progressTv);
        mDownload = (Button) findViewById(R.id.download);
        mDownload.setOnClickListener(this);
        mPause = (Button) findViewById(R.id.pause);
        mPause.setOnClickListener(this);
        mStart = (Button) findViewById(R.id.start);
        mStart.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        mProgress.setMax(100);
        mLoadFile = new DownLoadFile(filePath, loadUrl, this);
        mLoadFile.setmDownListener(new DownLoadFile.DownListener() {
            @Override
            public void onProgress(int progress) {
                mProgressTv.setText("当前进度 ：" + progress + " %");
                mProgress.setProgress(progress);
            }

            @Override
            public void onSuccess(String success) {
                Toast.makeText(Main2Activity.this, success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(Main2Activity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.download:
                    mLoadFile.downLoad();
                    mDownload.setClickable(false);
                break;
            case R.id.pause:
                mLoadFile.pause();
                break;
            case R.id.start:
                mLoadFile.start();
                break;
        }
    }

}
