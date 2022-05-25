package com.xianghe.ivy.ui.module.player.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.xianghe.ivy.R;

import java.io.Serializable;


/**
 * 使用Activity代替dialog, 方便效果实现.
 * 用于处理文字输入.
 */
public class TextInputDialog extends AppCompatActivity implements View.OnClickListener {

    private static final String KEY_TEXT = "key_text";
    private static final String KEY_HINT = "key_hint";
    private static final String KEY_SUBMIT_TEXT = "key_submit_text";
    private static final String KEY_TAG = "key_tag";
    private static final String KEY_RESULT = "key_result";

    private EditText mEtInput;
    private TextView mTvInputCount;
    private TextView mBtnSend;

    /**
     * 文字输入监听
     */
    private TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            int length = s.length();
            int maxLength = 0;

            // TODO: 2018/10/24 影响性能, but 现在不管
            InputFilter[] filters = mEtInput.getFilters();
            for (InputFilter filter : filters) {
                if (filter instanceof InputFilter.LengthFilter) {
                    maxLength = ((InputFilter.LengthFilter) filter).getMax();
                    break;
                }
            }
            mTvInputCount.setText(length + "/" + maxLength);
            if (length >= maxLength) {
                mTvInputCount.setTextColor(getResources().getColor(R.color.red));
            } else {
                mTvInputCount.setTextColor(getResources().getColor(R.color.text_color_gray));
            }
        }
    };

    /**
     * 正确的打开方式, 现在还没有限制打开姿势, 请大家自觉往进入.
     */
    public static Builder from(Context context) {
        Intent intent = new Intent(context, TextInputDialog.class);
        return new Builder(intent);
    }

    /**
     * 在{@link Activity#onActivityResult(int, int, Intent)}或{@link Fragment#onActivityResult(int, int, Intent)}, 解析输入结果.
     *
     * @param data Intent
     * @return 输入结果
     */
    public static String parseResult(Intent data) {
        String result = null;
        if (data != null) {
            result = data.getStringExtra(KEY_RESULT);
        }
        return result;
    }

    /**
     * 在{@link Activity#onActivityResult(int, int, Intent)}或{@link Fragment#onActivityResult(int, int, Intent)}, 解析输入结果.
     *
     * @param data Intent
     * @return tag
     */
    public static Serializable parseTag(Intent data) {
        Serializable result = null;
        if (data != null) {
            result = data.getSerializableExtra(KEY_TAG);
        }
        return result;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input);
        findView(savedInstanceState);
        initView(savedInstanceState);
        initListener(savedInstanceState);
    }

    private void findView(Bundle savedInstanceState) {
        mEtInput = (EditText) findViewById(R.id.et_input);
        mTvInputCount = (TextView) findViewById(R.id.tv_input_count);
        mBtnSend = findViewById(R.id.btn_send);
    }

    private void initView(Bundle savedInstanceState) {
        // 设置Activity样式
        Window window = getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.gravity = Gravity.BOTTOM;
        windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(windowParams);

        Intent intent = getIntent();
        String text = intent.getStringExtra(KEY_TEXT);
        String hint = intent.getStringExtra(KEY_HINT);
        String btnSubmitText = intent.getStringExtra(KEY_SUBMIT_TEXT);
        if (!TextUtils.isEmpty(text)) {
            mEtInput.setText(text);
        }
        if (!TextUtils.isEmpty(hint)) {
            mEtInput.setHint(hint);
        }
        if (!TextUtils.isEmpty(btnSubmitText)) {
            mBtnSend.setText(btnSubmitText);
        }
    }

    private void initListener(Bundle savedInstanceState) {
        mEtInput.addTextChangedListener(mWatcher);
        mBtnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                onClickBtnSend();
                break;
        }
    }

    private void onClickBtnSend() {
        Intent data = new Intent();
        data.putExtra(KEY_RESULT, mEtInput.getText().toString());
        data.putExtra(KEY_TAG, getIntent().getSerializableExtra(KEY_TAG));
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    public static class Builder {

        private final Intent mIntent;

        public Builder(Intent intent) {
            mIntent = intent;
        }

        public Builder setText(String text) {
            mIntent.putExtra(KEY_TEXT, text);
            return this;
        }

        public Builder setHint(String hint) {
            mIntent.putExtra(KEY_HINT, hint);
            return this;
        }

        public Builder setBtnSubmitText(String text) {
            mIntent.putExtra(KEY_SUBMIT_TEXT, text);
            return this;
        }

        public Builder setTag(Serializable tag) {
            mIntent.putExtra(KEY_TAG, tag);
            return this;
        }

        /**
         * 正确的打开方式, 现在还没有限制打开姿势, 请大家自觉往这走.
         *
         * @param activity    activity
         * @param requestCode requestCode, 在{@link Activity#onActivityResult(int, int, Intent)} 或
         *                    {@link Fragment#onActivityResult(int, int, Intent)} 中, 回调的判断标志.
         */
        public void show(Activity activity, int requestCode) {
            activity.startActivityForResult(mIntent, requestCode);
        }

        /**
         * 正确的打开方式, 现在还没有限制打开姿势, 请大家自觉往这走.
         *
         * @param fragment    fragment
         * @param requestCode requestCode
         */
        public void show(Fragment fragment, int requestCode) {
            fragment.startActivityForResult(mIntent, requestCode);
        }
    }
}
