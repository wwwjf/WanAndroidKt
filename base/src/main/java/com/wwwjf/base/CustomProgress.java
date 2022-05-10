package com.wwwjf.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;



public class CustomProgress extends Dialog {

    private Context mContext;

    public CustomProgress(Context context) {
        this(context, R.style.Custom_Progress);
        mContext = context;
    }

    public CustomProgress(Context context, int theme) {
        super(context, theme);
    }


    /**
     * 弹出自定义ProgressDialog
     *
     * @param message        ic_warn
     * @param cancelable     是否按返回键取消
     * @param cancelListener 按下返回键监听
     */
    public void show(CharSequence message, boolean cancelable, OnCancelListener cancelListener) {
        setTitle("");
        setContentView(R.layout.dialog_custom_progress);
        //设置取消的按钮和显示对话框
        setCancelableBtnAndShow(cancelable, cancelListener, 0f);
    }


    /**
     * 显示弹出对话框
     *
     * @param content           显示的内容
     * @param title             显示的标题
     * @param cancelBtnListener 取消按钮的监听器
     * @param ensureBtnListener 确认按钮的监听器
     * @param cancelable        是否可以触摸外部让对话框消失
     * @param cancelListener    触摸外部让对话框消失的监听器
     */
    public void show(String content,
                     String title,
                     View.OnClickListener cancelBtnListener,
                     View.OnClickListener ensureBtnListener,
                     boolean cancelable,
                     OnCancelListener cancelListener) {
        show(content, title,"取消","确定",
                cancelBtnListener, ensureBtnListener, cancelable, cancelListener);
    }

    public void show(String content,
                     View.OnClickListener cancelBtnListener,
                     boolean cancelable,
                     OnCancelListener cancelListener, final Handler handler) {
        setTitle("");
        setContentView(R.layout.custom_alert_dialog);
        TextView cancelBtn = findViewById(R.id.tv_custom_alert_dialog_cancel);
        TextView contentText = findViewById(R.id.tv_custom_alert_dialog_ensure);

        if (!TextUtils.isEmpty(content)) {
            contentText.setText(content);
        }

        setCustomClick(cancelBtnListener, null, cancelable, cancelListener, cancelBtn, null);

        if (handler != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                    handler.removeCallbacks(this);
                }
            }, 1000);
        }
    }


    /**
     * 显示弹出对话框
     *
     * @param content           显示的内容
     * @param title             显示的标题
     * @param cancelBtnListener 取消按钮的监听器
     * @param ensureBtnListener 确认按钮的监听器
     * @param cancelable        是否可以触摸外部让对话框消失
     * @param cancelListener    触摸外部让对话框消失的监听器
     */
    public void show(String content,
                     String title,
                     String cancelText, String ensureText,
                     View.OnClickListener cancelBtnListener,
                     View.OnClickListener ensureBtnListener,
                     boolean cancelable,
                     OnCancelListener cancelListener) {
        setTitle("");
        setContentView(R.layout.custom_alert_dialog);
        TextView cancelBtn = findViewById(R.id.tv_custom_alert_dialog_cancel);
        TextView ensureBtn = findViewById(R.id.tv_custom_alert_dialog_ensure);
        //设置确定和取消的按钮
        if (!TextUtils.isEmpty(cancelText)) {
            cancelBtn.setText(cancelText);
            cancelBtn.setVisibility(View.VISIBLE);
        } else {
            cancelBtn.setVisibility(View.GONE);
            View line = findViewById(R.id.tv_custom_alert_dialog_line);
            line.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(ensureText)) {
            ensureBtn.setText(ensureText);
        }
        ensureBtn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        TextView tvContent = findViewById(R.id.ll_custom_alert_dialog_content);
        TextView tvTitle = findViewById(R.id.tv_custom_alert_dialog_title);

        //设置title
        setCustomTitle(title, tvTitle);

        //添加内容的显示
        if (TextUtils.isEmpty(content)) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setText(content);
        }

        //设置点击事件
        setCustomClick(cancelBtnListener, ensureBtnListener, cancelable, cancelListener, cancelBtn, ensureBtn);
    }

    /**
     * 显示弹出对话框
     *
     * @param content           显示的内容
     * @param title             显示的标题
     * @param cancelBtnListener 取消按钮的监听器
     * @param ensureBtnListener 确认按钮的监听器
     * @param cancelable        是否可以触摸外部让对话框消失
     * @param cancelListener    触摸外部让对话框消失的监听器
     */
    public void show(SpannableStringBuilder content,
                     String title,
                     String cancelText, String ensureText,
                     View.OnClickListener cancelBtnListener,
                     View.OnClickListener ensureBtnListener,
                     boolean cancelable,
                     OnCancelListener cancelListener) {
        setTitle("");
        setContentView(R.layout.custom_alert_dialog);
        TextView cancelBtn = findViewById(R.id.tv_custom_alert_dialog_cancel);
        TextView ensureBtn = findViewById(R.id.tv_custom_alert_dialog_ensure);
        //设置确定和取消的按钮
        if (!TextUtils.isEmpty(cancelText)) {
            cancelBtn.setText(cancelText);
            cancelBtn.setVisibility(View.VISIBLE);
        } else {
            cancelBtn.setVisibility(View.GONE);
            View line = findViewById(R.id.tv_custom_alert_dialog_line);
            line.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(ensureText)) {
            ensureBtn.setText(ensureText);
        }
        ensureBtn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        TextView tvContent = findViewById(R.id.ll_custom_alert_dialog_content);
        TextView tvTitle = findViewById(R.id.tv_custom_alert_dialog_title);

        //设置title
        setCustomTitle(title, tvTitle);

        //添加内容的显示
        if (content.length() > 0) {
            tvContent.setGravity(Gravity.START);
            tvContent.setText(content);
            tvContent.setMovementMethod(LinkMovementMethod.getInstance());  //很重要，点击无效就是由于没有设置这个引起
        } else {
            tvContent.setVisibility(View.GONE);
        }

        //设置点击事件
        setCustomClick(cancelBtnListener, ensureBtnListener, cancelable, cancelListener, cancelBtn, ensureBtn);
    }

    /**
     * 显示弹出对话框
     *
     * @param content           显示的内容
     * @param title             显示的标题
     * @param cancelBtnListener 取消按钮的监听器
     * @param ensureBtnListener 确认按钮的监听器
     * @param cancelable        是否可以触摸外部让对话框消失
     * @param cancelListener    触摸外部让对话框消失的监听器
     */
    public void showLeftEnsure(String content,
                               String title,
                               String cancelText, String ensureText,
                               View.OnClickListener cancelBtnListener,
                               View.OnClickListener ensureBtnListener,
                               boolean cancelable,
                               OnCancelListener cancelListener) {
        setTitle("");
        setContentView(R.layout.custom_alert_dialog);
        TextView cancelBtn = findViewById(R.id.tv_custom_alert_dialog_cancel);
        TextView ensureBtn = findViewById(R.id.tv_custom_alert_dialog_ensure);
        //设置确定和取消的按钮
        if (!TextUtils.isEmpty(cancelText)) {
            cancelBtn.setText(cancelText);
        } else {
            cancelBtn.setText("确定");
            cancelBtn.setTextColor(mContext.getResources().getColor(R.color.black));
        }

        if (!TextUtils.isEmpty(ensureText)) {
            ensureBtn.setText(ensureText);
        } else {
            ensureBtn.setText("取消");
            ensureBtn.setTextColor(mContext.getResources().getColor(R.color.black_30));
        }
        cancelBtn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        TextView tvContent = findViewById(R.id.ll_custom_alert_dialog_content);
        TextView tvTitle = findViewById(R.id.tv_custom_alert_dialog_title);

        //设置title
        setCustomTitle(title, tvTitle);

        //添加内容的显示
        if (TextUtils.isEmpty(content)) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setText(content);
        }

        //设置点击事件
        setCustomClick(ensureBtnListener, cancelBtnListener, cancelable, cancelListener, cancelBtn, ensureBtn);
    }

    /**
     * 设置定义的点击事件
     *
     * @param cancelBtnListener 取消的监听
     * @param ensureBtnListener 确认的监听
     * @param cancelable        是否可以点击外部取消
     * @param cancelListener    点击外部取消的监听
     * @param cancelBtn         取消的控件
     * @param ensureBtn         确定的控件
     */
    private void setCustomClick(View.OnClickListener cancelBtnListener, View.OnClickListener ensureBtnListener, boolean cancelable, OnCancelListener cancelListener, View cancelBtn, View ensureBtn) {
        //设置点击事件
        if (cancelBtnListener != null) {
            cancelBtn.setOnClickListener(cancelBtnListener);
        }

        if (ensureBtnListener != null) {
            ensureBtn.setOnClickListener(ensureBtnListener);
        }
        // 按返回键是否取消
        setCancelableBtnAndShow(cancelable, cancelListener);
    }


    /**
     * 设置title
     *
     * @param title   文本内容
     * @param tvTitle 需要设置的textView
     */
    private void setCustomTitle(String title, TextView tvTitle) {
        //设置title
        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            if (!tvTitle.isShown()) {
                tvTitle.setVisibility(View.VISIBLE);
            }
            tvTitle.setText(title);
            tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
    }

    /**
     * 设置只有确定按钮的提示
     *
     * @param content           内容
     * @param title             标题
     * @param ensureBtnListener 确定按钮
     * @param cancelable        是否可以点击外部消失
     * @param cancelListener    消失的监听
     */
    public void show(String content,
                     String title, String ensureText,
                     View.OnClickListener ensureBtnListener,
                     boolean cancelable,
                     OnCancelListener cancelListener) {
        setTitle("");
        setContentView(R.layout.custom_alert_dialog_sure);

        TextView ensureBtn = findViewById(R.id.tv_custom_alert_dialog_ensure_ensure);
        //设置确定和取消的按钮
        TextView tvContent = findViewById(R.id.ll_custom_alert_dialog_ensure_content);
        TextView tvTitle = findViewById(R.id.tv_custom_alert_dialog_ensure_title);

        //设置title
        setCustomTitle(title, tvTitle);

        //添加内容的显示
        tvContent.setText(content);

        if (!TextUtils.isEmpty(ensureText)) {
            ensureBtn.setText(ensureText);
        }

        ensureBtn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        if (ensureBtnListener != null) {
            ensureBtn.setOnClickListener(ensureBtnListener);
        }
        // 按返回键是否取消
        setCancelableBtnAndShow(cancelable, cancelListener);
    }

    /**
     * 设置只有确定按钮的提示
     *
     * @param content           内容
     * @param isContentLeft     居左显示
     * @param title             标题
     * @param ensureBtnListener 确定按钮
     * @param cancelable        是否可以点击外部消失
     * @param cancelListener    消失的监听
     */
    public void show(String content, boolean isContentLeft,
                     String title, String ensureText,
                     View.OnClickListener ensureBtnListener,
                     boolean cancelable,
                     OnCancelListener cancelListener) {
        setTitle("");
        setContentView(R.layout.custom_alert_dialog_sure);

        //设置确定的按钮
        TextView ensureBtn = findViewById(R.id.tv_custom_alert_dialog_ensure_ensure);
        TextView tvTitle = findViewById(R.id.tv_custom_alert_dialog_ensure_title);
        TextView tvContent = findViewById(R.id.ll_custom_alert_dialog_ensure_content);

        if (isContentLeft) {
            tvContent.setGravity(Gravity.START);
        }
        //设置title
        setCustomTitle(title, tvTitle);

        //添加内容的显示
        tvContent.setText(content);

        if (!TextUtils.isEmpty(ensureText)) {
            ensureBtn.setText(ensureText);
        }

        ensureBtn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        if (ensureBtnListener != null) {
            ensureBtn.setOnClickListener(ensureBtnListener);
        }
        // 按返回键是否取消
        setCancelableBtnAndShow(cancelable, cancelListener);
    }


    /**
     * 设置只有确定按钮的提示
     *
     * @param content           内容
     * @param title             标题
     * @param ensureBtnListener 确定按钮
     * @param cancelable        是否可以点击外部消失
     * @param cancelListener    消失的监听
     */
    public void show(String content,
                     String title,
                     View.OnClickListener ensureBtnListener,
                     boolean cancelable,
                     OnCancelListener cancelListener) {
        show(content, title, "", ensureBtnListener, cancelable, cancelListener);
    }


    /**
     * 设置取消按钮的事件和显示对话框
     *
     * @param cancelable     是否可以点击返回按键
     * @param cancelListener 返回按钮的监听器
     */
    private void setCancelableBtnAndShow(boolean cancelable, OnCancelListener cancelListener) {
        setCancelableBtnAndShow(cancelable, cancelListener, 0.5f);
    }

    /**
     * 设置取消按钮的事件和显示对话框
     *
     * @param cancelable     是否可以点击返回按键
     * @param cancelListener 返回按钮的监听器
     */
    private void setCancelableBtnAndShow(boolean cancelable, OnCancelListener cancelListener, float alpha) {
        // 按返回键是否取消
        setCancelable(cancelable);
        // 监听返回键处理
        setOnCancelListener(cancelListener);
        // 设置居中
        if (getWindow() != null) {
            getWindow().getAttributes().gravity = Gravity.CENTER;
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            // 设置背景层透明度
            lp.dimAmount = alpha;
            getWindow().setAttributes(lp);
        }
        show();
    }
}
