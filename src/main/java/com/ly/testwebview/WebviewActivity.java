package com.ly.testwebview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

/**
 * Webview控件 建议在使用时再动态创建添加
 */
public class WebviewActivity extends Activity
{
    //    private final String htmlCode = "<img src=\"http://192.168.1.103:8080/Test/test.png\"/>";
    private final String htmlCode = "Hello World";

    private Context mContext;

    private Button mBtn_loadUrl;
    private Button mBtn_loadAppHtmlFile;
    private Button mBtn_loadNativeHtmlFile;
    private Button mBtn_clearCache;
    private Button mBtn_clearHistory;
    private Button mBtn_clearFormData;
    private Button mBtn_selfAdaptionSize;
    private Button mBtn_forbidZoom;
    private Button mBtn_supportZoom;
    private Button mBtn_setZoomControl;
    private Button mBtn_hideZoomButton;
    private Button mBtn_showZoomButton;
    private Button mBtn_otherSettings;
    private Button mBtn_networkLoad;
    private Button mBtn_nativeLoad;
    private Button mBtn_outLineLoad;
    private WebView mWv_web;
    private WebSettings mWebSettings;


    private WebViewClient mWebViewClient = new WebViewClient()
    {
        /**
         * 开始载入页面调用的，我们可以设定一个loading的页面，告诉用户程序在等待网络响应。
         * @param view
         * @param url
         * @param favicon
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            Lg.d("onPageStarted：" + url);
            Toast.makeText(mContext, "正常加载页面,请稍后", Toast.LENGTH_SHORT).show();
            super.onPageStarted(view, url, favicon);
        }


        /**
         * 在页面加载结束时调用。我们可以关闭loading 条，切换程序动作。
         *
         * @param view
         * @param url
         */
        @Override
        public void onPageFinished(WebView view, String url)
        {
            Lg.d("onPageFinished：" + url);
            Toast.makeText(mContext, "加载页面完成,欢迎使用", Toast.LENGTH_SHORT).show();
            super.onPageFinished(view, url);
        }


        /**
         * url地址变更，此函数回调在api24 android7.0版本已弃用，但还会有回调接收
         * @param view
         * @param url
         * @return true为自己处理/false为默认处理
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            Lg.d("shouldOverrideUrlLoading 24d：" + url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        /**
         * url地址变更，此函数回调为api24 android7.0开始使用，7.0以下使用上面面的函数回调
         * @param view
         * @param request
         * @return true为自己处理/false为默认处理
         */
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
        {
            Lg.d("shouldOverrideUrlLoading  24t：" + request.getUrl().toString());
            return super.shouldOverrideUrlLoading(view, request);
        }

        /**
         * 加载给定url上的指定资源,每个资源的加载都会调用
         *
         * @param view
         * @param url
         */
        @Override
        public void onLoadResource(WebView view, String url)
        {
            Lg.d("onLoadResource：" + url);
            super.onLoadResource(view, url);
        }

        /**
         * 请求url失败响应的回调
         * 此函数回调为api24 android7.0开始使用，7.0以下使用上面面的函数回调
         * @param view
         * @param errorCode
         * @param description
         * @param failingUrl
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            Lg.d("onReceivedError   23d");
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        /**
         * 请求url失败响应的回调
         * 此函数回调为api23 android6.0开始使用，6.0以下使用上面面的函数回调
         * @param view
         * @param request
         * @param error
         */
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
        {
            Lg.d("onReceivedError   23t");
            super.onReceivedError(view, request, error);
        }

        /**
         * Webview本身不处理https请求,需在此回调中自行处理
         * @param view
         * @param handler
         * @param error
         */
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
        {
            handler.proceed();    //表示等待证书响应
            // handler.cancel();      //表示挂起连接，为默认方式
            // handler.handleMessage(null);    //可做其他处理
            //            super.onReceivedSslError(view, handler, error);
        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient()
    {
        /**
         * 获得网页的加载进度并显示
         * @param view
         * @param newProgress
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress)
        {
            //            super.onProgressChanged(view, newProgress);
            Lg.d("onProgressChanged");
            if (newProgress < 100)
            {
                String progress = newProgress + "%";
                Lg.i("网页正在加载:" + progress);
            }
            else
            {
                Lg.i("网页加载完成");
            }
        }

        /**
         * 获取网页标题
         * @param view
         * @param title
         */
        @Override
        public void onReceivedTitle(WebView view, String title)
        {
            //            super.onReceivedTitle(view, title);
            Lg.i("网页标题为：" + title);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.mContext = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initEvent();
        mWebSettings = mWv_web.getSettings();
        mWv_web.setWebViewClient(mWebViewClient);
        mWv_web.setWebChromeClient(mWebChromeClient);
        selfAdaptionSize();
        otherSettings();
    }

    private void initViews()
    {
        mBtn_loadUrl = findViewById(R.id.btn_loadUrl);
        mBtn_loadAppHtmlFile = findViewById(R.id.btn_loadAppHtmlFile);
        mBtn_loadNativeHtmlFile = findViewById(R.id.btn_loadNativeHtmlFile);
        mBtn_clearCache = findViewById(R.id.btn_clearCache);
        mBtn_clearHistory = findViewById(R.id.btn_clearHistory);
        mBtn_clearFormData = findViewById(R.id.btn_clearFormData);
        mBtn_selfAdaptionSize = findViewById(R.id.btn_selfAdaptionSize);
        mBtn_forbidZoom = findViewById(R.id.btn_forbidZoom);
        mBtn_supportZoom = findViewById(R.id.btn_supportZoom);
        mBtn_setZoomControl = findViewById(R.id.btn_setZoomControl);
        mBtn_hideZoomButton = findViewById(R.id.btn_hideZoomButton);
        mBtn_showZoomButton = findViewById(R.id.btn_showZoomButton);
        mBtn_otherSettings = findViewById(R.id.btn_otherSettings);
        mBtn_networkLoad = findViewById(R.id.btn_networkLoad);
        mBtn_nativeLoad = findViewById(R.id.btn_nativeLoad);
        mBtn_outLineLoad = findViewById(R.id.btn_outLineLoad);
        mWv_web = findViewById(R.id.wv_web);
    }

    private void initEvent()
    {
        View.OnClickListener btnClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switch (view.getId())
                {
                    case R.id.btn_loadUrl:
                        loadUrl();
                        break;
                    case R.id.btn_loadAppHtmlFile:
                        loadAppHtmlFile();
                        break;
                    case R.id.btn_loadNativeHtmlFile:
                        loadNativeHtmlFile();
                        break;
                    case R.id.btn_clearCache:
                        clearCache();
                        break;
                    case R.id.btn_clearHistory:
                        clearHistory();
                        break;
                    case R.id.btn_clearFormData:
                        clearFormData();
                        break;
                    case R.id.btn_selfAdaptionSize:
                        selfAdaptionSize();
                        break;
                    case R.id.btn_forbidZoom:
                        forbidZoom();
                        break;
                    case R.id.btn_supportZoom:
                        supportZoom();
                        break;
                    case R.id.btn_setZoomControl:
                        setZoomControl();
                        break;
                    case R.id.btn_hideZoomButton:
                        hideZoomButton();
                        break;
                    case R.id.btn_showZoomButton:
                        showZoomButton();
                        break;
                    case R.id.btn_otherSettings:
                        otherSettings();
                        break;
                    case R.id.btn_networkLoad:
                        networkLoad();
                        break;
                    case R.id.btn_nativeLoad:
                        nativeLoad();
                        break;
                    case R.id.btn_outLineLoad:
                        outLineLoad();
                        break;
                    default:
                        break;
                }
            }
        };
        mBtn_loadUrl.setOnClickListener(btnClickListener);
        mBtn_loadAppHtmlFile.setOnClickListener(btnClickListener);
        mBtn_loadNativeHtmlFile.setOnClickListener(btnClickListener);
        mBtn_clearCache.setOnClickListener(btnClickListener);
        mBtn_clearHistory.setOnClickListener(btnClickListener);
        mBtn_clearFormData.setOnClickListener(btnClickListener);
        mBtn_selfAdaptionSize.setOnClickListener(btnClickListener);
        mBtn_forbidZoom.setOnClickListener(btnClickListener);
        mBtn_supportZoom.setOnClickListener(btnClickListener);
        mBtn_setZoomControl.setOnClickListener(btnClickListener);
        mBtn_hideZoomButton.setOnClickListener(btnClickListener);
        mBtn_showZoomButton.setOnClickListener(btnClickListener);
        mBtn_otherSettings.setOnClickListener(btnClickListener);
        mBtn_networkLoad.setOnClickListener(btnClickListener);
        mBtn_nativeLoad.setOnClickListener(btnClickListener);
        mBtn_outLineLoad.setOnClickListener(btnClickListener);
    }

    public void loadUrl()
    {
        //        mWv_web.loadUrl("http://www.213");
        mWv_web.loadUrl("http://www.hao123.com");
        //        mWv_web.loadData(htmlCode, null, null);
    }

    /**
     * 加载apk包中的html页面
     */
    public void loadAppHtmlFile()
    {
        mWv_web.loadUrl("file:///android_asset/c.html");
    }

    /**
     * 加载手机本地的html页面
     */
    public void loadNativeHtmlFile()
    {
        String url = "file:///" + Environment.getExternalStorageDirectory().getPath() + File.separator + "b.html";
        mWv_web.loadUrl(url);
    }

    public void clearCache()
    {
        //清理浏览器缓存
        mWv_web.clearCache(true);
    }

    /**
     * 清理历史访问记录,除了本次访问记录(导致返回上一页无效)
     */
    public void clearHistory()
    {
        mWv_web.clearHistory();
    }

    /**
     * 清理填写的表单数据
     */
    public void clearFormData()
    {
        mWv_web.clearFormData();
    }

    /**
     * 设置自适应尺寸大小
     */
    public void selfAdaptionSize()
    {
        //设置自适应屏幕，两者合用
        mWebSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        mWebSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
    }

    /**
     * 控制缩放
     */
    public void forbidZoom()
    {
        //缩放操作
        mWebSettings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        Lg.d("forbidZoom executed");
    }

    /**
     * 控制缩放
     */
    public void supportZoom()
    {
        //缩放操作
        mWebSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        Lg.d("supportZoom executed");
    }

    /**
     * 控制缩放控件
     */
    public void setZoomControl()
    {
        //缩放操作
        //        mWebSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        mWebSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        Lg.d("setZoomControl executed");
    }

    /**
     * 控制原声缩放控件
     */
    public void hideZoomButton()
    {
        mWebSettings.setDisplayZoomControls(false); //显示原生的缩放控件，如果缩放按钮已存在则无法隐藏
        Lg.d("hideZoomButton executed");
    }

    /**
     * 控制原声缩放控件
     */
    public void showZoomButton()
    {
        mWebSettings.setDisplayZoomControls(true); //显示原生的缩放控件，如果缩放按钮已存在则无法隐藏
        Lg.d("showZoomButton executed");
    }

    /**
     * 其它
     */
    private void otherSettings()
    {
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        mWebSettings.setJavaScriptEnabled(true);
        //支持插件 已过期
        //        mWebSettings.setPluginState(null);
        //其他细节操作
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        mWebSettings.setAllowFileAccess(true); //设置可以访问文件
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        mWebSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        mWebSettings.setAppCacheEnabled(true);//开启 Application Caches 功能
        mWebSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
        mWebSettings.setDatabaseEnabled(true);   //开启 database storage API 功能

        String cacheDirPath = getFilesDir().getAbsolutePath() + "myweb";
        mWebSettings.setAppCachePath(cacheDirPath); //设置  Application Caches 缓存目录
        mWebSettings.setAppCacheMaxSize(20 * 1024 * 1024);// 设置 缓存大小
        mWebSettings.setAppCacheEnabled(true); //开启缓存


        //取消Webview密码保存
        mWebSettings.setSavePassword(false);
        Lg.d("otherSettings executed");
    }

    /**
     * 网络加载设置
     */
    private void networkLoad()
    {
        Lg.d("networkLoad");
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据
    }

    /**
     * 离线加载设置
     */
    private void nativeLoad()
    {
        Lg.d("nativeLoad");
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);//从本地获取，即离线加载
    }

    /**
     * 离线加载设置
     */
    private void outLineLoad()
    {
        Lg.d("outLineLoad");
        if (isConnected(getApplicationContext()))
        {
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        }
        else
        {
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
        }


    }


    /**
     * back键控制返回
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWv_web.canGoBack())
        {
            mWv_web.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        boolean b = super.onKeyDown(keyCode, event);
        return b;
    }

    @Override
    protected void onDestroy()
    {
        /**
         * 释放Webview资源
         */
        if (mWv_web != null)
        {
            mWv_web.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWv_web.clearHistory();
            ((ViewGroup) mWv_web.getParent()).removeView(mWv_web);
            mWv_web.destroy();
            mWv_web = null;
        }
        super.onDestroy();

    }

    private boolean isConnected(Context pContext)
    {

        try
        {


            // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
            //
            ConnectivityManager connectivity = (ConnectivityManager) pContext.getSystemService(Context
                    .CONNECTIVITY_SERVICE);

            if (connectivity != null)
            {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnectedOrConnecting())
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            Lg.e(e);
        }

        return false;
    }
}
