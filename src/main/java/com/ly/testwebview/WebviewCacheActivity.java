package com.ly.testwebview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class WebviewCacheActivity extends Activity
{

    private static final String TAG = "fuck";
    private Context mContext;
    private FrameLayout mLayout_webview;
    private WebView mWv_web;

    private Button mBtn_load;
    private Button mBtn_webviewCache;
    private Button mBtn_applicationCache;
    private Button mBtn_dom_storage;
    private Button mBtn_indexedDB;
    private Button mBtn_setCachepattern;
    private Button mBtn_customCache;

    private WebSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.mContext = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_cache);
        initViews();
        initEvent();
        init();
    }

    private void initViews()
    {
        mLayout_webview = findViewById(R.id.layout_webview_cache);
        mWv_web = new WebView(mContext);
        mWv_web.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT));
        mLayout_webview.addView(mWv_web);

        mBtn_load = findViewById(R.id.btn_load_android_to_js);
        mBtn_webviewCache = findViewById(R.id.btn_webview_cache);
        mBtn_applicationCache = findViewById(R.id.btn_application_cache);
        mBtn_dom_storage = findViewById(R.id.btn_dom_storage);
        mBtn_indexedDB = findViewById(R.id.btn_indexedDB);
        mBtn_setCachepattern = findViewById(R.id.btn_setCachePattern);
        mBtn_customCache = findViewById(R.id.btn_customCache);
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
                    case R.id.btn_load_android_to_js:
                        load();
                        break;
                    case R.id.btn_webview_cache:
                        webviewCache();
                        break;
                    case R.id.btn_application_cache:
                        applicationCache();
                        break;
                    case R.id.btn_dom_storage:
                        domStorageCache();
                        break;
                    case R.id.btn_indexedDB:
                        indexedDbCache();
                        break;
                    case R.id.btn_setCachePattern:
                        setCachePattern();
                        break;
                    case R.id.btn_customCache:
                        customCache();
                        break;
                    default:
                        break;
                }
            }
        };
        mBtn_load.setOnClickListener(btnClickListener);
        mBtn_webviewCache.setOnClickListener(btnClickListener);
        mBtn_applicationCache.setOnClickListener(btnClickListener);
        mBtn_dom_storage.setOnClickListener(btnClickListener);
        mBtn_indexedDB.setOnClickListener(btnClickListener);
        mBtn_setCachepattern.setOnClickListener(btnClickListener);
        mBtn_customCache.setOnClickListener(btnClickListener);
    }

    private void init()
    {
        WebViewClient webviewClient = new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url)
            {
                WebResourceResponse response = disposeNetworkReuqestInterceptByApi21D(url);
                if (response != null)
                {
                    return response;
                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request)
            {
                WebResourceResponse response = disposeNetworkReuqestInterceptByApi21T(request);
                if (response != null)
                {
                    return response;
                }

                return super.shouldInterceptRequest(view, request);
            }
        };
        mWv_web.setWebViewClient(webviewClient);
        settings = mWv_web.getSettings();
    }

    public void load()
    {
        //        mWv_web.loadUrl("http://hao123.com");
        mWv_web.loadUrl("http://192.168.1.103:8080/Test/test.html");
    }

    /**
     * 根据 HTTP 协议头里的 Cache-Control（或 Expires）和 Last-Modified（或 Etag）等字段来控制文件缓存的机制
     * Cache-Control：用于控制文件在本地缓存有效时长
     * Expires：与Cache-Control功能相同，即控制缓存的有效时间
     * Last-Modified：标识文件在服务器上的最新更新时间
     * Etag：功能同Last-Modified ，即标识文件在服务器上的最新更新时间。
     * 应用场景：静态资源文件的存储，如` JS、CSS`、字体、图片等。
     */
    private void webviewCache()
    {
        //Webview自行实现,无需特殊处理
    }

    /**
     * Application缓存，对Webview自身缓存的一个补充而不是替代
     * 应用场景：存储静态文件（如JS、CSS、字体文件）
     */
    private void applicationCache()
    {
        String cacheDirPath = mContext.getFilesDir().getAbsolutePath() + File.separator + "cache/";
        settings.setAppCachePath(cacheDirPath); // 1. 设置缓存路径
        settings.setAppCacheMaxSize(20 * 1024 * 1024);// 2. 设置缓存大小
        settings.setAppCacheEnabled(true); // 3. 开启Application Cache存储机制
    }

    /**
     * 特点：存储安全、便捷： Dom Storage 存储的数据在本地，不需要经常和服务器进行交互
     * 应用场景：存储临时、简单的数据
     */
    private void domStorageCache()
    {
        settings.setDomStorageEnabled(true);// 开启DOM storage
    }

    /**
     * 应用场景：存储 复杂、数据量大的结构化数据
     */
    private void indexedDbCache()
    {
        // 只需设置支持JS就自动打开IndexedDB存储机制
        // Android 在4.4开始加入对 IndexedDB 的支持，只需打开允许 JS 执行的开关就好了。
        settings.setJavaScriptEnabled(true);
    }

    /**
     * 设置缓存模式
     * 缓存模式说明:
     * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
     * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
     * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
     * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
     */
    private void setCachePattern()
    {
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    /**
     * 自定义缓存操作
     * 背景：H5页面有一些更新频率低、常用 & 固定的静态资源文件（如JS、CSS文件、图片等）
     * 使用场景：每次重新加载会浪费很多资源（时间 & 流量）
     * 原理：通过拦截`H5`页面的资源网络请求 从而 直接从本地读取资源 而不需要发送网络请求到服务器读取。
     * 实现步骤：
     * 1、事先将更新频率较低、常用 & 固定的H5静态资源 文件（如JS、CSS文件、图片等） 放到本地
     * 2、拦截H5页面的资源网络请求 并进行检测
     * 3、如果检测到本地具有相同的静态资源 就 直接从本地读取进行替换 而 不发送该资源的网络请求 到 服务器获取
     */
    private void customCache()
    {

    }

    /**
     * 处理资源拦截
     *
     * @param url
     * @return
     */
    private WebResourceResponse disposeNetworkReuqestInterceptByApi21D(String url)
    {
        // 步骤1:判断拦截资源的条件，即判断url里的图片资源的文件名
        if (url.contains("test.png"))
        {
            // 假设网页里该图片资源的地址为：http://abc.com/imgage/logo.gif
            // 图片的资源文件名为:logo.gif

            InputStream is = null;
            // 步骤2:创建一个输入流

            try
            {
                is = getApplicationContext().getAssets().open("images/szbf.jpg");
                // 步骤3:获得需要替换的资源(存放在assets文件夹里)
                // a. 先在app/src/main下创建一个assets文件夹
                // b. 在assets文件夹里再创建一个images文件夹
                // c. 在images文件夹放上需要替换的资源（此处替换的是abc.png图片）

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            // 步骤4:替换资源
            WebResourceResponse response = new WebResourceResponse("image/jpg", "utf-8", is);
            // 参数1：http请求里该图片的Content-Type,此处图片为image/png
            // 参数2：编码类型
            // 参数3：存放着替换资源的输入流（上面创建的那个）
            return response;
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private WebResourceResponse disposeNetworkReuqestInterceptByApi21T(WebResourceRequest request)
    {
        return disposeNetworkReuqestInterceptByApi21D(request.getUrl().toString());
    }

}
