package com.ly.testwebview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Set;

public class TestJsActivity extends Activity
{
    private Context mContext;
    private FrameLayout mLayout_webview;
    private WebView mWv_webview;
    private Button mBtn_loadAndroidToJs;
    private Button mBtn_loadJsToAndroid;
    private Button mBtn_androidCallJsByLoadUrl;
    private Button mBtn_androidCallJsByEvaluateJavascript;
    private Button mBtn_jsCallAndroidByaddJavascriptInterface;
    private WebSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.mContext = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_js);
        initViews();
        initEvent();
        init();
    }

    private void initViews()
    {
        mLayout_webview = findViewById(R.id.layout_webview_cache);
        mWv_webview = new WebView(mContext);
        mWv_webview.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT));
        mLayout_webview.addView(mWv_webview);
        mBtn_loadAndroidToJs = findViewById(R.id.btn_load_android_to_js);
        mBtn_loadJsToAndroid = findViewById(R.id.btn_load_js_to_android);
        mBtn_androidCallJsByLoadUrl = findViewById(R.id.btn_androidCallJs_loadUrl);
        mBtn_androidCallJsByEvaluateJavascript = findViewById(R.id.btn_androidCallJs_evaluateJavascript);
        mBtn_jsCallAndroidByaddJavascriptInterface = findViewById(R.id.btn_jsCallAndroid_addJavascriptInterface);
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
                        // 先载入JS代码
                        // 格式规定为:file:///android_asset/文件名.html
                        mWv_webview.loadUrl("file:///android_asset/js/androidToJs.html");
                        break;
                    case R.id.btn_load_js_to_android:
                        mWv_webview.loadUrl("file:///android_asset/js/jsToAndroid.html");
                        break;
                    case R.id.btn_androidCallJs_loadUrl:
                        androidCallJsByLoadUrl();
                        break;
                    case R.id.btn_androidCallJs_evaluateJavascript:
                        androidCallJsByEvaluateJavascript();
                        break;
                    case R.id.btn_jsCallAndroid_addJavascriptInterface:
                        jsCallAndroid_addJavascriptInterface();
                        break;
                    default:
                        break;
                }
            }
        };
        mBtn_loadAndroidToJs.setOnClickListener(btnClickListener);
        mBtn_loadJsToAndroid.setOnClickListener(btnClickListener);
        mBtn_androidCallJsByLoadUrl.setOnClickListener(btnClickListener);
        mBtn_androidCallJsByEvaluateJavascript.setOnClickListener(btnClickListener);
        mBtn_jsCallAndroidByaddJavascriptInterface.setOnClickListener(btnClickListener);
    }

    private void init()
    {
        settings = mWv_webview.getSettings();
        mWv_webview.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if (shouldOverrideUrlLoadingRealizeJsCallAndroid(url))
                {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
            {
                Lg.d("shouldOverrideUrlLoading(WebView view, WebResourceRequest request)");
                return super.shouldOverrideUrlLoading(view, request);
            }

            /**
             * js代码必须在此方法回调后才能调用
             * @param view
             * @param url
             */
            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
            }
        });
        //设置与js交互的权限
        settings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 由于设置了弹窗检验调用结果,所以需要支持js对话框
        // webview只是载体，内容的渲染需要使用webviewChromClient类去实现
        // 通过设置WebChromeClient对象处理JavaScript的对话框
        //设置响应js 的Alert()函数
        mWv_webview.setWebChromeClient(new WebChromeClient()
        {
            // 拦截JS的警告框
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result)
            {
                Lg.d(message);

                                               /* AlertDialog.Builder b = new AlertDialog.Builder(TestJsActivity.this);
                                                b.setTitle("Alert");
                                                b.setMessage(message);
                                                b.setPositiveButton(android.R.string.ok, new DialogInterface
                                                .OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which)
                                                    {
                                                        result.confirm();
                                                    }
                                                });
                                                b.setCancelable(false);
                                                b.create().show();*/
                return super.onJsAlert(view, url, message, result);
            }

            // 拦截输入框(原理同方式2)
            // 参数message:代表promt（）的内容（不是url）
            // 参数result:代表输入框的返回值
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult
                    result)
            {
                boolean b = TestJsActivity.this.onJsPrompt(message, result);
                if (b)
                {
                    return true;
                }
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            // 通过alert()和confirm()拦截的原理相同，此处不作过多讲述
            // 拦截JS的确认框
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result)
            {
                return super.onJsConfirm(view, url, message, result);
            }

        });
    }

    /**
     * 通过WebView的loadUrl（）
     * 通过WebView的evaluateJavascript（）
     */
    private void androidCallJsByLoadUrl()
    {
        mWv_webview.post(new Runnable()
        {
            @Override
            public void run()
            {
                // 注意调用的JS方法名要对应上
                // 调用javascript的callJS()方法
                //此方式会刷新界面
                mWv_webview.loadUrl("javascript:callJS()");
            }
        });

    }

    /**
     * android4.4使用
     * 建议版本兼容混合使用
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void androidCallJsByEvaluateJavascript()
    {
        // 只需要将第一种方法的loadUrl()换成下面该方法即可
        mWv_webview.evaluateJavascript("javascript:callJS()", new ValueCallback<String>()
        {
            @Override
            public void onReceiveValue(String value)
            {
                //此处为 js 返回的结果
                Lg.d("androidCallJsByEvaluateJavascript：" + value);
            }
        });
    }

    /**
     * 方案1  4.2一下存在漏洞
     * 通过WebView的addJavascriptInterface（）进行对象映射
     * 通过 WebViewClient 的shouldOverrideUrlLoading ()方法回调拦截 url
     * 通过 WebChromeClient 的onJsAlert()、onJsConfirm()、onJsPrompt（）方法回调拦截JS对话框alert()、confirm()、prompt（） 消息
     * 必须先设置js的映射再去加载界面才能成功使用
     * 特点：使用简单
     */
    private void jsCallAndroid_addJavascriptInterface()
    {
        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        mWv_webview.addJavascriptInterface(new AndroidToJs(), "test");//AndroidtoJS类对象映射到js的test对象
    }

    /**
     * 方案2  需要进行协议约束
     * 通过拦截地址,匹配协议来调用android代码
     * 缺点：JS获取Android方法的返回值复杂
     * mWebView.loadUrl("javascript:returnResult(" + result + ")");
     * function returnResult(result){
     * alert("result is" + result);
     * }
     *
     * @param url
     * @return
     */
    private boolean shouldOverrideUrlLoadingRealizeJsCallAndroid(String url)
    {
        // 步骤2：根据协议的参数，判断是否是所需要的url
        // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
        //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）

        Uri uri = Uri.parse(url);
        // 如果url的协议 = 预先约定的 js 协议
        // 就解析往下解析参数
        if (uri.getScheme().equals("js"))
        {

            // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
            // 所以拦截url,下面JS开始调用Android需要的方法
            if (uri.getAuthority().equals("webview"))
            {

                //  步骤3：
                // 执行JS所需要调用的逻辑
                Lg.d("js调用了Android的方法");
                // 可以在协议上带有参数并传递到Android上
                HashMap<String, String> params = new HashMap<>();
                Set<String> collection = uri.getQueryParameterNames();

            }

            return true;
        }
        return false;
    }

    /**
     * 方案3  功能全面
     * 拦截输入框(原理同方式2)
     * 参数message:代表promt（）的内容（不是url）
     * 参数result:代表输入框的返回值
     *
     * @param message
     * @param result
     * @return
     */
    public boolean onJsPrompt(String message, JsPromptResult result)
    {
        // 根据协议的参数，判断是否是所需要的url(原理同方式2)
        // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
        //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）

        Uri uri = Uri.parse(message);
        // 如果url的协议 = 预先约定的 js 协议
        // 就解析往下解析参数
        if (uri.getScheme().equals("js"))
        {

            // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
            // 所以拦截url,下面JS开始调用Android需要的方法
            if (uri.getAuthority().equals("webview"))
            {

                //
                // 执行JS所需要调用的逻辑
                Lg.d("js调用了Android的方法");
                // 可以在协议上带有参数并传递到Android上
                HashMap<String, String> params = new HashMap<>();
                Set<String> collection = uri.getQueryParameterNames();

                //参数result:代表消息框的返回值(输入值)
                result.confirm("js调用了Android的方法成功啦");
            }
            return true;
        }
        return false;
    }
}
