package com.xianghe.ivy.ui.module.player.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.xianghe.ivy.R;

import java.io.Serializable;

/**
 * 举报结果对话框, 目前dialog的外部点击设置是不生效的（都会出现dismiss的效果）.
 * 因为为了实现效果, 用一个透明的view 填充了整个window, 所以不存在外部区域, 如果想控制外部点击不消失, 请自行修改相关代码.
 */
public class TipResultDialog extends DialogFragment implements View.OnClickListener {
    private static final String KEY_TEXT = "key_text";
    private static final String KEY_RESULT = "key_result";

    public enum Result implements Serializable {
        SUCCESS,
        FAILED,
        INFO,
    }

    private TextView mTvText;
    private ImageView mIvImage;

    private CharSequence mText;
    private Result mResult;

    /**
     * 正确打开姿势, 从这里进入, 默认显示成功图标.
     *
     * @param text text
     * @return TipResultDialog
     */
    public static TipResultDialog create(@Nullable String text) {
        return create(text, Result.SUCCESS);
    }

    /**
     * 正确打开姿势, 从这里进入.
     *
     * @param text   text
     * @param result {@link Result#SUCCESS} 或  {@link Result#FAILED}, 该标志只影响右侧图片样式，用enum做有点蠢了.
     * @return TipResultDialog
     */
    @SuppressWarnings("ConstantConditions")
    public static TipResultDialog create(@Nullable String text, @NonNull Result result) {
        if (result == null) {
            throw new RuntimeException("result can not null");
        }

        Bundle bundle = new Bundle();
        bundle.putString(KEY_TEXT, text);
        bundle.putSerializable(KEY_RESULT, result);

        TipResultDialog dialog = new TipResultDialog();
        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments == null) {
            throw new RuntimeException("you should call TipResultDialog.create() to create dialog");
        }
        mText = arguments.getString(KEY_TEXT);
        mResult = (Result) arguments.getSerializable(KEY_RESULT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_report_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view, savedInstanceState);
        initView(view, savedInstanceState);
        initListener(view, savedInstanceState);
    }

    private void findView(View rootView, Bundle savedInstanceState) {
        mTvText = rootView.findViewById(R.id.tv_text);
        mIvImage = rootView.findViewById(R.id.iv_image);
    }

    private void initView(View rootView, Bundle savedInstanceState) {
        setText(mText);
        setResultImg(mResult);
    }

    private void initListener(View rootView, Bundle savedInstanceState) {
        rootView.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(params);
    }

    public TipResultDialog setText(CharSequence text) {
        mText = text;
        mTvText.setText(text);
        return this;
    }

    private void setResultImg(Result result) {
        switch (result) {
            case FAILED:
                mIvImage.setImageResource(R.mipmap.pic_paisheshibai);
                break;
            case SUCCESS:
                mIvImage.setImageResource(R.mipmap.pic_jubaochenggong);
                break;
            default:
                mIvImage.setImageResource(R.drawable.ic_fenlei);
                break;
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        dismissDelay(800);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        dismissDelay(800);
        return super.show(transaction, tag);
    }

    @Override
    public void showNow(FragmentManager manager, String tag) {
        super.showNow(manager, tag);
        dismissDelay(800);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }


    private void dismissDelay(long delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        },delay);
    }
}
