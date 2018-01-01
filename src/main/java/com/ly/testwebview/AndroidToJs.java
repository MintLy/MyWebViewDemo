package com.ly.testwebview;

import android.webkit.JavascriptInterface;

/**
 * Created by Mint on 2018/1/1.
 */

public class AndroidToJs extends Object
{
    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void hello(String msg)
    {
        Lg.d("JS调用了Android的hello方法");
    }
}
