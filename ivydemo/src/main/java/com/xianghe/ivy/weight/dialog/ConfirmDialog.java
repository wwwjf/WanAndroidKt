package com.xianghe.ivy.weight.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.xianghe.ivy.R;

public class ConfirmDialog extends DialogFragment implements View.OnClickListener {

    private static final String KEY_TITLE_TEXT = "key_title_text";
    private static final String KEY_TITLE_TEXT_SIZE = "key_title_text_size";
    private static final String KEY_TITLE_TEXT_COLOR = "key_title_text_color";

    private static final String KEY_CONTENT_TEXT = "key_content_text";
    private static final String KEY_CONTENT_TEXT_SIZE = "key_content_text_size";
    private static final String KEY_CONTENT_TEXT_COLOR = "key_content_text_color";

    private static final String KEY_BTN_POSITIVE_TEXT = "key_btn_positive_text";
    private static final String KEY_BTN_POSITIVE_TEXT_SIZE = "key_btn_positive_text_size";
    private static final String KEY_BTN_POSITIVE_TEXT_COLOR = "key_btn_positive_text_color";

    private static final String KEY_BTN_NAGTIVE_TEXT = "key_btn_nagtive_text";
    private static final String KEY_BTN_NAGTIVE_TEXT_SIZE = "key_btn_nagtive_text_size";
    private static final String KEY_BTN_NAGTIVE_TEXT_COLOR = "key_btn_nagtive_text_color";

    private static final int INVALID_COLOR = -1;        // 无效颜色
    private static final float INVALID_TEXT_SIZE = -1;    // 无效字体大小

    private TextView mTvTitle;
    private TextView mTvContent;
    private TextView mBtnPositive;
    private TextView mBtnNagtive;

    private BtnClickListener mBtnClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view, savedInstanceState);
        initView(view, savedInstanceState);
        initListener(view, savedInstanceState);
    }

    private void findView(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvContent = view.findViewById(R.id.tv_content);

        mBtnPositive = view.findViewById(R.id.btn_positive);
        mBtnNagtive = view.findViewById(R.id.btn_nagtive);
    }

    private void initView(View view, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        // title
        String titleText = arguments.getString(KEY_TITLE_TEXT);
        float titleTextSize = arguments.getFloat(KEY_TITLE_TEXT_SIZE, INVALID_TEXT_SIZE);
        int titleTextColor = arguments.getInt(KEY_TITLE_TEXT_COLOR, INVALID_COLOR);
        setUpView(mTvTitle, titleText, titleTextSize, titleTextColor);

        // content
        String contentText = arguments.getString(KEY_CONTENT_TEXT);
        float contentTextSize = arguments.getFloat(KEY_CONTENT_TEXT_SIZE, INVALID_TEXT_SIZE);
        int contentTextColor = arguments.getInt(KEY_CONTENT_TEXT_COLOR, INVALID_COLOR);
        setUpView(mTvContent, contentText, contentTextSize, contentTextColor);

        // positive btn
        String btnPositiveText = arguments.getString(KEY_BTN_POSITIVE_TEXT);
        float btnPositiveTextSize = arguments.getFloat(KEY_BTN_POSITIVE_TEXT_SIZE, INVALID_TEXT_SIZE);
        int btnPositiveTextColor = arguments.getInt(KEY_BTN_POSITIVE_TEXT_COLOR, INVALID_COLOR);
        setUpView(mBtnPositive, btnPositiveText, btnPositiveTextSize, btnPositiveTextColor);

        // nagtive btn
        String btnNagtiveText = arguments.getString(KEY_BTN_NAGTIVE_TEXT);
        float btnNagtiveTextSize = arguments.getFloat(KEY_BTN_NAGTIVE_TEXT_SIZE, INVALID_TEXT_SIZE);
        int btnNagtiveTextColor = arguments.getInt(KEY_BTN_NAGTIVE_TEXT_COLOR, INVALID_COLOR);
        setUpView(mBtnNagtive, btnNagtiveText, btnNagtiveTextSize, btnNagtiveTextColor);
    }

    private void initListener(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mBtnPositive.setOnClickListener(this);
        mBtnNagtive.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_positive:
                if (mBtnClickListener != null) {
                    mBtnClickListener.onPositiveBtnClick(this, v);
                }
                break;
            case R.id.btn_nagtive:
                if (mBtnClickListener != null) {
                    mBtnClickListener.onNagtiveBtnClick(this, v);
                }
                break;
        }
    }

    public BtnClickListener getBtnClickListener() {
        return mBtnClickListener;
    }

    public void setBtnClickListener(BtnClickListener btnClickListener) {
        mBtnClickListener = btnClickListener;
    }

    public interface BtnClickListener {
        public void onPositiveBtnClick(ConfirmDialog dialog, View view);

        public void onNagtiveBtnClick(ConfirmDialog dialog, View view);
    }

    public void setUpView(TextView tv, String text, float textSize, int textColor) {
        if (tv == null) {
            return;
        }

        tv.setText(text);
        if (textSize <= INVALID_TEXT_SIZE) {
            tv.setTextSize(textSize);
        }
        if (textColor != INVALID_COLOR) {
            tv.setTextColor(textColor);
        }
    }

    public static class Builder {
        // title
        private String titleText;
        private float titleTextSize = INVALID_TEXT_SIZE;
        private int titleTextColor = INVALID_COLOR;

        // content
        private String contentText;
        private float contentTextSize = INVALID_TEXT_SIZE;
        private int contentTextColor = INVALID_COLOR;

        // positive btn
        private String btnPositiveText;
        private float btnPositiveTextSize = INVALID_TEXT_SIZE;
        private int btnPositiveTextColor = INVALID_COLOR;

        // nagtive btn
        private String btnNagtiveText;
        private float btnNagtiveTextSize = INVALID_TEXT_SIZE;
        private int btnNagtiveTextColor = INVALID_COLOR;

        private BtnClickListener btnClickListener;

        public String getTitleText() {
            return titleText;
        }

        public Builder setTitleText(String titleText) {
            this.titleText = titleText;
            return this;
        }

        public float getTitleTextSize() {
            return titleTextSize;
        }

        public Builder setTitleTextSize(float titleTextSize) {
            this.titleTextSize = titleTextSize;
            return this;
        }

        public int getTitleTextColor() {
            return titleTextColor;
        }

        public Builder setTitleTextColor(int titleTextColor) {
            this.titleTextColor = titleTextColor;
            return this;
        }

        public String getContentText() {
            return contentText;
        }

        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public float getContentTextSize() {
            return contentTextSize;
        }

        public Builder setContentTextSize(float contentTextSize) {
            this.contentTextSize = contentTextSize;
            return this;
        }

        public int getContentTextColor() {
            return contentTextColor;
        }

        public Builder setContentTextColor(int contentTextColor) {
            this.contentTextColor = contentTextColor;
            return this;
        }

        public String getBtnPositiveText() {
            return btnPositiveText;
        }

        public Builder setBtnPositiveText(String btnPositiveText) {
            this.btnPositiveText = btnPositiveText;
            return this;
        }

        public float getBtnPositiveTextSize() {
            return btnPositiveTextSize;
        }

        public Builder setBtnPositiveTextSize(float btnPositiveTextSize) {
            this.btnPositiveTextSize = btnPositiveTextSize;
            return this;
        }

        public int getBtnPositiveTextColor() {
            return btnPositiveTextColor;
        }

        public Builder setBtnPositiveTextColor(int btnPositiveTextColor) {
            this.btnPositiveTextColor = btnPositiveTextColor;
            return this;
        }

        public String getBtnNagtiveText() {
            return btnNagtiveText;
        }

        public Builder setBtnNagtiveText(String btnNagtiveText) {
            this.btnNagtiveText = btnNagtiveText;
            return this;
        }

        public float getBtnNagtiveTextSize() {
            return btnNagtiveTextSize;
        }

        public Builder setBtnNagtiveTextSize(float btnNagtiveTextSize) {
            this.btnNagtiveTextSize = btnNagtiveTextSize;
            return this;
        }

        public int getBtnNagtiveTextColor() {
            return btnNagtiveTextColor;
        }

        public Builder setBtnNagtiveTextColor(int btnNagtiveTextColor) {
            this.btnNagtiveTextColor = btnNagtiveTextColor;
            return this;
        }

        public BtnClickListener getBtnClickListener() {
            return btnClickListener;
        }

        public Builder setBtnClickListener(BtnClickListener btnClickListener) {
            this.btnClickListener = btnClickListener;
            return this;
        }

        public ConfirmDialog build() {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_TITLE_TEXT, getTitleText());
            bundle.putFloat(KEY_TITLE_TEXT_SIZE, getTitleTextSize());
            bundle.putInt(KEY_TITLE_TEXT_COLOR, getTitleTextColor());

            bundle.putString(KEY_CONTENT_TEXT, getContentText());
            bundle.putFloat(KEY_CONTENT_TEXT_SIZE, getContentTextSize());
            bundle.putInt(KEY_CONTENT_TEXT_COLOR, getContentTextColor());

            bundle.putString(KEY_BTN_POSITIVE_TEXT, getBtnPositiveText());
            bundle.putFloat(KEY_BTN_POSITIVE_TEXT_SIZE, getBtnPositiveTextSize());
            bundle.putInt(KEY_BTN_POSITIVE_TEXT_COLOR, getBtnPositiveTextColor());


            bundle.putString(KEY_BTN_NAGTIVE_TEXT, getBtnNagtiveText());
            bundle.putFloat(KEY_BTN_NAGTIVE_TEXT_SIZE, getBtnNagtiveTextSize());
            bundle.putInt(KEY_BTN_NAGTIVE_TEXT_COLOR, getBtnNagtiveTextColor());

            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setArguments(bundle);
            dialog.setBtnClickListener(btnClickListener);
            return dialog;
        }
    }
}
