package com.xianghe.ivy.ui.module.videopush.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.xianghe.ivy.R;
import com.xianghe.ivy.weight.basepopuwindow.util.InputMethodUtils;

import java.io.Serializable;


/**
 * 使用Activity代替dialog, 方便效果实现.
 * 用于处理文字输入.
 */
public class VPInputDialog extends AppCompatActivity implements View.OnClickListener {

    private static final String KEY_TEXT = "key_text";
    private static final String KEY_HINT = "key_hint";
    private static final String KEY_TAG = "key_tag";
    private static final String KEY_RESULT = "key_result";

    private EditText mEtInput;
    private TextView mTvInputCount;
    private View mBtnSend;

    private Toast mNoticeToast;

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
        Intent intent = new Intent(context, VPInputDialog.class);
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
        setContentView(R.layout.dialog_input_vp);
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
//        windowParams.dimAmount = 0;
        windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(windowParams);

        Intent intent = getIntent();
        String text = intent.getStringExtra(KEY_TEXT);
        String hint = intent.getStringExtra(KEY_HINT);
        if (!TextUtils.isEmpty(text)) {
            mEtInput.setText(text);

            int length = text.length();
            mEtInput.setSelection(length);
            int maxLength = 0;

            // TODO: 2018/10/24 影响性能, but 现在不管
            InputFilter[] filters = mEtInput.getFilters();
            for (InputFilter filter : filters) {
                if (filter instanceof InputFilter.LengthFilter) {
                    maxLength = ((InputFilter.LengthFilter) filter).getMax();
                }
            }
            mTvInputCount.setText(length + "/" + maxLength);
            if (length >= 15) {
                mTvInputCount.setTextColor(getResources().getColor(R.color.red));
            } else {
                mTvInputCount.setTextColor(getResources().getColor(R.color.text_color_gray));
            }
        }
        if (!TextUtils.isEmpty(hint)) {
            mEtInput.setHint(hint);
        }
    }

    private void initListener(Bundle savedInstanceState) {
        mEtInput.addTextChangedListener(mWatcher);
        mBtnSend.setOnClickListener(this);
        mNoticeToast = createNoticeToast();
    }

    private Toast createNoticeToast() {
        Toast toast = new Toast(this);
        View view = LayoutInflater.from(this).inflate(R.layout.toast_item_notice, null);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        return toast;
    }

    private void showNoticeToast(String msg) {
        if (mNoticeToast != null) {
            View view = mNoticeToast.getView();
            TextView tv = (TextView) view.findViewById(R.id.tv_custom_alert_dialog_notice_ensure);
            tv.setText(msg);
            mNoticeToast.show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String inputText = mEtInput.getText().toString();
                if (TextUtils.isEmpty(inputText)) {
                    showNoticeToast(getIntent().getStringExtra(KEY_HINT));
                    return;
                }
//                if (inputText.matches("[\\u4e00-\\u9fa5]+")) {
//                    if (inputText.length() > 4) {
//                        showNoticeToast("最多输入4个中文");
//                        return;
//                    }
//                } else {
                    if (inputText.length() > 15) {
                        showNoticeToast(getString(R.string.common_inpout_length_15));
                        return;
                    }
//                }
                InputMethodUtils.hideSoftInput(this);
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
         * @param requestCode requestCode, 在{@link Activity#onActivityResult(int, int, Intent)} 或
         *                    {@link Fragment#onActivityResult(int, int, Intent)} 中, 回调的判断标志.
         */
        public void show(Fragment fragment, int requestCode) {
            fragment.startActivityForResult(mIntent, requestCode);
        }
    }
}
