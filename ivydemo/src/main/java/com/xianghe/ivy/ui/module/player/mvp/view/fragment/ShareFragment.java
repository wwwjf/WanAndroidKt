package com.xianghe.ivy.ui.module.player.mvp.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.xianghe.ivy.R;
import com.xianghe.ivy.ui.module.player.dialog.ReportDialog;
import com.xianghe.ivy.ui.module.player.dialog.TextInputDialog;
import com.xianghe.ivy.ui.module.player.dialog.TipResultDialog;
import com.xianghe.ivy.ui.module.player.mvp.contact.ShareContact;
import com.xianghe.ivy.ui.module.player.mvp.presenter.SharePresenter;
import com.xianghe.ivy.ui.module.player.mvp.view.fragment.base.MenuFragment;
import com.xianghe.ivy.utils.LanguageUtil;
import com.xianghe.ivy.utils.ToastUtil;
import com.xianghe.ivy.weight.CustomProgress;

import java.util.Locale;
import java.util.Map;


public class ShareFragment extends MenuFragment<ShareContact.View, ShareContact.Presenter> implements ShareContact.View, View.OnClickListener {
    private static final String TAG = "ShareFragment";

    private static final String TAG_DIALOG_REPORT = "tag_dialog_report";    // 举报对话框tag
    private static final int REQ_CODE_FORWARD_INPUT = 666;    // 请求码：输入转发内容

    private Context mContext;

    private View mBtnWeChatShare;
    private View mBtnQQShare;
    private View mBtnWeiboShare;
    private View mBtnWeChatMomentsShare;
    private TextView mBtnFacebook;
    private View mBtnForward;
    private View mBtnDownload;
    private View mBtnReport;

    private Listener mListener = null;

    @Nullable
    @Override
    public ShareContact.Presenter createPresenter() {
        return new SharePresenter(mContext);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view, savedInstanceState);
        initView(view, savedInstanceState);
        initListener(view, savedInstanceState);
        if (mPresenter != null) {
            mPresenter.requestShareInfo();
        }
    }

    private void findView(View rootView, Bundle savedInstanceState) {
        mBtnWeChatShare = rootView.findViewById(R.id.item_wechat_share);
        mBtnQQShare = rootView.findViewById(R.id.item_qq_share);
        mBtnWeiboShare = rootView.findViewById(R.id.item_weibo_share);
        mBtnWeChatMomentsShare = rootView.findViewById(R.id.item_wechat_moments);
        mBtnFacebook = rootView.findViewById(R.id.item_facebook);
        mBtnForward = rootView.findViewById(R.id.item_forward);
        mBtnDownload = rootView.findViewById(R.id.item_download);
        mBtnReport = rootView.findViewById(R.id.btn_report);
    }

    private void initView(View view, Bundle savedInstanceState) {
        if (LanguageUtil.isSimplifiedChinese(getContext())) {
            mBtnQQShare.setVisibility(View.VISIBLE);
            mBtnWeiboShare.setVisibility(View.VISIBLE);
            mBtnWeChatMomentsShare.setVisibility(View.VISIBLE);
            mBtnFacebook.setVisibility(View.GONE);
        } else {
            mBtnQQShare.setVisibility(View.GONE);
            mBtnWeiboShare.setVisibility(View.GONE);
            mBtnWeChatMomentsShare.setVisibility(View.GONE);
            mBtnFacebook.setVisibility(View.VISIBLE);
        }
    }

    private void initListener(View rootView, Bundle savedInstanceState) {
        rootView.setOnClickListener(this);  // 空白区域点击
        mBtnWeChatShare.setOnClickListener(this);
        mBtnQQShare.setOnClickListener(this);
        mBtnWeiboShare.setOnClickListener(this);
        mBtnWeChatMomentsShare.setOnClickListener(this);
        mBtnFacebook.setOnClickListener(this);
        mBtnForward.setOnClickListener(this);
        mBtnDownload.setOnClickListener(this);
        mBtnReport.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_FORWARD_INPUT:
                handleForwardInput(resultCode, data);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_wechat_share:
                onClickItemWeChat();
                break;
            case R.id.item_qq_share:
                onClickItemQQ();
                break;
            case R.id.item_weibo_share:
                onClickItemWeiBo();
                break;
            case R.id.item_wechat_moments:
                onClickItemWeChatMoments();
                break;
            case R.id.item_facebook:
                onClickItemFacebook();
                break;
            case R.id.item_forward:
                onClickItemForward();
                break;
            case R.id.item_download:
                onClickItemDownload();
                break;
            case R.id.btn_report:
                onClickBtnReport();
                break;
            default:
                onClickEmpty();
                break;
        }
    }

    private void handleForwardInput(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String inputResult = TextInputDialog.parseResult(data);
            if (inputResult != null) {
                inputResult = inputResult.trim();
            }
            mBtnForward.setTag(inputResult);
            mPresenter.forwardMedia(getMovie().getId(), inputResult);
        }
    }

    private void onClickItemWeChat() {
//        mPresenter.showShare(mContext, Wechat.NAME, mMovieBean);
        requestMenuClose();
    }

    private void onClickItemQQ() {
//        mPresenter.showShare(mContext, QQ.NAME, mMovieBean);
        requestMenuClose();
    }

    private void onClickItemWeiBo() {
//        mPresenter.showShare(mContext, SinaWeibo.NAME, mMovieBean);
        requestMenuClose();
    }

    private void onClickItemWeChatMoments() {
//        mPresenter.showShare(mContext, WechatMoments.NAME, mMovieBean);
        requestMenuClose();
    }

    private void onClickItemFacebook() {
//        mPresenter.showShare(mContext, Facebook.NAME, mMovieBean);
        requestMenuClose();
    }

    private void onClickItemForward() {
        if (!mPresenter.isLogin()) {
            showLoginConfirmDialog();
            return;
        }

        TextInputDialog.from(getContext())
                .setBtnSubmitText(getString(R.string.share_forward_btn_submit_text))
                .setText((String) mBtnForward.getTag())
                .show(this, REQ_CODE_FORWARD_INPUT);
    }

    private void onClickItemDownload() {
        mPresenter.downloadManager(mContext, mMovieBean);
        requestMenuClose();
    }

    private void onClickBtnReport() {
        mPresenter.reportMedia();
        requestMenuClose();
    }

    private void onClickEmpty() {
        if (mListener != null) {
            mListener.onClickEmpty();
        }
    }

    @Override
    public void onMenuOpened() {
        // do nothing
    }

    @Override
    public void onMenuClosed() {
        // do nothing
    }

    public Listener getListener() {
        return mListener;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void showMsg(String msg) {
        ToastUtil.showToast(getContext(), msg);
    }

    @Override
    public void showMsg(String msg, int iconType) {
        if (iconType == 1) {
            TipResultDialog.create(msg, TipResultDialog.Result.SUCCESS).show(getChildFragmentManager(), "");
        } else if (iconType == 2) {
            TipResultDialog.create(msg, TipResultDialog.Result.FAILED).show(getChildFragmentManager(), "");
        } else {
            TipResultDialog.create(msg, TipResultDialog.Result.FAILED).show(getChildFragmentManager(), "");
        }
    }

    @Override
    public void showRequestPermissionDialog(String[] permissions) {
        requestPermissions(permissions, 0);
    }

    private CustomProgress mCustomProgress;

    @Override
    public void showLoginConfirmDialog() {
        if (mCustomProgress == null) {
            mCustomProgress = new CustomProgress(mContext);
        }
        mCustomProgress.show(getString(R.string.common_tips_no_login),
                getString(R.string.common_tips_title),
                getString(R.string.common_cancel),
                getString(R.string.common_ensure),
                view -> mCustomProgress.cancel(),
                view -> {
//                    startActivity(new Intent(mContext, LoginActivity.class));
                    mCustomProgress.cancel();
                },
                true, null);
    }

    @Override
    public void showReportDialog(Map<Integer, String> items) {
        // 规避用户快速点击出现多个对话框
        if (mReportDialog != null) {
            return;
        }
        ReportDialog.Builder builder = new ReportDialog.Builder();
        if (items != null) {
            for (Map.Entry<Integer, String> entry : items.entrySet()) {
                builder.addItem(new ReportDialog.Item(entry.getKey(), entry.getValue()));
            }
        }
        mReportDialog = builder.build();
        mReportDialog.setContentListener(mContentListener);
        mReportDialog.show(getChildFragmentManager(), TAG_DIALOG_REPORT);

    }

    @Override
    public void onShareResult(String name, boolean result) {
        if (mListener != null) {
            mListener.onShareResult(name, result);
        }
    }

    @Override
    public void onForwardMedia(long mediaId, boolean success) {
        if (success) {
            mBtnForward.setTag(null);
        }
        if (mListener != null) {
            mListener.onReply(mediaId, success);
        }
    }


    private ReportDialog mReportDialog;

    /**
     * 举报对话框事件监听
     */
    private final ReportDialog.OnContentListener mContentListener = new ReportDialog.OnContentListener() {
        @Override
        public void onDismiss(DialogFragment dialog) {
            Log.i(TAG, "onDismiss: ");
            mReportDialog = null;
        }

        @Override
        public void onBtnSubmitClick(DialogFragment dialog, ReportDialog.Item item, CharSequence others) {
            dialog.dismiss();
            int type = 0;
            if (item != null) {
                type = item.getId();
            }
            mPresenter.mediaReport(mMovieBean, type, others);
        }
    };

    public interface Listener {
        public void onClickEmpty();

        public void onShareResult(String name, boolean result);

        public void onReply(long mediaId, boolean success);
    }
}
