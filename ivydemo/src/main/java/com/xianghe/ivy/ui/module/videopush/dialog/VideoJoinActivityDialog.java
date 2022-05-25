package com.xianghe.ivy.ui.module.videopush.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xianghe.ivy.R;

/**
 * @创建者 Allen
 * @创建时间 2019/3/28 9:31
 * @描述     “微电影有奖征集”活动Dialog
 */
public class VideoJoinActivityDialog extends Dialog {

    private ImageView mDialogClose;
    private RelativeLayout mDialogRl;
    private ImageView mContentIv;
    private TextView mContentTv;
    private TextView mTitleName;
    private Activity mContext;
    private String mTitle, mContentDetails, mContentUrl;

    public VideoJoinActivityDialog(Activity context) {
        super(context);
    }

    public VideoJoinActivityDialog(Activity context, String titleName, String contentTv, String contentUrl) {
        super(context, R.style.DialogStyleBottomTranslut);
        mContext = context;
        mTitle = titleName;
        mContentDetails = contentTv;
        mContentUrl = contentUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_vp_join_details);
        initView();
        initListener();
        initData();
    }

    private void initData() {
        if (mTitle != null && !mTitle.isEmpty()){
            mTitleName.setText(mTitle);
        }

        if (mContentDetails != null && !mContentDetails.isEmpty()){
            mContentTv.setText(mContentDetails);
            mContentTv.setVisibility(View.VISIBLE);
        } else {
            mContentTv.setVisibility(View.GONE);
        }

        if (mContentUrl != null && !mContentUrl.isEmpty()){
            Glide.with(mContext).load(mContentUrl).into(mContentIv);
        }
    }

    private void initView() {
        mDialogClose = findViewById(R.id.dialog_close_iv);
        mDialogRl = findViewById(R.id.huodong_dialog_rl);
        mContentTv = (TextView) findViewById(R.id.dialog_content_tv);
        mTitleName = (TextView) findViewById(R.id.dialog_title_name);
        mContentIv = (ImageView) findViewById(R.id.dialog_content_iv);
    }

    private void initListener() {
        mDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
