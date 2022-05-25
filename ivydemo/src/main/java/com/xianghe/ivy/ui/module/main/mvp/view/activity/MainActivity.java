package com.xianghe.ivy.ui.module.main.mvp.view.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.app.hubert.guide.model.RelativeGuide;
import com.blankj.utilcode.util.AppUtils;
import com.google.android.material.tabs.TabLayout;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.app.IvyConstants;
import com.xianghe.ivy.app.em.EMManager;
import com.xianghe.ivy.app.umeng.UmengManager;
import com.xianghe.ivy.constant.GlobalVariables;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.manager.download.ApkDownloadListener;
import com.xianghe.ivy.manager.download.MovieDownloadManager;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.HuoDongInfoBean;
import com.xianghe.ivy.model.VersionBean;
import com.xianghe.ivy.ui.base.FloatingMenuActivity;
import com.xianghe.ivy.ui.media.config.MediaConfig;
import com.xianghe.ivy.ui.module.main.adapter.MainPagerAdapter;
import com.xianghe.ivy.ui.module.main.mvp.MainContact;
import com.xianghe.ivy.ui.module.main.mvp.presenter.MainPresenter;
import com.xianghe.ivy.ui.module.main.mvp.view.fragment.category.AbsCategoryFragment;
import com.xianghe.ivy.ui.module.main.mvp.view.fragment.category.MainFollowFragment;
import com.xianghe.ivy.ui.module.main.mvp.view.fragment.category.MainMomentsFragment;
import com.xianghe.ivy.ui.module.main.mvp.view.fragment.category.MainRecommendFragment;
import com.xianghe.ivy.ui.module.player.PlayerActivity;
import com.xianghe.ivy.ui.module.player.mvp.contact.PlayerContact;
import com.xianghe.ivy.ui.module.record.RecordActivity;
import com.xianghe.ivy.ui.module.user.UserActivity;
import com.xianghe.ivy.ui.module.welcom.XWLauncherActivity;
import com.xianghe.ivy.utils.HuoDongHelper;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.LanguageUtil;
import com.xianghe.ivy.utils.ToastUtil;
import com.xianghe.ivy.weight.CustomProgress;
import com.xianghe.ivy.weight.circlemenu.IMenu;
import com.xianghe.ivy.weight.dialog.RankingDialog;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.xianghe.ivy.app.IvyConstants.USER_MOBILE;
import static com.xianghe.ivy.constant.Api.VIP.MEMBERURL;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;


public class MainActivity extends FloatingMenuActivity<MainContact.View, MainContact.Presenter> implements MainContact.View {
    private static final String TAG = "MainActivity lalala";

    private static final String KEY_CHECK_DOWN_UP_LOAD_TASK = "key_check_down_up_load_task";
    public static final String KEY_SHOW_RAGNKING = "key_show_ragnking";
    public static final String KEY_JOIN_JI_ZHAN = "key_join_ji_zhan";

    public static final String LIST_DATA = "data";

    private Context mContext;
    private boolean mIsCheckTask;

    private TabLayout mTabLayout;
    private View mBtnSearch;
    private ImageView mIvQRCode;
    private View mBtnRanking;
    private ImageView mIvIsMember;

    private ViewPager mViewPager;

    private MainPagerAdapter mAdapter;

    private List<String> mTitles;

    private CustomProgress mCustomProgress;

    private Intent mIntent;
    private boolean mOpenVideo;
    private PlayerContact.IView.Data mPlayerData;

    @Override
    protected boolean isNeedDownUpLoad() {
        return true;
    }

    public static Intent getStartIntent(Context context, boolean checkTask) {
        return getStartIntent(context, checkTask, false, false);
    }

    public static Intent getStartIntent(Context context, boolean checkTask, boolean showHuoDong, boolean join) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_CHECK_DOWN_UP_LOAD_TASK, checkTask);
        intent.putExtra(KEY_HIDE_MENU, false);
        intent.putExtra(KEY_SHOW_RAGNKING, showHuoDong);
        intent.putExtra(KEY_JOIN_JI_ZHAN, join);
        return intent;
    }

    @Nullable
    @Override
    public MainContact.Presenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KLog.d(TAG, "onCreate: savedInstanceState = " + savedInstanceState);
        mContext = this;
        init(savedInstanceState);
        findView(savedInstanceState);
        initView(savedInstanceState);
        initListener(savedInstanceState);

        if (mIsCheckTask) {
            GlobalVariables.showUploadDialog = true;
            expendedMenu(true);
            // 如果是启动页面进来，且登录过，就拉取环信的信息
            if (UserInfoManager.isLogin()) {
                EMManager.INSTANCE.login(UserInfoManager.getHX_userName(), UserInfoManager.getHX_password(), 0);// 登录过的用户每次进入刷新环信
            }
        }
        mPresenter.startListenEvent();
        mPresenter.checkVersion();

        //查询用户是否是会员
        if (UserInfoManager.isLogin()){
            mPresenter.getUserHomePage(UserInfoManager.getUid());
        }

        boolean joinJiZhan = mIntent.getBooleanExtra(KEY_JOIN_JI_ZHAN, false);

        if (joinJiZhan) {
            checkLogin(() -> {
                Intent intent = new Intent(mContext, RecordActivity.class);
                startActivity(intent);
            });
        } else if (mOpenVideo) {
            if (mPlayerData == null) {
                showMsg(getString(R.string.share_media_info_error));
                return;
            }
            mPlayerData.setHideMenu(false);
            Intent intentPlayer = PlayerActivity.getStartIntent(MainActivity.this, mPlayerData);
            startActivity(intentPlayer);

        }
    }

    @Override
    protected void onMenuExpandFinish() {
        super.onMenuExpandFinish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        KLog.d(TAG, "onNewIntent: intent = " + intent);
        mIntent = intent;
        if (mIntent != null) {
            mOpenVideo = mIntent.getBooleanExtra(XWLauncherActivity.SHARE_ACTION_OPEN_VIDEO, false);
            Serializable playerExtra = mIntent.getSerializableExtra(XWLauncherActivity.SHARE_PLAYER_DATA);
            if (playerExtra instanceof PlayerContact.IView.Data) {
                mPlayerData = (PlayerContact.IView.Data) playerExtra;
            }
        }
        if (mOpenVideo) {
            if (mPlayerData == null) {
                showMsg(getString(R.string.share_media_info_error));
                return;
            }
            mPlayerData.setHideMenu(false);
            Intent intentPlayer = PlayerActivity.getStartIntent(MainActivity.this, mPlayerData);
            startActivity(intentPlayer);

        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.stopListenEvent();
        super.onDestroy();
    }

    protected void init(Bundle savedInstanceState) {
        mIntent = getIntent();
        mIsCheckTask = mIntent.getBooleanExtra(KEY_CHECK_DOWN_UP_LOAD_TASK, false);
        ArrayList<CategoryMovieBean> categoryMovieBeans = (ArrayList<CategoryMovieBean>) mIntent.getSerializableExtra(LIST_DATA);
        mOpenVideo = mIntent.getBooleanExtra(XWLauncherActivity.SHARE_ACTION_OPEN_VIDEO, false);
        Serializable playerExtra = mIntent.getSerializableExtra(XWLauncherActivity.SHARE_PLAYER_DATA);
        if (playerExtra instanceof PlayerContact.IView.Data) {
            mPlayerData = (PlayerContact.IView.Data) playerExtra;
        }

        mTitles = new ArrayList<>();
        mTitles.add(getString(R.string.main_tab_label_follow));
        mTitles.add(getString(R.string.main_tab_label_recommend));
        mTitles.add(getString(R.string.main_tab_label_moments));

        MainRecommendFragment mainRecommendFragment = new MainRecommendFragment();
        if (categoryMovieBeans != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(LIST_DATA, categoryMovieBeans);
            mainRecommendFragment.setArguments(bundle);
        }
        List<AbsCategoryFragment> fragments = new ArrayList<>();
        fragments.add(new MainFollowFragment());
        fragments.add(mainRecommendFragment);
        fragments.add(new MainMomentsFragment());

        mAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);

        mCustomProgress = new CustomProgress(this);
    }

    protected void findView(Bundle savedInstanceState) {
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        mBtnSearch = findViewById(R.id.btn_search);
        mIvQRCode = findViewById(R.id.iv_qrCode);
        mBtnRanking = findViewById(R.id.btn_ranking);
        mIvIsMember = findViewById(R.id.btn_is_member);
    }

    protected void initView(Bundle savedInstanceState) {
        try {
            final boolean chinese = LanguageUtil.isSimplifiedChinese(this);

            // viewpager
            mViewPager.setAdapter(mAdapter);
            mViewPager.setOffscreenPageLimit(3);

            // tablayout
            mTabLayout.setupWithViewPager(mViewPager);
            mTabLayout.removeAllTabs();
            for (int i = 0; i < mAdapter.getCount(); i++) {
                View tabView = LayoutInflater.from(mContext).inflate(R.layout.layout_main_tab, null, false);
                TextView tvTabText = (TextView) tabView.findViewById(R.id.tv_text);
                tvTabText.setText(mTitles.get(i));

                mTabLayout.addTab(mTabLayout.newTab().setCustomView(tabView));
            }

            // 默认为 1， 默认显示 “推荐”；
            mViewPager.setCurrentItem(1);
            TabLayout.Tab tab = mTabLayout.getTabAt(1);
            setTabTextColor(tab, getResources().getColor(R.color.text_color_white));

            // 邀请二维码
            if (UserInfoManager.isLogin() && chinese) {
                showShareQrCode();
            } else {
                hideShareQrCode();
            }

            // 活动
            boolean showHuoDongPic = false;
            if (chinese) {
                List<HuoDongInfoBean> huoDongs = HuoDongHelper.get(this).getHuoDongs();
                if (huoDongs != null && huoDongs.size() > 0) {
                    final long currentTimeMillis = System.currentTimeMillis();
                    for (HuoDongInfoBean huoDong : huoDongs) {
                        if (huoDong.getActivityType() == HuoDongInfoBean.ACTIVITY_TYPE_JI_ZHAN) {
                            if (huoDong.getStartTimeByMillisecond() < currentTimeMillis && huoDong.getLastShowTimeByMillisecond() > currentTimeMillis) {
                                showHuoDongPic = true;
                            }
                            break;
                        }
                    }
                }
            }
            mBtnRanking.setVisibility(showHuoDongPic ? View.VISIBLE : View.GONE);

            //英文版、未登录不显示会员入口
            mIvIsMember.setVisibility(chinese && UserInfoManager.isLogin() ?View.VISIBLE:View.GONE);

            final boolean isShowRanking = mIntent.getBooleanExtra(KEY_SHOW_RAGNKING, false);
            if (isShowRanking) {
                showRankingDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 新手指引
     *
     * @param locationCamera
     * @param locationMine
     */
    private void initGuideLayout(int[] locationCamera, int[] locationMine, int offset) {
        GuidePage guidePageFirst = GuidePage.newInstance()
                .setEverywhereCancelable(false)
                .setLayoutRes(R.layout.view_guide_main_first, R.id.imageView_main_first_ignore, R.id.imageView_main_first_know)
                .addHighLight(new RectF(locationCamera[0] + 10, locationCamera[1], locationCamera[0] + offset - 10, locationCamera[1] + offset - 20),
                        HighLight.Shape.CIRCLE, new RelativeGuide(R.layout.view_guide_main_camera, Gravity.LEFT) {
                            @Override
                            protected void offsetMargin(MarginInfo marginInfo, ViewGroup viewGroup, View view) {
                                marginInfo.topMargin -= 150;
                                marginInfo.rightMargin -= 20;
                            }
                        })
                .addHighLight(new RectF(locationMine[0] + 10, locationMine[1], locationMine[0] + offset - 10, locationMine[1] + offset - 20),
                        HighLight.Shape.CIRCLE, new RelativeGuide(R.layout.view_guide_main_mine, Gravity.LEFT) {
                            @Override
                            protected void offsetMargin(MarginInfo marginInfo, ViewGroup viewGroup, View view) {
                                marginInfo.topMargin -= 30;
                            }
                        })
                .setOnLayoutInflatedListener((view, controller) ->
                        view.findViewById(R.id.imageView_main_first_ignore)
                                .setOnClickListener(v -> controller.remove()));
        if (UserInfoManager.isLogin() && LanguageUtil.isSimplifiedChinese(this)) {
            guidePageFirst.addHighLight(mIvQRCode, HighLight.Shape.CIRCLE, new RelativeGuide(R.layout.view_guide_main_qrcode, Gravity.BOTTOM));
        }

    }

    /**
     * 添加新手指引布局
     *
     * @param layoutId 布局
     * @param ignoreId 跳过控件
     * @param nextId   下一步控件
     * @return
     */
    private GuidePage addGuidePage(int layoutId, int ignoreId, int nextId) {
        return GuidePage.newInstance().setEverywhereCancelable(false)
                .setLayoutRes(layoutId, ignoreId, nextId)
                .setOnLayoutInflatedListener((view, controller) ->
                        view.findViewById(ignoreId)
                                .setOnClickListener((v) -> controller.remove()));
    }

    protected void initListener(Bundle savedInstanceState) {
        mIvQRCode.setOnClickListener(mClickListener);
        mBtnSearch.setOnClickListener(mClickListener);
        mBtnRanking.setOnClickListener(mClickListener);
        mIvIsMember.setOnClickListener(mClickListener);
        mTabLayout.addOnTabSelectedListener(mTabSelectedListener);
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mMenu.setStateChangeListener((menu, state) -> {
            if (state.ordinal() == IMenu.State.OPENED.ordinal()) {//菜单展开
                int[] locationCamera = new int[2];
                int[] locationMine = new int[2];
                mMenu.getItems().get(2).getView().getLocationInWindow(locationCamera);
                mMenu.getItems().get(1).getView().getLocationInWindow(locationMine);

                if (!mOpenVideo) {
                    initGuideLayout(locationCamera, locationMine, mMenu.getItems().get(2).getWidht());
                }
            } else if (state.ordinal() == IMenu.State.CLOSED.ordinal()) {//菜单关闭

            }
        });
    }

    @Override
    protected void onClickMenuCamera(IMenu menus) {
        menus.close(true);

        checkLogin(() -> {
            Intent intent = new Intent(mContext, RecordActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onClickMenuMine(IMenu menus) {
        menus.close(true);

        if (!UserInfoManager.isLogin()) {
//            startActivity(new Intent(mContext, LoginActivity.class));
            return;
        }
        // 跳转我的
        Intent intent = new Intent(mContext, UserActivity.class);
        Uri uri = Uri.parse("ivy://UserActivity?uid=0");
        intent.setData(uri);
        intent.putExtra(KEY_HIDE_MENU, false);  //首页跳转 我的 显示悬浮菜单
        startActivity(intent);
    }

    @Override
    protected void onClickMenuHome(IMenu menus) {
        menus.close(true);
    }

    /**
     * 点击二维码
     */
    private void onClickToolbarBtnQrCode(View v) {
        try {
            mPresenter.jump2ShareQrCode(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击排行榜
     */
    private void onClickToolbarBtnRanking(View v) {
        showRankingDialog();
    }

    /**
     * 点击会员
     * @param v
     */
    private void onClickToolbarBtnMember(View v) {

        if (UserInfoManager.isLogin() && UserInfoManager.getUserIsMember()){
//            Intent intent = new Intent(MainActivity.this,MemberActivity.class);
//            intent.putExtra("url",
//                    String.format(MEMBERURL,
//                            String.valueOf(UserInfoManager.getUid()),
//                            UserInfoManager.getString(USER_MOBILE, ""),
//                            UserInfoManager.getTicket(), "member"));
//            startActivity(intent);
        } else {
//            Intent intent = new Intent(MainActivity.this, AboutMemberActivity.class);
//            startActivityForResult(intent, IvyConstants.REQUEST_CODE_20006_ABOUT_MEMBER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IvyConstants.REQUEST_CODE_20006_ABOUT_MEMBER){
            //非会员成为会员
            UserInfoManager.putUserIsMember(true);
        }
    }

    private void showRankingDialog() {
        new RankingDialog().show(getSupportFragmentManager(), "");
    }

    /**
     * toolbar 搜索按钮点击
     */
    private void onClickToolbarBtnSearch(View v) {
//        startActivityForResult(new Intent(mContext, MainSearchActivity.class), IvyConstants.REQUEST_CODE_20003);
    }


    private long lastBackPressed;

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressed < 2000) {
            UmengManager.Companion.getInstance().onKillProcess(this);
            MovieDownloadManager.getInstance(mContext).stopAll();

            // 释放资源
            IvyApp.getInstance().getHandler().removeCallbacksAndMessages(null);

            super.onBackPressed();
            return;
        } else {
            ToastUtil.showToast(this,getResources().getString(R.string.common_quit_double_click));
        }

        lastBackPressed = currentTime;
    }

    @Override
    public void showFollowBadge(boolean show) {
        TabLayout.Tab tab = mTabLayout.getTabAt(0);
        showBadge(tab, show);
    }

    @Override
    public void showRecommendBadge(boolean show) {
        TabLayout.Tab tab = mTabLayout.getTabAt(1);
        showBadge(tab, show);
    }

    @Override
    public void showMomentsBadge(boolean show) {
        TabLayout.Tab tab = mTabLayout.getTabAt(2);
        showBadge(tab, show);
    }

    private void showBadge(TabLayout.Tab tab, boolean show) {
        View tabView = tab.getCustomView();
        tabView.findViewById(R.id.tv_badge).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setTabTextColor(TabLayout.Tab tab, int textColor) {
        View tabView = tab.getCustomView();
        TextView txText = tabView.findViewById(R.id.tv_text);
        txText.setTextColor(textColor);
    }

    @Override
    public int getCurrentPage() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public void refreshPage(int page) {
        mAdapter.getItem(page);

        AbsCategoryFragment fragment = mAdapter.getItem(page);
        fragment.refreshData();
    }

    @Override
    public void showUpdateView(VersionBean versionBean, boolean isForceUpdate) {
        if (isForceUpdate) {
            mCustomProgress.show(getResources().getString(R.string.common_new_version) + "\n" + versionBean.getMake_content(),
                    getString(R.string.common_version_check_title),
                    getString(R.string.common_update),
                    view -> {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            }
                            return;
                        }
//                        http://download.i-weiying.com/apk/release/ivy.apk
                        mCustomProgress.cancel();
                        showDownloadView(versionBean.getUrl());
                    },
                    false, null);
        } else {
            mCustomProgress.show(getResources().getString(R.string.common_new_version) + "\n" + versionBean.getMake_content(),
                    getString(R.string.common_version_check_title),
                    getString(R.string.common_cancel),
                    getString(R.string.common_update),
                    view -> mCustomProgress.cancel(),
                    view -> {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            }
                            return;
                        }
                        mCustomProgress.cancel();
                        showDownloadView(versionBean.getUrl());
                    },
                    false, null);
        }

    }

    @Override
    public void checkVersionFailed(Throwable throwable) {
        String message = throwable.getMessage();
        if (!TextUtils.isEmpty(message)) {
            KLog.e("-------errorMessage:" + message);
        }
    }

    private void showDownloadView(String url) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置ProgressDialog 标题
        progressDialog.setTitle(getResources().getString(R.string.download_prompt));
        // 设置ProgressDialog 提示信息
        progressDialog.setMessage(getResources().getString(R.string.download_progress));
        // 设置ProgressDialog 是否可以按退回按键取消
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setMax(100);
        File file = new File(new File(Environment.getExternalStorageDirectory(), MediaConfig.PATH_APK), "ivy_" + System.currentTimeMillis() + ".apk");
        DownloadTask task = new DownloadTask.Builder(url, file)
                .setMinIntervalMillisCallbackProcess(5)
                .setPassIfAlreadyCompleted(true)
                .build();
        task.setTag("ivyApkDownload");
        task.enqueue(new ApkDownloadListener() {
            @Override
            public void onTaskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
                progressDialog.setProgress(0);
                KLog.e("---taskStart");
            }

            @Override
            public void onProgress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
                int progress = (int) (currentOffset * 100 / totalLength);
                progressDialog.setProgress(progress);
            }

            @Override
            public void onTaskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
                KLog.e("taskEnd==");
                progressDialog.cancel();
                if (!cause.equals(EndCause.COMPLETED)) {
                    return;
                }
                File taskFile = task.getFile();
                if (taskFile == null) {
                    return;
                }
                boolean exists = taskFile.exists();
                if (!exists) {
                    return;
                }
                String absolutePath = taskFile.getAbsolutePath();
                AppUtils.installApp(MainActivity.this, absolutePath, "com.xianghe.ivy.fileprovider", 1000);
            }
        });
    }

    @Override
    public void clearPage(int pageMoments) {
        mAdapter.getItem(pageMoments).clearData();
    }

    @Override
    public void showShareQrCode() {
        mIvQRCode.setVisibility(View.VISIBLE);
        mPresenter.getInvitationCode();
    }

    @Override
    public void getInvitationFail(Throwable throwable) {
        new Handler().postDelayed(() -> {
            if (mIvQRCode.getVisibility() == View.VISIBLE) {
                mPresenter.getInvitationCode();
            }
        }, 5000);
    }

    @Override
    public void showMemberIcon() {
        mIvIsMember.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMemberIcon() {
        mIvIsMember.setVisibility(View.GONE);
    }

    @Override
    public void hideShareQrCode() {
        mIvQRCode.setVisibility(View.GONE);
    }

    @Override
    public void showMsg(String msg) {
        ToastUtil.showToast(mContext, msg);
    }

    private TabLayout.OnTabSelectedListener mTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            KLog.d(TAG, "onTabSelected");
            showBadge(tab, false);
            setTabTextColor(tab, getResources().getColor(R.color.text_color_white));
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            KLog.d(TAG, "onTabUnselected");
            setTabTextColor(tab, getResources().getColor(R.color.text_color_gray));
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            KLog.d(TAG, "onTabReselected: ");
        }
    };

    /**
     * 点击事件接口回调
     */
    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_search:
                    onClickToolbarBtnSearch(v);
                    break;
                case R.id.iv_qrCode:
                    onClickToolbarBtnQrCode(v);
                    break;
                case R.id.btn_ranking:
                    onClickToolbarBtnRanking(v);
                    break;
                case R.id.btn_is_member:
                    onClickToolbarBtnMember(v);
                    break;
            }
        }
    };



    /**
     * viewpager 监听接口回调
     */
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            KLog.d(TAG, "onPageSelected: position = " + position);
            AbsCategoryFragment fragment = mAdapter.getItem(mViewPager.getCurrentItem());
            fragment.onShow();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
