package com.xianghe.ivy.ui.module.user;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xianghe.ivy.R;
import com.xianghe.ivy.utils.LanguageUtil;


/**
 * @Author: ycl
 * @Date: 2018/10/25 14:11
 * @Desc:
 */
public class UserShareDialog extends Dialog implements View.OnClickListener {


    private LinearLayout mBtnWeChatShare;
    private LinearLayout mBtnQQShare;
    private LinearLayout mBtnWeiboShare;
    private LinearLayout mBtnFriend;
    private TextView mBtnFacebook;
    private userShareListener mUserShareListener;

    public UserShareDialog(@NonNull Context context, userShareListener userShareListener) {
        super(context, R.style.DialogStyleRightTranslut);
        this.mUserShareListener = userShareListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_user_share);
        initView();
        initData();
    }


    private void initData() {
        // 设置window偏右
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().getAttributes().gravity = Gravity.RIGHT;

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

    }


    private void initView() {
        mBtnWeChatShare = (LinearLayout)findViewById(R.id.item_wechat_share);
        mBtnQQShare = (LinearLayout)findViewById(R.id.item_qq_share);
        mBtnWeiboShare = (LinearLayout)findViewById(R.id.item_weibo_share);
        mBtnFriend = (LinearLayout)findViewById(R.id.item_friend);
        mBtnFacebook = findViewById(R.id.tv_item_facebook);

        if (!LanguageUtil.isSimplifiedChinese(getContext())) {
            mBtnQQShare.setVisibility(View.GONE);
            mBtnWeiboShare.setVisibility(View.GONE);
            mBtnFacebook.setVisibility(View.VISIBLE);
        } else {
            mBtnQQShare.setVisibility(View.VISIBLE);
            mBtnWeiboShare.setVisibility(View.VISIBLE);
            mBtnFacebook.setVisibility(View.GONE);
        }

        mBtnWeChatShare.setOnClickListener(this);
        mBtnQQShare.setOnClickListener(this);
        mBtnWeiboShare.setOnClickListener(this);
        mBtnFriend.setOnClickListener(this);
        mBtnFacebook.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        String platform = "";
        /*switch (v.getId()) {
            case R.id.item_wechat_share:
                platform = Wechat.NAME;
                break;
            case R.id.item_qq_share:
                platform = QQ.NAME;
                break;
            case R.id.item_weibo_share:
                platform = SinaWeibo.NAME;
                break;
            case R.id.item_friend:
                platform = WechatMoments.NAME;
                break;
            case R.id.tv_item_facebook:
                platform = Facebook.NAME;
                break;
        }*/
        if (mUserShareListener != null){
            this.mUserShareListener.userShareSuccess(platform);
        }
    }




    public interface userShareListener {
        void userShareSuccess(String platform);
    }


}
