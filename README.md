# downloadFile
## 沉浸式 ##
````
ImmersionBar.with(this)
                .transparentStatusBar()  //透明状态栏，不写默认透明色
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();
````
## 通过广播设置网络监听 ##
1.创建一个类，继承BroadcastReceiver并实现onReceive方法
````
if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (NetworkInfo.State.CONNECTED == networkInfo.getState()) {
		//有网的情况下
                EventBus.getDefault().postSticky(true);
            } else {
		//没网的情况下
                EventBus.getDefault().postSticky(false);
            }
        }
````
2.在需要的地方动态注册广播
```
Receiver mReceiver = new Receiver();
IntentFilter filter = new IntentFilter();
filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
registerReceiver(mReceiver, filter);
```
3.通过Eventbus发送广播
````
//注册
 if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
````
````
//注销EventBus
if (EventBus.getDefault().isRegistered(this))
   EventBus.getDefault().unregister(this);
ImmersionBar.destroy(this, null);
unregisterReceiver(mReceiver);
````
````
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
````
## 如果需要加载图片的话推荐使用Glide或者Fresco ##
````
DiskCacheConfig cacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryName("iamges")
                .setBaseDirectoryPath(Environment.getExternalStorageDirectory())
                .build();
        //设置磁盘缓存的配置,生成配置文件
ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setMainDiskCacheConfig(cacheConfig)
                .build();

Fresco.initialize(this, config);
````
## 重点！！！实现多线程断点续传文件下载  ##
1.接口回调实现对进度条、下载成功、失败的监听
````
public void setmDownListener(DownListener mDownListener) {
        this.mDownListener = mDownListener;
    }

    public interface DownListener {
        void onProgress(int progress);

        void onSuccess(String success);

        void onError(String error);
    }
````
2.通过RandomAccessFile这个类，实现随机访问流从网络上下载文件。这里使用的是HttpURLConnection
````
 public void downLoad() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //判断线程数组是否存在
                if (mThread == null)
                    mThread = new Thread[mThreadCount];
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(mFilePath, "rwd");
                    URL url = new URL(downPath);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    if (connection.getResponseCode() == 200) {
                        mFileLength = connection.getContentLength();
                        randomAccessFile.setLength(mFileLength);
                        randomAccessFile.close();
                        //每个线程下载的分块
                        int threadIndex = mFileLength / mThreadCount;
                        SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                        //这个参数没有其他功能只是用来记录进度条的进度
                        mCurrentLength = sp.getInt(CURR_LENGTH, 0);
                        for (int i = 0; i < mThread.length; i++) {
                            //每个线程开始下载的标记
                            int start = sp.getInt(SP_NAME + (i + 1), i * threadIndex);
                            //每个线程结束下载的标记
                            int end = (i + 1) * threadIndex - 1;
                            //将最后一个线程结束位置扩大，防止文件下载不完全，大了不影响，小了文件失效
                            if (i + 1 == mThreadCount) {
                                end = end * 2;
                            }
                            mThread[i] = new DownThread(start, end, i + 1);
                            mThread[i].start();
                        }
                    } else {
                        handler.sendEmptyMessage(FAILURE);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(FAILURE);
                }
            }
        }).start();

    }
````
3.实现线程
```
class DownThread extends Thread {
        private boolean isGoOn = true;  //判断是否下载
        private int endIndex;           //结束节点
        private int currentIndex;       //已经下载到的节点
        private int startIndex;         //开始节点
        private int threadId;           //线程ID

        public DownThread(int startIndex, int endIndex, int threadId) {
            this.endIndex = endIndex;
            this.currentIndex = startIndex;
            this.startIndex = startIndex;
            this.threadId = threadId;
            runningThreadCount++;		//每运行一次，下载的线程数量+1
        }

        //取消下载
        public void cancle() {
            isGoOn = false;
        }

        @Override
        public void run() {
            try {
	//通过Range来设置文件的开始的地方和结束的地方
                SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                RandomAccessFile randomAccessFile = new RandomAccessFile(mFilePath, "rwd");
                URL url = new URL(downPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);
	//206表示部分文件下载
                if (connection.getResponseCode() == 206) {
                    if (!isGoOn) {
                        return;
                    }
	//设置偏移
                    randomAccessFile.seek(startIndex);
                    int leng = 0;
                    byte[] bytes = new byte[1024];
                    while ((leng = connection.getInputStream().read(bytes)) != -1) {
                        if (mDownListener != null) {
                            mCurrentLength += leng;
	//设置百分比
                            int progress = (int) ((float) mCurrentLength / (float) mFileLength * 100);
                            handler.sendEmptyMessage(progress);
                        }
                        randomAccessFile.write(bytes, 0, leng);
                        //记载指针指到最后那个位置
                        currentIndex += leng;
                        synchronized (DOWN_PAUSE) {
                            if (stateDownload.equals(DOWN_PAUSE)) {
                                DOWN_PAUSE.wait();
                            }
                        }
                    }
                    randomAccessFile.close();
                    runningThreadCount--;
                    if (!isGoOn) {
                        if (currentIndex < endIndex) {
                            sp.edit().putInt(SP_NAME + threadId, mCurrentLength).apply();
                            sp.edit().putInt(CURR_LENGTH, currentIndex).apply();
                        }
                        return;
                    }
                    if (runningThreadCount == 0) {
                        sp.edit().clear().apply();
                        handler.sendEmptyMessage(SUCCESS);
                        handler.sendEmptyMessage(100);
                        mThread = null;
                    }
                } else {//如果不是206下发失败
                    sp.edit().clear().apply();
                    handler.sendEmptyMessage(FAILURE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(FAILURE);
            }
        }
    }
```
4.通过handler更新主线程
```
 private final int SUCCESS = 0x00000101;
    private final int FAILURE = 0x00000102;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (mDownListener != null) {
                if (msg.what == SUCCESS) {
                    mDownListener.onSuccess("下载成功");
                } else if (msg.what == FAILURE) {
                    mDownListener.onError("下载失败");
                } else {
                    mDownListener.onProgress(msg.what);
                }
            }
        }
    };
```
5.设置暂停，继续，销毁的方法
```
public void pause() {
        if (mThread != null) {//如果不等于null 说明线程在跑 run方法在执行
            stateDownload = DOWN_PAUSE;
        }
    }
```
```
public void start() {
        if (mThread != null)
            synchronized (DOWN_PAUSE) {
                stateDownload = DOWN_START;
                DOWN_PAUSE.notifyAll();
            }
    }
```
```
public void onDestroy() {
        if (mThread != null)
            mThread = null;
    }
```
