package com.xianghe.ivy.mvp;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xianghe.ivy.R;
import com.xianghe.ivy.mvp.loadPager.BaseMVPCustomLoadingActivity;
import com.xianghe.ivy.mvp.loadPager.BaseMVPLoadingActivity;
import com.xianghe.ivy.weight.CustomProgress;
import com.xianghe.ivy.weight.loading.VaryViewHelperController;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseWebViewActivity<V extends IBaseConctact.IBaseView,K extends BasePresenter<V>> extends BaseMVPCustomLoadingActivity<V,K> {

    @BindView(R.id.web_view)
    public WebView mWebView;

    protected String mUrl;

    protected CustomProgress mCustomProgress;

    private Unbinder mBind;

    protected boolean hasNetError;

    protected VaryViewHelperController mVaryViewHelperController;

    private boolean mIsUrl;

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        dismissLoading();

        mBind = ButterKnife.bind(this);

        mCustomProgress = new CustomProgress(this);

        mVaryViewHelperController = new VaryViewHelperController(mWebView);

        mVaryViewHelperController.showLoading();

        initWebView();

        mIsUrl = getIntent().getBooleanExtra("isUrl", true);

        mUrl = getIntent().getStringExtra("url");

        if (null != savedInstanceState) {
            mWebView.restoreState(savedInstanceState);
        } else {
            initLoad();
        }

        initSubData();
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);//启用js
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//js和android交互
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.getSettings().setAllowFileAccess(true);// 允许访问文件
        mWebView.getSettings().setSupportZoom(false);//关闭zoom按钮
        mWebView.getSettings().setBuiltInZoomControls(false);//关闭zoom
        mWebView.getSettings().setBlockNetworkImage(true);
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mWebView.getSettings().setLoadsImagesAutomatically(false);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(0);
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < 19) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        WebSettings settings = mWebView.getSettings();
        Class<?> clazz = settings.getClass();
        try {
            clazz.getMethod("setPluginsEnabled", boolean.class).invoke(
                    settings, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mWebView.addJavascriptInterface(getJsHistory(), "grapefs");

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https://")) {
                    view.loadUrl(url);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mWebView != null) {
                    if (!mWebView.getSettings().getLoadsImagesAutomatically()) {
                        mWebView.getSettings().setLoadsImagesAutomatically(true);
                    }
                    if (mWebView.getSettings().getBlockNetworkImage())
                        mWebView.getSettings().setBlockNetworkImage(false);
                }

                if (!hasNetError){
                    restoreView();

                    onPageFinish();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                hasNetError = true;
                mVaryViewHelperController.showNetworkError(v -> {
                    hasNetError = false;
                    mVaryViewHelperController.showLoading();
                    initLoad();
                });
            }
        });
    }

    protected void restoreView(){
        mVaryViewHelperController.restore();
    }

    protected void showDialog(String text) {
        if (mCustomProgress != null && !mCustomProgress.isShowing()) {
            mCustomProgress.show(text, false, null);
        }
    }

    protected void dismissDialog() {
        if (mCustomProgress != null && mCustomProgress.isShowing()) {
            mCustomProgress.dismiss();
        }
    }

    protected void doJsProgress(String text,String fun,String ... params){
        if (!TextUtils.isEmpty(text)){
            showDialog(text);
        }
        executeJs(fun,params);
    }

    /**
     * 网页加载成功回调
     */
    protected abstract void onPageFinish();

    protected abstract void initSubData();

    protected abstract Object getJsHistory();

    private void initLoad() {
        hasNetError = false;
        Map<String, String> noCacheHeaders = new HashMap<>(2);
        noCacheHeaders.put("Pragma", "no-cache");
        noCacheHeaders.put("Cache-Control", "no-cache");
        if (mIsUrl) {
            mWebView.loadUrl(mUrl, noCacheHeaders);
        } else {
            mWebView.loadData(mUrl, "text/html;charset=utf-8", "utf-8");
        }
    }

    protected void executeJs(String fun, String... params) {
        if (mWebView != null) {
            String action;
            if (params == null || params.length == 0) {
                action = "javascript:" + fun + "()";
            } else {
                StringBuilder allParams = new StringBuilder();
                for (int i = 0;i<params.length;i++){
                    allParams.append(params[i]).append(",");
                }
                allParams.deleteCharAt(allParams.length()-1);
                action = "javascript:" + fun + "('" + allParams + "')";
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mWebView.evaluateJavascript(action, null);
            } else {
                mWebView.loadUrl(action);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mBind != null) {
            mBind.unbind();
        }

        if (null != mWebView) {
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.destroy();
            mWebView = null;
        }

        if (mCustomProgress != null && mCustomProgress.isShowing()) {
            mCustomProgress.dismiss();
        }
        super.onDestroy();
    }
}
