package com.xianghe.ivy.ui.module.pic2video;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.xianghe.ivy.R;

public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading_luwei);
        setCanceledOnTouchOutside(false);   // 即可实现点击外部不消失
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
