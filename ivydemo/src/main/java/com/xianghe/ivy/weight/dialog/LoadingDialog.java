package com.xianghe.ivy.weight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.xianghe.ivy.R;

/**
 * @Author: ycl
 * @Date: 2018/11/6 11:08
 * @Desc:
 */
public class LoadingDialog extends Dialog {
    private onBackCallBackListener mBackListener;

    public LoadingDialog(Context context) {
        super(context, R.style.DialogStyleScaleTranslut);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);

        ImageView iv = (ImageView) findViewById(R.id.iv);
        Glide.with(getContext()).asGif().load(R.drawable.gif_loading_dialog).into(iv);


        // 设置window偏右
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().getAttributes().gravity = Gravity.CENTER;
    }


    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBackListener != null) {
                mBackListener.onBackCallBack();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void setBackListener(onBackCallBackListener backListener) {
        mBackListener = backListener;
    }

    public interface onBackCallBackListener {
        void onBackCallBack();
    }
}
