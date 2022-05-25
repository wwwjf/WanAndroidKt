package com.xianghe.ivy.ui.module.videopush.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xianghe.ivy.R;

/**
 * @Author: ycl
 * @Date: 2018/10/25 14:11
 * @Desc:
 */
public class VideoPushInputDialog extends Dialog {

    private onItemClickListener mClickListener;
    private Activity mActivity;
    private String mTitle;
    private String mContent;

    private TextView mTvCustomAlertDialogTitle;
    private EditText mLlCustomAlertDialogContent;
    private TextView mTvCustomAlertDialogCancel;
    private TextView mTvCustomAlertDialogEnsure;


    public VideoPushInputDialog(@NonNull Activity activity, String title, String content, onItemClickListener clickListener) {
        super(activity, R.style.DialogStyleBottomTranslut);
        this.mActivity = activity;
        this.mClickListener = clickListener;
        this.mTitle = title;
        this.mContent = content;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_vp_eidt);
        initView();
        initListener();
    }

    private void initListener() {
        if (mTitle == null || TextUtils.isEmpty(mTitle)) {
            mTvCustomAlertDialogTitle.setVisibility(View.GONE);
        } else {
            mTvCustomAlertDialogTitle.setText(mTitle);
        }
        if (mContent == null || TextUtils.isEmpty(mContent)) {
//            mLlCustomAlertDialogContent.setVisibility(View.GONE);
        } else {
            mLlCustomAlertDialogContent.setText(mContent);
            mLlCustomAlertDialogContent.setSelection(mLlCustomAlertDialogContent.getText().length());
        }
        mTvCustomAlertDialogCancel.setOnClickListener(v -> {
            if (VideoPushInputDialog.this.isShowing()) {
                VideoPushInputDialog.this.dismiss();
            }
        });
        mTvCustomAlertDialogEnsure.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.click(mLlCustomAlertDialogContent.getText().toString());
            }
        });
        if (mLlCustomAlertDialogContent != null) {
            mLlCustomAlertDialogContent.post(new Runnable() {
                @Override
                public void run() {
                    showSoftInputView();
                }
            });
        }
    }


    private void initView() {
        mTvCustomAlertDialogTitle = (TextView) findViewById(R.id.tv_custom_alert_dialog_title);
        mLlCustomAlertDialogContent = (EditText) findViewById(R.id.ll_custom_alert_dialog_content);
        mTvCustomAlertDialogCancel = (TextView) findViewById(R.id.tv_custom_alert_dialog_cancel);
        mTvCustomAlertDialogEnsure = (TextView) findViewById(R.id.tv_custom_alert_dialog_ensure);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().getAttributes().gravity = Gravity.CENTER;
    }

    private void showSoftInputView() {
        if (mActivity != null) {
            InputMethodManager manager = ((InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE));
            if (mActivity.getCurrentFocus() != null)
                manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public interface onItemClickListener {
        public void click(String content);
    }
}

