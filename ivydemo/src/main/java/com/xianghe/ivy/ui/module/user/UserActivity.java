package com.xianghe.ivy.ui.module.user;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.app.hubert.guide.model.RelativeGuide;
import com.blankj.utilcode.util.ActivityUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.wwwjf.base.utils.ToastUtil;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.app.IvyConstants;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.constant.RxBusCode;
import com.xianghe.ivy.manager.PermissionManager;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.TabEntity;
import com.xianghe.ivy.model.UserBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.ui.base.FloatingMenuActivity;
import com.xianghe.ivy.ui.module.main.mvp.view.activity.MainActivity;
import com.xianghe.ivy.ui.module.player.dialog.ReportDialog;
import com.xianghe.ivy.ui.module.player.dialog.TipResultDialog;
import com.xianghe.ivy.ui.module.record.RecordActivity;
import com.xianghe.ivy.ui.module.record.cache_movie.CacheMovieActivity;
import com.xianghe.ivy.ui.module.user.movie.UserMovieFragment;
import com.xianghe.ivy.ui.module.welcom.CustomVideoView;
import com.xianghe.ivy.ui.push.PushModel;
import com.xianghe.ivy.utils.Formatter;
import com.xianghe.ivy.utils.IvyUtils;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.LanguageUtil;
import com.xianghe.ivy.utils.NetworkUtil;
import com.xianghe.ivy.weight.CustomProgress;
import com.xianghe.ivy.weight.circlemenu.IMenu;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gorden.rxbus2.RxBus;
import gorden.rxbus2.Subscribe;
import gorden.rxbus2.ThreadMode;

public class UserActivity extends FloatingMenuActivity<UserContract.View, UserContract.Presenter>
        implements UserContract.View {

    //????????????????????????
    private MyBroadcastReceiver myBroadcastReceiver;
    private IntentFilter intentFilter1;

    @BindView(R.id.iv_activity_user_focus)
    ImageView mIvUserFocus;
    @BindView(R.id.iv_activity_user_relative)
    ImageView mIvUserRelative;
    @BindView(R.id.iv_activity_user_report)
    ImageView mIvUserReport;
    @BindView(R.id.ll_activity_user_theirInfo)
    LinearLayout mLlUserTheirInfo;
    @BindView(R.id.iv_activity_user_movieCache)
    ImageView mIvMovieCache;
    @BindView(R.id.iv_activity_user_share)
    ImageView mIvUserShare;
    @BindView(R.id.iv_activity_user_guide)
    ImageView mIvUserGuide;
    @BindView(R.id.iv_activity_user_invite_friend)
    ImageView mIvUserInviteFriend;
    @BindView(R.id.iv_activity_user_setting)
    ImageView mIvUserSetting;
    @BindView(R.id.ll_activity_user_myInfo)
    LinearLayout mLlUserMyInfo;
    @BindView(R.id.iv_activity_user_avatar)
    ImageView mIvUserAvatar;
    @BindView(R.id.iv_activity_user_stockman)
    ImageView mIvUserStockMan;
    @BindView(R.id.iv_activity_user_vip)
    ImageView mIvUserVip;
    @BindView(R.id.tv_activity_user_userName)
    TextView mTvUserName;
    @BindView(R.id.tv_activity_user_ivyUid)
    TextView mTvUserIvyUid;
    @BindView(R.id.tv_activity_user_intro)
    TextView mTvUserIntro;
    @BindView(R.id.tv_activity_user_follow_count)
    TextView mTvUserFollowCount;
    @BindView(R.id.ll_activity_user_follow)
    LinearLayout mLlUserFollow;
    @BindView(R.id.tv_activity_user_like_count)
    TextView mTvUserLikeCount;
    @BindView(R.id.ll_activity_user_like)
    LinearLayout mLlUserLike;
    @BindView(R.id.tv_activity_user_collect_count)
    TextView mTvUserCollectCount;
    @BindView(R.id.ll_activity_user_collect)
    LinearLayout mLlUserCollect;
    @BindView(R.id.tv_activity_user_fans_count)
    TextView mTvUserFansCount;
    @BindView(R.id.ll_activity_user_fans)
    LinearLayout mLlUserFans;
    @BindView(R.id.tv_activity_user_relativeFriend_count)
    TextView mTvUserRelativeFriendCount;
    @BindView(R.id.ll_activity_user_relativeFriend)
    LinearLayout mLlUserRelativeFriend;
    @BindView(R.id.iv_activity_user_myInfo_back)
    ImageView mIvMyInfoBack;
    @BindView(R.id.iv_activity_user_back)
    ImageView mIvUserBack;
    @BindView(R.id.iv_activity_user_background)
    ImageView mIvUserBackground;
    @BindView(R.id.ll_activity_user_left_root)
    LinearLayout mLlLeftRootView;
    @BindView(R.id.ll_activity_user_full_root)
    View mLlRootView;
    @BindView(R.id.commonTabLayout_activity_user_detail)
    CommonTabLayout mCommonTabLayoutUser;
    @BindView(R.id.viewPager_activity_user_detail)
    ViewPager mViewPagerUser;
    @BindView(R.id.iv_activity_user_videoCall)
    ImageView mIvVideoCall;

    private long mUid;
    private long mLoginId;
    private UserBean mUserBean;
    private ReportDialog mReportDialog;
    private CustomProgress mCustomProgress;
    private EasyPopup mUserMorePopup;
    private String[] mTitles;
    private ArrayList<Fragment> mFragmentList;
    private ArrayList<CustomTabEntity> mTabDataList;
    private PermissionManager.PermissionListener mPermissionListener;
    private UserShareDialog mUserShareDialog;
    private static final String TAG_DIALOG_REPORT = "tag_dialog_report";    // ???????????????tag
    private static final String RECODER_TAG = "recoder";
    private UserRelationshipBroadCast mUserRelationshipBroadCast;

    private RequestOptions mPlaceholder = new RequestOptions()
            .error(R.mipmap.ic_my_head_default)
            .placeholder(R.mipmap.ic_my_head_default);
    /**
     * ??????????????????????????????
     */
    private String mAction;
    public static final String ACTION_KEY = "route_action";
    public static final String DYNAMIC_VALUE = "dynamic";

    @Override
    protected boolean isNeedDownUpLoad() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        mCustomProgress = new CustomProgress(this);
        Uri data = getIntent().getData();
        if (data != null) {
            mUid = Long.parseLong(Objects.requireNonNull(TextUtils.isEmpty(data.getQueryParameter("uid")) ?
                    "0" : data.getQueryParameter("uid")));
            mAction = data.getQueryParameter(ACTION_KEY);
        }
        mPermissionListener = new MainPermissionListener();

        initUserInfoData();
        initViewPager();

        //????????????
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Global.USER_RELATIONSHIP_CHANGE);
        mUserRelationshipBroadCast = new UserRelationshipBroadCast();
        registerReceiver(mUserRelationshipBroadCast, intentFilter);

        //??????????????????????????? ????????????
        intentFilter1 =new IntentFilter();
        //????????????????????????????????????????????????????????????????????????
        intentFilter1.addAction(IvyConstants.close_activity_broadcast_receiver);
        //??????????????????
        myBroadcastReceiver = new UserActivity.MyBroadcastReceiver();
        //????????????????????????????????????????????????????????????
        registerReceiver(myBroadcastReceiver,intentFilter1);

        //????????????
        if (UserInfoManager.isLogin() && mUid == 0 && NetworkUtil.isNetworkConnected(this)) {
           // mIvUserSetting.postDelayed(this::initGuideLayout,10);
        }

    }




    private void initViewPager() {
        mTitles = new String[]{getString(R.string.common_media),
                getString(R.string.common_dynamic)};
        mTabDataList = new ArrayList<>();
        mFragmentList = new ArrayList<>();
        KLog.e("===========mtitle[0]=" + mTitles[0]);
        KLog.e("===========mtitle[1]=" + mTitles[1]);
        mTabDataList.add(new TabEntity(mTitles[0], 0, 0));
        mTabDataList.add(new TabEntity(mTitles[1], 0, 0));
        Bundle bundle = new Bundle();
        bundle.putString("param1", mUid + "");
        bundle.putString("param2", mTitles[0]);
        UserMovieFragment userMovieFragment = new UserMovieFragment();
        userMovieFragment.setArguments(bundle);
        mFragmentList.add(userMovieFragment);
        bundle.clear();
        bundle.putString("param1", mUid + "");
        bundle.putString("param2", mTitles[1]);
        FragPagerAdapter fragPagerAdapter = new FragPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPagerUser.setAdapter(fragPagerAdapter);
        mCommonTabLayoutUser.setTabData(mTabDataList);
        mCommonTabLayoutUser.showDot(1);
        mCommonTabLayoutUser.hideMsg(1);
        mCommonTabLayoutUser.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPagerUser.setCurrentItem(position, true);
                if (position == 1) {
                    mCommonTabLayoutUser.hideMsg(1);
                }
            }

            @Override
            public void onTabReselect(int position) {
                if (position == 1) {
                    mCommonTabLayoutUser.hideMsg(1);
                }
            }
        });

        //??????????????????
        if (!TextUtils.isEmpty(mAction) && DYNAMIC_VALUE.equals(mAction)) {
            mViewPagerUser.setCurrentItem(1);
            mCommonTabLayoutUser.setCurrentTab(1);
        }
    }

    /**
     * ?????????????????????
     */
    private void initUserInfoData() {
        //??????????????????uid???????????????uid??????
        mLoginId = UserInfoManager.getUid();
        if (mUid == 0 || mLoginId == mUid) {//????????????
            if (mLoginId == mUid) {//?????????uid????????????uid??????
                //?????????????????????
                mIvMovieCache.setVisibility(View.GONE);
            } else {
                //?????????????????????
                mIvMovieCache.setVisibility(View.VISIBLE);
            }
            mLlUserMyInfo.setVisibility(View.VISIBLE);
            mLlUserTheirInfo.setVisibility(View.GONE);
            mIvVideoCall.setVisibility(View.GONE);
        } else {//????????????
            mLlUserMyInfo.setVisibility(View.GONE);
            mLlUserTheirInfo.setVisibility(View.VISIBLE);
            if (mIvMovieCache.getVisibility() == View.VISIBLE) {
                mIvMovieCache.setVisibility(View.GONE);
            }
            if (UserInfoManager.getHxIsLogin()) {
                mIvVideoCall.setVisibility(View.VISIBLE);
            }else {
                mIvVideoCall.setVisibility(View.GONE);
            }
        }

        if (UserInfoManager.isLogin()) {
            mLlLeftRootView.setVisibility(View.VISIBLE);
            if (mPresenter != null) {
                mPresenter.requestData(mUid);
            }
        } else {
            initUnLoginView();
        }
    }

    @Override
    protected void onClickMenuCamera(@NonNull IMenu menus) {
        menus.close(true);
        if (!UserInfoManager.isLogin()) {
            showLogin();
            return;
        }

        /*if (UploadTaskManager.getInstance().hasTasks()) {
            showToast("????????????????????????????????????????????????");
            return;
        }*/

        //????????????
        mPermissionManager.requestPermissions(this, mPermissionListener, new String[]{
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.RECORD_AUDIO
        }, RECODER_TAG);
    }

    @Override
    protected void onClickMenuMine(@NonNull IMenu menus) {
        menus.close(true);
        if (mUid != 0) {
            if (mUid == mLoginId) {//????????????
                return;
            }
            ActivityUtils.startActivity(this, UserActivity.class);
        }
    }

    @Override
    protected void onClickMenuHome(@NonNull IMenu menus) {
        menus.close(true);
        this.finish();
        // ????????????
        Intent intent = MainActivity.getStartIntent(getBaseContext(), false);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        if (LanguageUtil.isSimplifiedChinese(this)) {
            if (mIvUserInviteFriend.getVisibility() == View.GONE) {
                mIvUserInviteFriend.setVisibility(View.VISIBLE);
            }
            if (mIvUserStockMan.getVisibility() == View.GONE) {
                mIvUserStockMan.setVisibility(View.VISIBLE);
            }

            if (mIvUserVip.getVisibility() == View.GONE) {
                mIvUserVip.setVisibility(View.VISIBLE);
            }

            if (mUserBean != null) {
                if (mUserBean.getIs_author_star() == 0) {
                    mIvUserFocus.setImageResource(R.mipmap.ic_my_focus);
                } else {
                    mIvUserFocus.setImageResource(R.mipmap.btn_yiguanzhu_normal);
                }

                if (mUserBean.getIs_friend() == 0) {
                    mIvUserRelative.setImageResource(R.mipmap.ic_my_applyrelative);
                } else if (mUserBean.getIs_friend() == 1) {
                    mIvUserRelative.setImageResource(R.mipmap.btn_quxiaoqinyou_default);
                } else if (mUserBean.getIs_friend() == 2) {
                    mIvUserRelative.setImageResource(R.mipmap.btn_wait_default);
                }

                mIvUserStockMan.setVisibility(mUserBean.getRole() == 1 ? View.VISIBLE : View.GONE);
                mIvUserStockMan.setImageResource(R.mipmap.ic_my_stockman);
                mIvUserVip.setVisibility(mUserBean.getMembership() == 1 ? View.VISIBLE : View.GONE);
                mIvUserVip.setImageResource(R.mipmap.ic_my_vip);
            }
        } else {
            if (mUserBean != null) {
                if (mUserBean.getIs_author_star() == 0) {
                    mIvUserFocus.setImageResource(R.mipmap.ic_my_focus_en);
                } else {
                    mIvUserFocus.setImageResource(R.mipmap.btn_yiguanzhu_normal_en);
                }

                if (mUserBean.getIs_friend() == 0) {
                    mIvUserRelative.setImageResource(R.mipmap.ic_my_applyrelative_en);
                } else if (mUserBean.getIs_friend() == 1) {
                    mIvUserRelative.setImageResource(R.mipmap.btn_quxiaoqinyou_default_en);
                } else if (mUserBean.getIs_friend() == 2) {
                    mIvUserRelative.setImageResource(R.mipmap.btn_wait_default_en);
                }
                mIvUserStockMan.setVisibility(mUserBean.getRole() == 1 ? View.VISIBLE : View.GONE);
                mIvUserStockMan.setImageResource(R.mipmap.ic_my_stockman_en);
                mIvUserVip.setVisibility(mUserBean.getMembership() == 1 ? View.VISIBLE : View.GONE);
                mIvUserVip.setImageResource(R.mipmap.ic_my_vip_en);
            }

            if (mIvUserInviteFriend.getVisibility() == View.VISIBLE) {
                mIvUserInviteFriend.setVisibility(View.GONE);
            }

            if (mIvUserStockMan.getVisibility() == View.VISIBLE) {
                mIvUserStockMan.setVisibility(View.GONE);
            }

            if (mIvUserVip.getVisibility() == View.VISIBLE) {
                mIvUserVip.setVisibility(View.GONE);
            }
        }
        super.onResume();
    }

    @Nullable
    @Override
    public UserContract.Presenter createPresenter() {
        return new UserPresenter();
    }

    @Override
    public <E> void onError(@Nullable E e) {
        KLog.e("==============onError");
    }

    @Override
    public <D> void onSuccess(@Nullable D d) {
        KLog.e("==============onSuccess");
    }

    @Override
    public void showLoading() {
//        mVaryViewHelperController.showLoading();
        KLog.e("---------showLoading");
    }

    @Override
    public void dismissLoading() {
//        mVaryViewHelperController.restore();
        KLog.e("-------dismissLoading");
    }

    @Override
    public void onRequestDataSuccess(BaseResponse<UserBean> d) {
        showUserInfo(d.getData());
    }

    @Override
    public void onReportUserSuccess(BaseResponse<String> d) {
        KLog.e("--------------????????????");
        TipResultDialog.create(getString(R.string.dialog_report_success),
                TipResultDialog.Result.SUCCESS).show(getSupportFragmentManager(), "");

    }

    @Override
    public void onReportUserFailed(String errorMsg) {
        KLog.e("--------------????????????");
        TipResultDialog.create(getString(R.string.dialog_report_failed),
                TipResultDialog.Result.FAILED).show(getSupportFragmentManager(), "");
    }

    @Override
    public void onBlacklistSuccess(BaseResponse<String> response) {
        showToast(BaseResponse.infoCode2String(UserActivity.this, response.getInfoCode()));
        clearNetworkCache();
        setResult(RESULT_OK);
        //????????????
        if (mPresenter != null) {
            mPresenter.requestData(mUid);
        }
        RxBus.get().send(RxBusCode.ACT_BLACKLIST_CHANGE);
        if (mUserBean != null) {
            // ??? ??????????????? -- ?????? --> ??????????????????????????????
            if (mUserBean.getIs_author_star() == 1) {
                sendBroadcast(mUserBean.getUid(), Global.CASE_UNFOLLOW);
            }
            if (mUserBean.getIs_friend() == 1) {
                sendBroadcast(mUserBean.getUid(), Global.CASE_DEL_FRIEND);
            }

            mUserBean.setIs_author_star(0);
            mUserBean.setIs_friend(0);
        }
    }

    @Override
    public void onBlacklistFailed(Throwable throwable) {
        showToast(throwable.getMessage());
    }

    @Override
    public void showReportDialog(Map<Integer, String> items) {
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
        mReportDialog.show(getSupportFragmentManager(), TAG_DIALOG_REPORT);
    }

    @Override
    public void onFollowUserSuccess(BaseResponse<String> response) {
        showToast(getString(R.string.common_follow_success));
        clearNetworkCache();
        if (LanguageUtil.isSimplifiedChinese(this)) {
            mIvUserFocus.setImageResource(R.mipmap.btn_yiguanzhu_normal);
        } else {
            mIvUserFocus.setImageResource(R.mipmap.btn_yiguanzhu_normal_en);
        }
        if (mPresenter != null) {
            mPresenter.requestData(mUid);
            setResult(RESULT_OK);
            if (mUserBean != null) {
                mUserBean.setIs_author_star(1);
                sendBroadcast(mUserBean.getUid(), Global.CASE_FOLLOW);
            }
        }

    }

    @Override
    public void onUnFollowUserSuccess(BaseResponse<String> response) {
        showToast(getString(R.string.common_cancel_follow_success));
        clearNetworkCache();
        if (LanguageUtil.isSimplifiedChinese(this)) {
            mIvUserFocus.setImageResource(R.mipmap.ic_my_focus);
        } else {
            mIvUserFocus.setImageResource(R.mipmap.ic_my_focus_en);
        }
        if (mPresenter != null) {
            mPresenter.requestData(mUid);
            setResult(RESULT_OK);
            if (mUserBean != null) {
                mUserBean.setIs_author_star(0);
                sendBroadcast(mUserBean.getUid(), Global.CASE_UNFOLLOW);
            }
        }
    }

    @Override
    public void onApplyRelativeFriendSuccess(BaseResponse<String> response) {
        showToast(getString(R.string.common_wait_user_apply));
        clearNetworkCache();
        if (mPresenter != null) {
            mPresenter.requestData(mUid);
        }
        if (mUserBean != null) {
            mUserBean.setIs_friend(2);
            sendBroadcast(mUserBean.getUid(), Global.CASE_ADD_FRIEND);
        }
    }

    @Override
    public void onRequestFailed(Throwable throwable) {
        String message = throwable.getMessage();
        if (!TextUtils.isEmpty(message)) {
            showToast(message);
        }
    }

    @Override
    public void onDeleteRelativeFriendSuccess(BaseResponse<String> response) {
        showToast(getString(R.string.common_delete_friend_success));
        clearNetworkCache();
        if (mPresenter != null) {
            mPresenter.requestData(mUid);
        }
        setResult(RESULT_OK);
        RxBus.get().send(RxBusCode.ACT_RELATIVE_FRIEND);

        if (mUserBean != null) {
            mUserBean.setIs_friend(0);
            sendBroadcast(mUserBean.getUid(), Global.CASE_DEL_FRIEND);
        }
    }

    @Override
    public void onNetworkError(Throwable throwable) {
        KLog.e("----------onNetworkError");
        /*mVaryViewHelperController.showNetworkError(v -> {
            if (mPresenter != null) {
                showLoading();
                mPresenter.requestData(mUid);
            }
        });*/
    }


    private void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        ToastUtil.show(this,msg);
    }

    /**
     * ??????????????????
     *
     * @param userBean
     */
    private void showUserInfo(UserBean userBean) {
        mUserBean = userBean;
        if (mUserBean == null) {
            return;
        }

        if (!TextUtils.isEmpty(userBean.getAvatar())) {
            Glide.with(this)
                    .load(new GlideUrl(userBean.getAvatar()))
                    .apply(mPlaceholder)
                    .into(mIvUserAvatar);
            Glide.with(this)
                    .load(new GlideUrl(userBean.getAvatar()))
                    .into(mIvUserBackground);
        }

        boolean isZh = LanguageUtil.isSimplifiedChinese(this);
        if (mUserBean.getIs_author_star() == 0) {
            if (isZh) {
                mIvUserFocus.setImageResource(R.mipmap.ic_my_focus);
            } else {
                mIvUserFocus.setImageResource(R.mipmap.ic_my_focus_en);
            }
        } else {
            if (isZh) {
                mIvUserFocus.setImageResource(R.mipmap.btn_yiguanzhu_normal);
            } else {
                mIvUserFocus.setImageResource(R.mipmap.btn_yiguanzhu_normal_en);
            }
        }

        if (mUserBean.getIs_friend() == 0) {
            if (isZh) {
                mIvUserRelative.setImageResource(R.mipmap.ic_my_applyrelative);
            } else {
                mIvUserRelative.setImageResource(R.mipmap.en_jaiqinyou_default);
            }
        } else if (mUserBean.getIs_friend() == 1) {
            if (isZh) {
                mIvUserRelative.setImageResource(R.mipmap.btn_quxiaoqinyou_default);
            } else {
                mIvUserRelative.setImageResource(R.mipmap.btn_quxiaoqinyou_default_en);
            }
        } else if (mUserBean.getIs_friend() == 2) {
            if (isZh) {
                mIvUserRelative.setImageResource(R.mipmap.btn_wait_default);
            } else {
                mIvUserRelative.setImageResource(R.mipmap.btn_wait_default_en);
            }
        }
        //?????????????????????????????????-???????????????????????????-????????????
        mTvUserName.setText(userBean.getName());
        if (!TextUtils.isEmpty(userBean.getRemarkName())){
            mTvUserName.setText(userBean.getRemarkName());
        }
        Drawable sexDrawable = userBean.getSex() == 1 ? getDrawable(R.mipmap.icon_boy) : getDrawable(R.mipmap.icon_girl);
        mTvUserName.setCompoundDrawablesWithIntrinsicBounds(null, null, sexDrawable, null);
        mTvUserIvyUid.setText(String.valueOf("ID:" + userBean.getUid()));
        mTvUserFollowCount.setText(Formatter.formatNumber(userBean.getFollow_count()));
        mTvUserLikeCount.setText(Formatter.formatNumber(userBean.getPraise()));
        mTvUserCollectCount.setText(Formatter.formatNumber(userBean.getCollection()));
        mTvUserFansCount.setText(Formatter.formatNumber(userBean.getFans_count()));
        mTvUserRelativeFriendCount.setText(Formatter.formatNumber(userBean.getFriend_num()));
        mIvUserStockMan.setVisibility(userBean.getRole() == 1 ? View.VISIBLE : View.GONE);
        mIvUserStockMan.setImageResource(isZh ? R.mipmap.ic_my_stockman : R.mipmap.ic_my_stockman_en);
        mIvUserVip.setVisibility(userBean.getMembership() == 1 ? View.VISIBLE : View.GONE);
        mIvUserVip.setImageResource(isZh ? R.mipmap.ic_my_vip : R.mipmap.ic_my_vip_en);
        if (mUid == 0 || mUid == mLoginId) {
            //????????????
            mTvUserIntro.setText(TextUtils.isEmpty(userBean.getSignature()) ? "" : userBean.getSignature());
            mLlUserRelativeFriend.setVisibility(View.VISIBLE);
            UserInfoManager.putUserIsMember(userBean.getMembership() == 1);
        } else {
            //????????????
            if (TextUtils.isEmpty(userBean.getSignature())) {//?????????????????????
                mTvUserIntro.setHint(getResources().getString(R.string.user_intro_empty));
            } else {
                mTvUserIntro.setText(userBean.getSignature());
            }
            mLlUserRelativeFriend.setVisibility(View.GONE);//??????????????????
        }
    }

    @OnClick({R.id.iv_activity_user_movieCache, R.id.iv_activity_user_share, R.id.iv_activity_user_guide,R.id.iv_activity_user_invite_friend,
            R.id.iv_activity_user_setting, R.id.iv_activity_user_myInfo_back, R.id.iv_activity_user_back, R.id.iv_activity_user_focus,
            R.id.iv_activity_user_relative, R.id.iv_activity_user_report, R.id.iv_activity_user_avatar,R.id.tv_activity_user_userName,
            R.id.ll_activity_user_follow, R.id.ll_activity_user_like, R.id.ll_activity_user_collect,
            R.id.ll_activity_user_fans, R.id.ll_activity_user_relativeFriend, R.id.iv_activity_user_videoCall})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.iv_activity_user_movieCache:
                Intent intentCacheMovie = new Intent(UserActivity.this, CacheMovieActivity.class);
//                Intent intentCacheMovie = new Intent(UserActivity.this, WithdrawalActivity.class);
                startActivityForResult(intentCacheMovie, IvyConstants.REQUEST_CODE_MOVIE_CACHE);

                break;
            case R.id.iv_activity_user_share:
                onClickShare();
                break;
            case R.id.iv_activity_user_guide:
                onClickGuide();
                break;
            case R.id.iv_activity_user_invite_friend:
                //????????????
                mPermissionManager.requestPermissions(this, mPermissionListener, new String[]{
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS
                }, IvyConstants.PERMISSION_REQUEST_CONTACTS_INVITE);
                break;
            case R.id.iv_activity_user_setting:
                boolean isMemberShip = false;
                boolean isStockMan = false;
                boolean isOperationsManager = false;
                if (!UserInfoManager.isLogin()) {
                    showLogin();
                    return;
                }
                if (mUserBean != null) {
                    isMemberShip = mUserBean.getMembership() == 1;
                    isStockMan = mUserBean.getRole() == 1;
                    isOperationsManager = mUserBean.getIs_affiliation_leader() == 1;
                }
//                Intent intent = new Intent(this, SettingActivity.class);
//                intent.putExtra(SettingActivity.KEY_IS_MEMBERSHIP, isMemberShip);
//                intent.putExtra(SettingActivity.KEY_IS_STOCKMAN, isStockMan);
//                intent.putExtra(SettingActivity.KEY_IS_MARKETINGMAN,isOperationsManager);
//                startActivityForResult(intent, IvyConstants.REQUEST_CODE_20003);
                break;
            case R.id.iv_activity_user_myInfo_back:
            case R.id.iv_activity_user_back:
                onBackPressed();
                break;
            case R.id.iv_activity_user_focus:
                if (mUserBean == null) {
                    return;
                }
                if (mPresenter == null) {
                    return;
                }
                if (!UserInfoManager.isLogin()) {
                    showLogin();
                    return;
                }
                if (mUserBean.getIs_author_star() == 0) {//?????????
                    mPresenter.followUser(mUserBean.getId());
                } else {
                    mPresenter.unFollowUser(mUserBean.getId());
                }
                break;
            case R.id.iv_activity_user_relative:
                if (mUserBean == null) {
                    return;
                }
                if (mPresenter == null) {
                    return;
                }
                if (!UserInfoManager.isLogin()) {
                    showLogin();
                    return;
                }
                if (mUserBean.getIs_friend() == 0) {
                    mPresenter.applyRelativeFriend(mUid);
                } else if (mUserBean.getIs_friend() == 1) {
                    mPresenter.deleteRelativeFriend(mUid);
                }
                break;
            case R.id.iv_activity_user_report:
                if (!UserInfoManager.isLogin()) {
                    showLogin();
                    return;
                }
                if (mUserBean == null) {
                    return;
                }
                showUserMoreOperate(mUserBean);
                break;
            case R.id.iv_activity_user_avatar:
                if (mUserBean == null){
                    return;
                }
                if (mUid == 0 || mUid == mLoginId) {
                    if (!UserInfoManager.isLogin()) {
                        showLogin();
                        return;
                    }
//                    Intent intentProfile = new Intent(this, UserProfileActivity.class);
//                    intentProfile.putExtra(UserProfileActivity.INTENT_KEY, UserProfileActivity.FLAG_USER_PROFILE);
//                    startActivityForResult(intentProfile, IvyConstants.REQUEST_CODE_USER_UPDATE);
                } else {
//                    Intent intentPicture = new Intent(this,PictureActivity.class);
//                    intentPicture.putExtra(PictureActivity.INTENT_KEY,mUserBean.getAvatar());

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            mIvUserAvatar,getString(R.string.cover_scene_transition));

//                    startActivity(intentPicture,optionsCompat.toBundle());
                }
                break;
            case R.id.tv_activity_user_userName:
                if (mUserBean == null){
                    return;
                }
                if (mUserBean.getIs_friend() != 1){
                    //????????????????????????????????????
                    return;
                }
                if (mUid != 0 && mUid != mLoginId){
                    //Intent intentRemark = new Intent(UserActivity.this, UserRemarkActivity.class);
                   // intentRemark.putExtra(UserRemarkActivity.INTENT_KEY_UID,mUid);
                   // startActivityForResult(intentRemark,IvyConstants.REQUEST_CODE_20003);
                }
                break;
            case R.id.ll_activity_user_follow:
                //??????????????????
                if (mUid == 0 || mUid == mLoginId) {
                    if (!UserInfoManager.isLogin()) {
                        showLogin();
                        return;
                    }
//                    startActivityForResult(new Intent(this, FollowActivity.class), IvyConstants.REQUEST_CODE_20003);
                }
                break;
            case R.id.ll_activity_user_like:
                if (mUid == 0 || mUid == mLoginId) {
                    if (mUserBean != null) {
                        if (!UserInfoManager.isLogin()) {
                            showLogin();
                            return;
                        }
                        showPraiseData(mUserBean);
                    }
                }
                break;
            case R.id.ll_activity_user_collect:
                //??????????????????
                if (mUid == 0 || mUid == mLoginId) {
                    if (!UserInfoManager.isLogin()) {
                        showLogin();
                        return;
                    }
//                    startActivityForResult(new Intent(this, CollectionActivity.class), IvyConstants.REQUEST_CODE_20003);
                }
                break;
            case R.id.ll_activity_user_fans:
                //??????i?????????
                if (mUid == 0 || mUid == mLoginId) {
                    if (!UserInfoManager.isLogin()) {
                        showLogin();
                        return;
                    }
//                    startActivityForResult(new Intent(this, FansActivity.class), IvyConstants.REQUEST_CODE_20003);
                }
                break;
            case R.id.ll_activity_user_relativeFriend:
                if (mUid == 0 || mUid == mLoginId) {
                    if (!UserInfoManager.isLogin()) {
                        showLogin();
                        return;
                    }
                    mPermissionManager.requestPermissions(this, mPermissionListener, new String[]{
                            Manifest.permission.GET_ACCOUNTS,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.SEND_SMS
                    }, IvyConstants.PERMISSION_REQUEST_CONTACTS_RELATIVE_FRIEND);
                }
                break;
            case R.id.iv_activity_user_videoCall:
                if (!UserInfoManager.isLogin()) {
//                    ActivityUtils.startActivity(new Intent(UserActivity.this, LoginActivity.class));
                    return;
                }
                requestVCUserInfo(mUid, mUserBean!=null?mUserBean.getName():null,IvyConstants.REQUEST_CODE_USER_UPDATE_VIDEO_CALL);
                break;
            default:
                break;
        }
    }

    /**
     * ???????????????
     *
     * @param userBean ????????????
     */
    private void showUserMoreOperate(UserBean userBean) {
        mUserMorePopup = EasyPopup.create()
                .setContentView(this, R.layout.popup_user_more)
                //??????????????????PopupWindow?????????????????????
                .setFocusAndOutsideEnable(true)
                //??????????????????(0-1)???0???????????????
                .setDimValue(0f)
                .apply();
        if (mUserMorePopup.isShowing()) {
            return;
        }
        mUserMorePopup.showAtAnchorView(mIvUserReport, YGravity.BELOW, XGravity.ALIGN_RIGHT);
        TextView tvBlacklist = mUserMorePopup.findViewById(R.id.tv_popup_user_blacklist);
        if (userBean.getIs_blacklist() == 1) {//?????????
            tvBlacklist.setText(getString(R.string.common_cancel_blacklist));
        } else {
            tvBlacklist.setText(getString(R.string.common_add_blacklist));
        }
        tvBlacklist.setOnClickListener(v -> {
            if (mPresenter != null) {
                mPresenter.blacklistUser(mUid, userBean.getIs_blacklist());
            }
            mUserMorePopup.dismiss();
        });
        mUserMorePopup.findViewById(R.id.tv_popup_user_report).setOnClickListener(v -> {
            mUserMorePopup.dismiss();
            if (mPresenter != null) {
                mPresenter.showReportDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IvyConstants.REQUEST_CODE_USER_UPDATE:
                case IvyConstants.REQUEST_CODE_20003:
                    clearNetworkCache();
                    if (mPresenter != null) {
                        mPresenter.requestData(mUid);
                    }
                    setResult(RESULT_OK);
                    break;
                case IvyConstants.REQUEST_CODE_USER_UPDATE_VIDEO_CALL://????????????????????????
                    setResult(RESULT_OK);
                    requestVCUserInfo(mUid,mUserBean!=null?mUserBean.getName():null, IvyConstants.REQUEST_CODE_USER_UPDATE_VIDEO_CALL);
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * ??????????????????
     *
     * @param userBean
     */
    private void showPraiseData(UserBean userBean) {
        String content = String.format(getString(R.string.common_praise_total), userBean.getName(), userBean.getPraise());
        mCustomProgress.show(content,
                getString(R.string.common_praise),
                getString(R.string.common_ensure),
                v -> mCustomProgress.cancel(),
                true, null);

    }

    @Override
    public void showLogin() {
        super.showLogin();
        initUnLoginView();
        RxBus.get().send(RxBusCode.ACT_USER_UNLOGIN_VIEW);
    }

    /**
     * ???????????????????????????
     */
    private final ReportDialog.OnContentListener mContentListener = new ReportDialog.OnContentListener() {
        @Override
        public void onDismiss(DialogFragment dialog) {
            mReportDialog = null;
        }

        @Override
        public void onBtnSubmitClick(DialogFragment dialog, ReportDialog.Item item, CharSequence others) {
            dialog.dismiss();
            if (item == null) {
                return;
            }
            if (mPresenter != null) {
                mPresenter.reportUser(mUid, item.getId(), others.toString(), 1);
            }
        }
    };

    private class MainPermissionListener implements PermissionManager.PermissionListener {

        @Override
        public void doAfterGrand(String[] permission, String tag) {
            if (RECODER_TAG.equals(tag)) {

               /* if (UploadTaskManager.getInstance().hasTasks()) {
                    showToast("????????????????????????????????????????????????");
                    return;
                }*/

                //??????????????????
                Intent intent = new Intent(UserActivity.this, RecordActivity.class);
                startActivity(intent);
            } else if (IvyConstants.PERMISSION_REQUEST_CONTACTS_INVITE.equals(tag)) {
                //?????????????????????
//                ActivityUtils.startActivity(new Intent(UserActivity.this, ContactInviteActivity.class));
        //        ActivityUtils.startActivity(new Intent(UserActivity.this,ContactFriendActivity.class));
            } else if (IvyConstants.PERMISSION_REQUEST_CONTACTS_RELATIVE_FRIEND.equals(tag)) {
                //??????????????????
//                startActivityForResult(new Intent(UserActivity.this, RelativeFriendActivity.class), IvyConstants.REQUEST_CODE_20003);
            }
        }

        @Override
        public void doAfterDeniedCancel(String[] permission, String tag) {

        }

        @Override
        public void doAfterDeniedEnsure(String[] permission, String tag) {

        }
    }


    private void onClickShare() {
        mUserShareDialog = new UserShareDialog(this, platform -> IvyUtils.shareApp(getBaseContext(), platform));
        mUserShareDialog.show();
    }

    private void onClickGuide(){
        //showToast("??????????????????");
        Dialog dialog = new Dialog(this);
        final View dialogView = LayoutInflater.from(UserActivity.this)
                .inflate(R.layout.dialog_video_guidance, null);

        // customizeDialog.setTitle("?????????????????????Dialog");

        dialog.setContentView(dialogView);
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth() * 0.78); //????????????
        lp.height = (int)(display.getHeight() * 0.83); //????????????
        dialog.getWindow().setAttributes(lp);
        startVideo(dialogView);
    }

    //    ---------------RxBus???????????? start------------------
    @Subscribe(code = RxBusCode.ACT_USER_LOGOUT, threadMode = ThreadMode.MAIN)
    public void logoutEvent() {
        showToast(getString(R.string.common_quit_login));
        initUserInfoData();
    }

    @Subscribe(code = RxBusCode.ACT_USER_PUSH_LOGOUT, threadMode = ThreadMode.MAIN)
    public void pushLogoutEvent() {
        initUserInfoData();
    }

    @Subscribe(code = RxBusCode.ACT_WECHAT_LOGIN_SUCCESS, threadMode = ThreadMode.MAIN)
    public void wechatLoginEvent(String userTag) {
        initUserInfoData();
    }

    @Subscribe(code = RxBusCode.ACT_USER_LOGIN, threadMode = ThreadMode.MAIN)
    public void loginEvent() {
        initUserInfoData();
    }

    @Subscribe(code = RxBusCode.ACT_COMMENT, threadMode = ThreadMode.MAIN)
    public void userDynamicEvent() {
        KLog.e("-----------????????????????????????");
        mCommonTabLayoutUser.showDot(1);
    }

    @Subscribe(code = RxBusCode.ACT_RELATIVE_FRIEND, threadMode = ThreadMode.MAIN)
    public void userInfoEvent() {
        clearNetworkCache();
        if (!UserInfoManager.isLogin()) {
            return;
        }
        if (mUid == 0 || mUid == mLoginId) {
            KLog.e("----------???????????????????????????????????????????????????,????????????????????????");
            if (mPresenter != null) {
                mPresenter.requestData(mUid);
            }
        }
    }

    @Subscribe(code = RxBusCode.ACT_BLACKLIST_CHANGE, threadMode = ThreadMode.MAIN)
    public void blacklistChangeEvent() {
        clearNetworkCache();
        if (!UserInfoManager.isLogin()) {
            return;
        }
        if (mUid == 0 || mUid == mLoginId) {
            KLog.e("----------?????????????????????,????????????????????????");
            if (mPresenter != null) {
                mPresenter.requestData(mUid);
            }
        }
    }

    @Subscribe(code = RxBusCode.ACT_USER_PUSH_RELATIVE, threadMode = ThreadMode.MAIN)
    public void pushRelativeEvent(PushModel pushModel) {
        KLog.e("-----????????????????????????????????????");
        clearNetworkCache();
        if (pushModel != null) {
            long uid = pushModel.getId();
            if (mUid == uid && mUserBean != null) {
                //???????????????????????????
                mUserBean.setIs_friend(1);
                if (LanguageUtil.isSimplifiedChinese(this)) {
                    mIvUserRelative.setImageResource(R.mipmap.btn_quxiaoqinyou_default);
                } else {
                    mIvUserRelative.setImageResource(R.mipmap.btn_quxiaoqinyou_default_en);
                }
            }
        }
    }

    /**
     * ????????????????????????????????????
     */
    private void clearNetworkCache() {
        long cacheUid = mUid;
        if (mUid == 0 || mUid == -1) {
            cacheUid = UserInfoManager.getUid();
        }
        IvyApp.getInstance().getACache().remove(Global.cache_key_userBeanResponse + cacheUid);
    }

//    ---------------RxBus ???????????? end------------------

    /**
     * @param uid  ??????????????????id
     * @param what ????????????
     */
    private void sendBroadcast(long uid, int what) {
        Intent intent = new Intent();
        intent.setAction(Global.USER_RELATIONSHIP_CHANGE);
        intent.putExtra(Global.EXTRA_KEY_UID, uid);
        intent.putExtra(Global.EXTRA_KEY_CASE, what);
        sendBroadcast(intent);
    }

    /**
     * ?????????????????????
     */
    private void initUnLoginView() {
        mIvUserBackground.setImageDrawable(null);
        mLlLeftRootView.setVisibility(View.GONE);
        mIvVideoCall.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityUploadSuccess() {
        // ????????????
        RxBus.get().send(RxBusCode.ACT_USER_MOVIE_PUBLISH_SUCCESS);
    }

    class UserRelationshipBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long uid = intent.getLongExtra(Global.EXTRA_KEY_UID, 0);
            int caseWhat = intent.getIntExtra(Global.EXTRA_KEY_CASE, Global.CASE_UNKOWN);
            if (mUid != uid) {
                return;
            }
            boolean isZh = LanguageUtil.isSimplifiedChinese(UserActivity.this);
            switch (caseWhat) {
                case Global.CASE_FOLLOW:
                    mUserBean.setIs_author_star(1);
                    if (isZh) {
                        mIvUserFocus.setImageResource(R.mipmap.btn_yiguanzhu_normal);
                    } else {
                        mIvUserFocus.setImageResource(R.mipmap.btn_yiguanzhu_normal_en);
                    }
                    break;
                case Global.CASE_UNFOLLOW:
                    mUserBean.setIs_author_star(0);
                    if (isZh) {
                        mIvUserFocus.setImageResource(R.mipmap.ic_my_focus);
                    } else {
                        mIvUserFocus.setImageResource(R.mipmap.ic_my_focus_en);
                    }
                    break;
                case Global.CASE_ADD_FRIEND:
                    mUserBean.setIs_friend(2);
                    if (isZh) {
                        mIvUserRelative.setImageResource(R.mipmap.btn_wait_default);
                    } else {
                        mIvUserRelative.setImageResource(R.mipmap.btn_wait_default_en);
                    }
                    break;
                case Global.CASE_DEL_FRIEND:
                    mUserBean.setIs_friend(0);
                    if (isZh) {
                        mIvUserRelative.setImageResource(R.mipmap.ic_my_applyrelative);
                    } else {
                        mIvUserRelative.setImageResource(R.mipmap.en_jaiqinyou_default);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUserRelationshipBroadCast != null) {
            unregisterReceiver(mUserRelationshipBroadCast);
        }
        if (myBroadcastReceiver != null) {
            //????????????
            unregisterReceiver(myBroadcastReceiver);
        }


    }

    /**
     * ????????????
     */
    private void initGuideLayout() {
            GuidePage guidePageSecond = GuidePage.newInstance()
                    .setEverywhereCancelable(false)
                    .setLayoutRes(R.layout.view_guide_mine_second, R.id.imageView_mine_second_ignore, R.id.imageView_mine_second_know)
                    .addHighLight(mLlUserRelativeFriend, HighLight.Shape.CIRCLE, new RelativeGuide(R.layout.view_guide_mine_relative_friend, Gravity.TOP))
                    .addHighLight(mIvUserAvatar, HighLight.Shape.CIRCLE, new RelativeGuide(R.layout.view_guide_mine_avatar, Gravity.RIGHT))
                    .addHighLight(mIvMovieCache, HighLight.Shape.CIRCLE, new RelativeGuide(R.layout.view_guide_mine_movie_cache, Gravity.BOTTOM))
                    .setOnLayoutInflatedListener((view, controller) -> view.findViewById(R.id.imageView_mine_second_ignore)
                            .setOnClickListener(v -> controller.remove()));
            GuidePage guidePageFirst = GuidePage.newInstance()
                    .setEverywhereCancelable(false)
                    .setLayoutRes(R.layout.view_guide_mine_first, R.id.imageView_mine_first_ignore, R.id.imageView_mine_first_know)
                    .addHighLight(mIvUserSetting, HighLight.Shape.CIRCLE, new RelativeGuide(R.layout.view_guide_mine_setting, Gravity.BOTTOM))
                    .addHighLight(mIvUserShare, HighLight.Shape.CIRCLE, new RelativeGuide(R.layout.view_guide_mine_share, Gravity.BOTTOM))
                    .setOnLayoutInflatedListener((view, controller) -> {
                                view.findViewById(R.id.imageView_mine_first_ignore)
                                        .setOnClickListener(v -> controller.remove());
                            }
                    );
            NewbieGuide.with(UserActivity.this)
                    .setLabel("UserLabel")
                    .alwaysShow(IvyConstants.ALWAYS_SHOW)
                    .addGuidePage(guidePageFirst)
                    .addGuidePage(guidePageSecond)
                    .show();
    }


    private void startVideo(View v) {
        final CustomVideoView vv = v.findViewById(R.id.video_view_1);

        /*vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                            vv.setBackgroundColor(Color.TRANSPARENT);
                        return true;
                    }
                });*/

        View btn_finish = v.findViewById(R.id.btn_finish);
        final String uri = "android.resource://" + getPackageName() + "/" + R.raw.v_guide;
        /*????????????????????????*/
        vv.setVideoURI(Uri.parse(uri));
        vv.start();
        vv.setOnPreparedListener(mp -> {
            mp.start();
            mp.setLooping(false);
            Handler myHandler = new Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            vv.setBackgroundColor(Color.TRANSPARENT);
                        }
                    },500);
                }
            }).start();


        });
        vv.setOnCompletionListener(mp -> {

            vv.stopPlayback();
            btn_finish.setVisibility(View.VISIBLE);
        });
    }

    //?????????????????????
    public class MyBroadcastReceiver extends BroadcastReceiver {
        public MyBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            UserActivity.this.finish();
        }
    }
}
