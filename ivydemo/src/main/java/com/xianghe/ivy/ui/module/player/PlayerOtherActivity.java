package com.xianghe.ivy.ui.module.player;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.app.hubert.guide.model.RelativeGuide;
import com.blankj.utilcode.util.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonElement;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.app.IvyConstants;
import com.xianghe.ivy.app.em.EMManager;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.manager.PermissionManager;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.manager.download.ApkDownloadListener;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.UserBean;
import com.xianghe.ivy.model.VersionBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.ui.base.DownloadActivity;
import com.xianghe.ivy.ui.media.config.MediaConfig;
import com.xianghe.ivy.ui.module.main.mvp.view.activity.MainActivity;
import com.xianghe.ivy.ui.module.player.adapter.MovieAdapter;
import com.xianghe.ivy.ui.module.player.mvp.contact.ExFunContact;
import com.xianghe.ivy.ui.module.player.mvp.contact.PlayerContact;
import com.xianghe.ivy.ui.module.player.mvp.presenter.PlayerPresenter;
import com.xianghe.ivy.ui.module.player.mvp.view.fragment.CommentFragment;
import com.xianghe.ivy.ui.module.player.mvp.view.fragment.PlayerExFunFragment;
import com.xianghe.ivy.ui.module.player.widget.IvyVideoView;
import com.xianghe.ivy.utils.AnimationUtils;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.LanguageUtil;
import com.xianghe.ivy.utils.ShakeUtils;
import com.xianghe.ivy.utils.ToastUtil;
import com.xianghe.ivy.utils.ViewUtil;
import com.xianghe.ivy.weight.ClickButton;
import com.xianghe.ivy.weight.CustomProgress;
import com.xianghe.ivy.weight.circlemenu.CircleMenu;
import com.xianghe.ivy.weight.circlemenu.IMenu;
import com.xianghe.ivy.weight.layoutmanager.topgravity.TopGravityLayoutManager;
import com.xianghe.ivy.weight.layoutmanager.topgravity.TopGravitySnapHelper;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import icepick.State;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 *
 * ??????????????????????????????
 * 2019???06???11???
 *
 */
public class PlayerOtherActivity extends DownloadActivity<PlayerContact.IView, PlayerContact.IPresenter>
        implements PlayerContact.IView, View.OnClickListener, OnRefreshListener,
        OnLoadMoreListener, IvyVideoView.GestureListener {

    private static final String TAG = "PlayerActivity";

    private static final String KEY_DATA = "key_data";  // key

    private final int PAGE_SIZE = 10;

    private Context mContext;
    private final Handler mHandler = new Handler();
    private ShakeUtils mShakeUtils = null;
    private int mPage = 1;

    // ???????????? ????????????
    private SmartRefreshLayout mRefreshLayout;
    private ClassicsFooter mClassicsFooter;
    //??????????????????
    private PermissionManager.PermissionListener mPermissionListener;
    // toolbar //????????????Layout
    private View mLayoutToolbar;
    //??????????????????Layout
    private View mLayoutToolbarBG;
    //??????????????????Layout
    private  View mLayoutToolBarR;
    //????????????????????????
    private View mBtntop1;
    //?????????
    private View mBtntop2;
    //??????????????????
    private View mBtntop3;
    //VIP??????
    private View mBtntop4;
    //????????????????????????
    private View mBtntop5;

   //?????????????????????????????????
    private View mBtnGoCamera;
    //????????????????????????
    private boolean isTop1 = false;
    //????????????????????????
    private boolean isTop2 = false;
    //VIP????????????
    private boolean isTop3 = false;
    //?????????????????????
    private boolean isTop4 = false;

    //??????????????????????????????
    private boolean isTop5 = false;
    //?????????????????????????????????????????????
    private boolean isGoCamera = false;
    //??????????????????
    private int newProgress = 0;
    //??????????????????????????????
    ClickButton shareView;
    //????????????layout
    LinearLayout layout_left;
    //????????????????????????90%
    private boolean markProgress = false;
    //????????????????????????
    private boolean markV = false;

    private RelativeLayout no_video_data;


    private View mBtnToolbarBack;   // toolbar ????????????
    private ImageView mIvPortrait;  // toolbar ????????????
    private TextView mTvUserName;   // toolbar ?????????
    private TextView mTvTab;        // toolbar ??????
    private View mBtnFollowUser;    // toolbar ????????????
    private View mIvToVideoCall;    // toolbar ????????????

    // ????????????
    private RecyclerView mViewList;

    // ????????????
    private CircleMenu mMenu;       // ????????????
    private ImageView mBtnAction;   // ???????????? - action view
    private ImageView mBtnCamera;   // ???????????? - ??????
    private ImageView mBtnMine;     // ???????????? - ??????
    private ImageView mBtnHome;     // ???????????? - ??????


    // ????????????
    private PlayerExFunFragment mExFunFragment;

    private TopGravityLayoutManager mLayoutManager;
    private MovieAdapter mAdapter;

    @State
    Data mConfig = null;

    private long oldplayLength;

    /**
     * ????????????????????????????????????????????????false
     */
    private boolean unPlay;

    private RequestOptions headerOptions = new RequestOptions()
            .placeholder(R.mipmap.ic_my_head_default)   //???????????????????????????
            .error(R.mipmap.ic_my_head_default);        //??????????????????????????????

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (recyclerView.getLayoutManager() instanceof TopGravityLayoutManager) {
                    TopGravityLayoutManager layoutManager = (TopGravityLayoutManager) recyclerView.getLayoutManager();
                    int pos = layoutManager.findCurrentPosition();
                    if (pos == RecyclerView.NO_POSITION) {
                        return;
                    }
                    onMovieChange(pos);
                }
            }
        }
    };
    private IvyVideoView videoPlayer;

    @Override
    protected boolean isNeedDownUpLoad() {
        return true;
    }

    public static Intent getStartIntent(Context ctx, @NonNull Data config) {
        Intent intent = new Intent(ctx, PlayerOtherActivity.class);
        intent.putExtra(KEY_DATA, config);
        /* intent.putExtra(KEY_HIDE_MENU, false);*/
        return intent;
    }

    @Nullable
    public static Data parseData(Intent data) {
        Data result = null;
        if (data != null) {
            result = (Data) data.getSerializableExtra(KEY_DATA);
        }
        return result;
    }

    @Nullable
    @Override
    public PlayerContact.IPresenter createPresenter() {
        return new PlayerPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KLog.e(TAG, "");
        mContext = this;
        unPlay = false;
        setContentView(R.layout.ativity_video_play_other);
        mPermissionListener = new PlayerOtherActivity.MainPermissionListener();

        init(savedInstanceState);
        findView(savedInstanceState);
        initView(savedInstanceState);
        initListener(savedInstanceState);
      //  initGuideLayout();
        hideBottomUIMenu();
        // ?????????????????????movie.
        // ????????????attach???????????????, ?????????????????????.
        mViewList.post(new Runnable() {
            @Override
            public void run() {
                if (unPlay) {
                    return;
                }
                playVideoByPosition();
            }
        });


        //20190531 ??????????????????
        mPresenter.startListener();
       //????????????
        mPresenter.checkVersion();
        huanxin();
       // loadComment


    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewList.post(new Runnable() {
            @Override
            public void run() {
                if (unPlay) {
                    return;
                }
                playVideoByPosition();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.resumeMediaPlayer();
        mShakeUtils.onResume();
        if (mPresenter != null) {
            mPresenter.requestData(UserInfoManager.getUid());
        }

        //????????????????????????
        try{
            hideBottomUIMenu();
            if (UserInfoManager.isLogin()){
                mBtntop1.setVisibility(View.VISIBLE);
                mBtntop2.setVisibility(View.VISIBLE);
                mBtntop3.setVisibility(View.VISIBLE);
                mBtntop4.setVisibility(View.VISIBLE);
                mBtntop5.setVisibility(View.VISIBLE);

                if(!LanguageUtil.isSimplifiedChinese(this)){
                    mBtntop2.setVisibility(View.GONE);
                    mBtntop4.setVisibility(View.GONE);
                };
            }else{
                mBtntop1.setVisibility(View.VISIBLE);
                mBtntop2.setVisibility(View.GONE);
                mBtntop3.setVisibility(View.VISIBLE);
                mBtntop4.setVisibility(View.GONE);
                mBtntop5.setVisibility(View.GONE);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * toolbar ??????????????????
     */
    private void onClickToolbarBtnSearch(View v) {
//        startActivityForResult(new Intent(mContext, MainSearchActivity.class), IvyConstants.REQUEST_CODE_20003);
    }

    @Override
    protected void onPause() {
        mPresenter.pauseMediaPlayer();
        mShakeUtils.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mPresenter.stopListener();
        mPresenter.stopMediaPlayer();
        AnimationUtils.cancel(mChangeMovieAnimation);
        mHandler.removeCallbacks(mPlayMovieRunnable);
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        KLog.e(TAG, "newConfig = " + newConfig.toString());
    }

    protected void init(Bundle savedInstanceState) {
        Intent intent = getIntent();
        mConfig = (Data) intent.getSerializableExtra(KEY_DATA);
        Log.e("456456","" + mConfig);
        Log.e("456456","" + mConfig.getMovies() + "--" );
        Log.e("456456","" + mConfig.getMovies().size());

        if (mConfig == null ) {
            finish();
        }
        mAdapter = new MovieAdapter(mConfig.getMovies());
        //???????????????????????????
        mPage = (int) (Math.ceil(mAdapter.getItemCount() * 1f / PAGE_SIZE) + 1);

        mLayoutManager = new TopGravityLayoutManager();
       //??????????????????
        mShakeUtils = new ShakeUtils(mContext);
        //?????????dailog
        mCustomProgress = new CustomProgress(mContext);
    }

    protected void findView(Bundle savedInstanceState) {
        no_video_data  = findViewById(R.id.no_video_data);
        if(mConfig.getMovies().size() == 0){
            no_video_data.setVisibility(View.VISIBLE);
        }
        mLayoutToolbar = findViewById(R.id.layout_toolbar);
        mLayoutToolbarBG =  findViewById(R.id.layout_toolbar_bg);
        mLayoutToolBarR =  findViewById(R.id.layout_toolbar_r);
        mBtntop1 =  findViewById(R.id.btn_top_1);

        mBtntop5 =  findViewById(R.id.btn_top_5);
        mBtntop2 =  findViewById(R.id.btn_top_2);
        mBtntop3 =  findViewById(R.id.btn_top_3);
        mBtntop4 =  findViewById(R.id.btn_top_4);
        mBtnGoCamera  =  findViewById(R.id.btn_go_camera);

        mRefreshLayout = findViewById(R.id.refresh_layout);
        mClassicsFooter = findViewById(R.id.refresh_footer);

        mIvToVideoCall = findViewById(R.id.iv_toVideoCall);
        mBtnToolbarBack = findViewById(R.id.btn_toolbar_back);
        mIvPortrait = findViewById(R.id.iv_portrait);
        mTvUserName = findViewById(R.id.tv_user_name);
        mTvTab = findViewById(R.id.tv_tab);
        mBtnFollowUser = findViewById(R.id.btn_follow_user);

        mViewList = findViewById(R.id.view_list);

        mMenu = findViewById(R.id.menu);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mExFunFragment = (PlayerExFunFragment) fragmentManager.findFragmentById(R.id.fragment_ex_fun);
            shareView =  (ClickButton)findViewById(R.id.btn_share);
        layout_left = findViewById(R.id.layout_left);

    }

    protected void initView(Bundle savedInstanceState) {
        mBtnGoCamera.setVisibility(View.GONE);
        mLayoutToolBarR.setVisibility(View.GONE);
        mRefreshLayout.setEnableRefresh(false);
        mClassicsFooter.setPrimaryColor(0xff333333);

        mViewList.setAdapter(mAdapter);
        mViewList.setLayoutManager(mLayoutManager);

        // ????????????
        TopGravitySnapHelper snapHelper = new TopGravitySnapHelper();
        snapHelper.attachToRecyclerView(mViewList);

        int index = mConfig.getIndex();
        if (index != Data.INVALID_INDEX) {
            mViewList.post(new Runnable() {
                @Override
                public void run() {
                    mLayoutManager.scrollToPosition(mConfig.getIndex());
                }
            });
        }
        Log.e("898989",mAdapter.getItemCount() + "");


        try {
            CategoryMovieBean movie = mAdapter.getItem(index);
            mExFunFragment.onMovieChange(movie);
            updateUI(movie);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  init menu
        initFloatMenu();

        if (mConfig.isHideMenu()) {
            mMenu.setVisibility(View.GONE);
        } else {
            //20190602???????????????????????????
            //mMenu.setVisibility(View.VISIBLE);
            mMenu.setVisibility(View.GONE);
        }

        // ??????startCommentId != -1
        // ????????????????????????.
        if (mConfig.getStartCommentId() != -1) {
            mExFunFragment.openMenu(ExFunContact.IView.Meuns.COMMENT);
            CommentFragment commentFragment = mExFunFragment.getFragmentComment();
            commentFragment.requestRefreshComment(mConfig.getStartCommentId());
        }
    }

    protected void initListener(Bundle savedInstanceState) {
        // toolbar
      //  mLayoutToolbar.setOnClickListener(this);
        mBtntop1.setOnClickListener(this);
        mBtntop2.setOnClickListener(this);
        mBtntop3.setOnClickListener(this);
        mBtntop4.setOnClickListener(this);
        mBtntop5.setOnClickListener(this);
        mBtnGoCamera.setOnClickListener(this);
       //20190601????????????????????????
        mLayoutToolbarBG.setOnClickListener(this);
        mBtnToolbarBack.setOnClickListener(this);
        mBtnFollowUser.setOnClickListener(this);
        mIvToVideoCall.setOnClickListener(this);

        // ???????????? ????????????
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);

        // ????????????
        mViewList.addOnScrollListener(mScrollListener);

        // ????????????
        mBtnCamera.setOnClickListener(this);
        mBtnMine.setOnClickListener(this);
        mBtnHome.setOnClickListener(this);

        // ?????????
        mAdapter.setGestureListener(this);
        // ????????????
        mExFunFragment.setMenuStateListener(mSlidMenuStateListener);

    }

    /**
     * ????????????
     */
    private void initGuideLayout() {
        GuidePage guidePageThird = GuidePage.newInstance()
                .setEverywhereCancelable(false)
                .setLayoutRes(R.layout.view_guide_player_third, R.id.imageView_player_third_ignore, R.id.imageView_player_third_know)
                .addHighLight(mIvPortrait, HighLight.Shape.CIRCLE, new RelativeGuide(R.layout.view_guide_player_avatar, Gravity.BOTTOM))
                .setOnLayoutInflatedListener((view, controller) ->
                        view.findViewById(R.id.imageView_player_third_ignore)
                                .setOnClickListener((v) -> controller.remove()));

                if( mConfig.getIndex() == 0){
                    return;
                }
        if( mConfig.getMovies() == null){
            return;
        }else{
        if (UserInfoManager.isLogin() &&
                mConfig != null &&
                mConfig.getMovies() != null &&
                mConfig.getMovies().get(mConfig.getIndex()) != null &&
                mConfig.getMovies().get(mConfig.getIndex()).getUid() != UserInfoManager.getUid()) {
            guidePageThird.addHighLight(mIvToVideoCall, HighLight.Shape.OVAL, new RelativeGuide(R.layout.view_guide_player_videochat, Gravity.BOTTOM));
        }
        }
        GuidePage guidePageSecond = GuidePage.newInstance().setEverywhereCancelable(false)
                .setLayoutRes(R.layout.view_guide_player_second, R.id.imageView_player_second_ignore, R.id.imageView_player_second_know)
                .addHighLight(mExFunFragment.getBtnComment(), HighLight.Shape.CIRCLE, new RelativeGuide(R.layout.view_guide_player_comment, Gravity.RIGHT))
                .addHighLight(mExFunFragment.getBtnFollow(), HighLight.Shape.CIRCLE, new RelativeGuide(R.layout.view_guide_player_follow, Gravity.RIGHT))
                .addHighLight(mExFunFragment.getBtnDianZan(), HighLight.Shape.CIRCLE, new RelativeGuide(R.layout.view_guide_player_like, Gravity.RIGHT))
                .addHighLight(mExFunFragment.getBtnShare(), HighLight.Shape.CIRCLE, new RelativeGuide(R.layout.view_guide_player_share, Gravity.RIGHT))
                .setOnLayoutInflatedListener((view, controller) -> view.findViewById(R.id.imageView_player_second_ignore)
                        .setOnClickListener(v -> controller.remove()));
        NewbieGuide.with(this)
                .setLabel("playerLabel")
                .alwaysShow(IvyConstants.ALWAYS_SHOW)
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {
                        KLog.e("=====onShowed");
                        unPlay = true;
                    }

                    @Override
                    public void onRemoved(Controller controller) {
                        KLog.e("=====onRemoved");
                        unPlay = false;
                        mViewList.post(() -> playVideoByPosition());
                    }
                })
                .addGuidePage(addGuidePage(R.layout.view_guide_player_first, R.id.imageView_player_first_ignore, R.id.imageView_player_first_know))
                .addGuidePage(guidePageSecond)
                .addGuidePage(guidePageThird)
                .show();
    }

    /**
     * ????????????????????????
     *
     * @param layoutId ??????
     * @param ignoreId ????????????
     * @param nextId   ???????????????
     * @return
     */
    private GuidePage addGuidePage(int layoutId, int ignoreId, int nextId) {
        return GuidePage.newInstance().setEverywhereCancelable(false)
                .setLayoutRes(layoutId, ignoreId, nextId)
                .setOnLayoutInflatedListener((view, controller) ->
                        view.findViewById(ignoreId)
                                .setOnClickListener((v) -> controller.remove()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_toolbar_bg:
                //20190601??????layout_toolbar

                if(mConfig != null && mConfig.getIndex() != 0){
                    onClickLayoutToolbar();
                }

                break;
            case R.id.layout_toolbar:
                //20190601??????
               // onClickLayoutToolbar();
                break;
            case R.id.btn_toolbar_back:
                onClickToolBarBack();
                break;
            case R.id.btn_follow_user:
                onClickToolbarBtnFollowUser();
                break;
            case R.id.iv_toVideoCall:
                onClickToolbarToVideoCall();
                break;
            case R.id.btn_main_menu_camera:
                onClickMenuCamera(mMenu);
                break;
            case R.id.btn_main_menu_mine:
                onClickMenuMine(mMenu);
                break;
            case R.id.btn_main_menu_home:
                onClickMenuHome(mMenu);
                break;

            case R.id.btn_go_camera:
                if(!isGoCamera){
                    isGoCamera = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isGoCamera = false;
                        }
                    },1000);
                    onClickMenuCamera(mMenu);
                }else{
                    ToastUtil.showToast(this,getString(R.string.request_loading));
                }
                break;
            case R.id.btn_top_1:
                //??????????????????

                if(!isTop1){
                    isTop1 = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isTop1 = false;
                        }
                    },1000);
                    onClickJump2UserInfo();
                }else{
                    ToastUtil.showToast(this,getString(R.string.request_loading));
                }

                break;
            case R.id.btn_top_2:
                //???????????????
                if(!isTop2){
                    isTop2 = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isTop2 = false;
                        }
                    },1000);
                    onClickToolbarBtnQrCode(v);
                }else{
                    ToastUtil.showToast(this,getString(R.string.request_loading));
                }

                break;
            case R.id.btn_top_3:
                //????????????
                if(!isTop3){
                    isTop3 = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isTop3 = false;
                        }
                    },1000);
                    onClickToolbarBtnSearch(v);
                }else{
                    ToastUtil.showToast(this,getString(R.string.request_loading));
                }

                break;
            case R.id.btn_top_4:
                // ????????????
                if(!isTop4){
                    isTop4 = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isTop4 = false;
                        }
                    },1000);
                    onClickToolbarBtnMember(v);
                }else{
                    ToastUtil.showToast(this,getString(R.string.request_loading));
                }
                break;

            case R.id.btn_top_5:
                // ????????????
                if(!isTop5){
                    isTop5 = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isTop5 = false;
                        }
                    },1000);
                  //  onClickToolbarBtnMember(v);
                    //????????????
                    mPermissionManager.requestPermissions(this, mPermissionListener, new String[]{
                            Manifest.permission.GET_ACCOUNTS,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.READ_SMS
                    }, IvyConstants.PERMISSION_REQUEST_CONTACTS_INVITE);
                }else{
                    ToastUtil.showToast(this,getString(R.string.request_loading));
                }
                break;

        }
    }

    /**
     * ???????????????
     */
    private void onClickToolbarBtnQrCode(View v) {
        try {
//            Intent intent = new Intent(mContext, NewInvitationActivity.class);
//            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     * @param v
     */
    private void onClickToolbarBtnMember(View v) {

        if (UserInfoManager.isLogin() && UserInfoManager.getUserIsMember()){
//            Intent intent = new Intent(PlayerOtherActivity.this, MemberActivity.class);
//            Log.e("111222", String.valueOf(UserInfoManager.getUid()));
//            Log.e("111222", UserInfoManager.getString(USER_MOBILE, ""));
//            Log.e("111222",  UserInfoManager.getTicket() + "");
//            intent.putExtra("url",
//                    String.format(MEMBERURL,
//                            String.valueOf(UserInfoManager.getUid()),
//                            UserInfoManager.getString(USER_MOBILE, ""),
//                            UserInfoManager.getTicket(), "member"));
//            startActivity(intent);
        } else {
            Log.e("111222",  UserInfoManager.getTicket() + "");
//            Intent intent = new Intent(PlayerOtherActivity.this, AboutMemberActivity.class);
//            startActivity(intent);
           // startActivityForResult(intent, IvyConstants.REQUEST_CODE_20006_ABOUT_MEMBER);
        }
    }

    private void onClickLayoutToolbar() {
        if (mConfig.isMine()) {
            onClickToolBarBack();
            return;
        }
        CategoryMovieBean movieBean = mAdapter.getItem(mConfig.getIndex());
        if (movieBean != null) {
            mPresenter.jump2UserInfo(mContext, movieBean.getUid(), false);
        }
    }

    /**
     * ???????????????????????????
     */
    private void onClickJump2UserInfo(){
            mPresenter.jump2UserInfo(mContext,   UserInfoManager.getUid(), false);
    }

    private void onClickToolBarBack() {
        finishIfNeed();
    }

    private void onClickToolbarBtnFollowUser() {
        CategoryMovieBean movieBean = mAdapter.getItem(mConfig.getIndex());
        mPresenter.followUser(movieBean.getUid());
    }

    private void onClickToolbarToVideoCall() {
        if (!UserInfoManager.isLogin()) {
            showLogin();
            return;
        }
        Object tag = mIvToVideoCall.getTag();
        if (tag != null && tag instanceof CategoryMovieBean) {
            CategoryMovieBean movieBean = (CategoryMovieBean) tag;
            requestVCUserInfo(movieBean.getUid(), movieBean.getUsername(), IvyConstants.REQUEST_CODE_USER_UPDATE_VIDEO_CALL);
        }
    }

    protected void onClickMenuHome(IMenu menus) {
        menus.close(true);
        Intent intent = MainActivity.getStartIntent(mContext, false);
        startActivity(intent);
    }

    protected void onClickMenuMine(IMenu menus) {
        menus.close(true);
        mPresenter.jump2UserInfo(mContext, 0, true);
    }

    protected void onClickMenuCamera(IMenu menus) {
        menus.close(true);
        mPresenter.jump2Record(mContext);
    }

    private void finishIfNeed() {
        if (mExFunFragment.isMenuOpen()) {
            mExFunFragment.closeMenu();
            return;
        }

        setResultAndFinish();
    }


    @Override
    public void onBackPressed() {
        if (mExFunFragment.isMenuOpen()) {
            mExFunFragment.closeMenu();
            return;
        }
        setResultAndFinish();
    }

    private void setResultAndFinish() {
        Data data = new Data();
        data.setMovies(mAdapter.getDatas());

        int position = mLayoutManager.findCurrentPosition();
        KLog.w(TAG, "mConfig.setIndex = " + position);
        data.setIndex(position);

        Intent Intent = new Intent();
        Intent.putExtra(KEY_DATA, data);
        setResult(Activity.RESULT_OK, Intent);
        finish();
    }

    private Animator mChangeMovieAnimation = null;

    private void updateUI(CategoryMovieBean movieBean) {
        if (mChangeMovieAnimation != null) {
            mChangeMovieAnimation.cancel();
        }
        newProgress = 0;
        shareView.setImageDrawable(getResources().getDrawable(R.drawable.btn_icon_share_selector));


       /* mExFunFragment.findViewById(R.id.video_view);

        fragment_ex_fun
        btn_share
        icon_btn_wechat_default*/
        // toolbar ??????
        PropertyValuesHolder[] toolbarPropertyHolders1 = {

                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, mLayoutToolbarBG.getTranslationX(), -mLayoutToolbarBG.getWidth()),
                PropertyValuesHolder.ofFloat(View.ALPHA, mLayoutToolbarBG.getAlpha(), 0),
              //  PropertyValuesHolder.ofFloat(View.TRANSLATION_X, mLayoutToolbar.getTranslationX(), -mLayoutToolbar.getWidth()),
              //  PropertyValuesHolder.ofFloat(View.ALPHA, mLayoutToolbar.getAlpha(), 0),
        };

      //  Animator toolbarAnimation1 = ObjectAnimator.ofPropertyValuesHolder(mLayoutToolbar, toolbarPropertyHolders1);
        Animator toolbarAnimation1 = ObjectAnimator.ofPropertyValuesHolder(mLayoutToolbarBG, toolbarPropertyHolders1);

        toolbarAnimation1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                KLog.d(TAG, "animation = " + animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isDestroyed()) {
                    return;
                }
                KLog.w(TAG, "animation = " + animation);

                try{
                    updateUiInfo(movieBean);
                } catch (Exception e) {
                e.printStackTrace();
                }


            }
        });
        PropertyValuesHolder[] toolbarPropertyHolders2 = {
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1),
        };


        Animator toolbarAnimation2 = ObjectAnimator.ofPropertyValuesHolder(mLayoutToolbarBG, toolbarPropertyHolders2);
      //  Animator toolbarAnimation2 = ObjectAnimator.ofPropertyValuesHolder(mLayoutToolbar, toolbarPropertyHolders2);
        AnimatorSet toolbarAnimatorSet = new AnimatorSet();
        toolbarAnimatorSet.playSequentially(toolbarAnimation1, toolbarAnimation2);
        toolbarAnimatorSet.setDuration(400);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(toolbarAnimatorSet/*, bottomInfoAnimation*/);
        mChangeMovieAnimation = animatorSet;
        mChangeMovieAnimation.start();
        //20190531??????????????????
        /*ObjectAnimator animator = ObjectAnimator.ofFloat(mLayoutToolbarBG,"alpha",0,0.5f,1.0f);
        animator.setDuration(2500);
        animator.start();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mLayoutToolBarR,"alpha",0,0.5f,1.0f);
        animator1.setDuration(2500);
        animator1.start();

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(layout_left,"alpha",0,0.5f,1.0f);
        animator2.setDuration(2500);
        animator2.start();*/


        HideButton();
    }

    private void updateUiInfo(CategoryMovieBean movieBean) {
        mTvUserName.setText(movieBean.getUsername());
        mIvToVideoCall.setTag(movieBean);
        Glide.with(PlayerOtherActivity.this)
                .load(movieBean.getAvatar())
                .apply(headerOptions)
                .into(mIvPortrait);

        updateUserRelation(movieBean);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        refreshData();
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        loadMoreData(true);

    }

    private void loadMoreData(boolean formUser) {
        if (mConfig.getLoadType() == Data.LOAD_TYPE_USER_MEDIA) {
            mPresenter.loadMoreUserMediaList(formUser, mConfig.getUid(), mPage);
        } else if (mConfig.getLoadType() == Data.LOAD_TYPE_COLLECTION) {
            mPresenter.loadMoreCollectMediaList(formUser, mPage);
        } else {
            mPresenter.loadMoreCategoryList(formUser, mConfig.getCategory(), mPage);
        }
    }

    @Override
    public void refreshData() {
        mPresenter.refresh(mConfig.getCategory());
    }

    @Override
    public void finishRefresh(long delay) {
        mRefreshLayout.finishRefresh((int) delay);
    }

    @Override
    public void finishLoadMore(long delay) {
        mRefreshLayout.finishLoadMore((int) delay);
    }

    @Override
    public void refreshCategoryList(List<CategoryMovieBean> movies) {
        mPage = (int) (Math.ceil(mAdapter.getItemCount() * 1f / PAGE_SIZE) + 1);

        mRefreshLayout.finishRefresh();
        mAdapter.setDatas(movies);

        final int index = 0;
        mConfig.setIndex(index);
        mViewList.scrollToPosition(index);
        updateUiInfo(mAdapter.getItem(index));
    }

    @Override
    public void addCategoryList(List<CategoryMovieBean> movies) {
        KLog.e(TAG, "???????????? page = " + mPage + ", movies = " + (movies == null ? 0 : movies.size()));

        mPage = mPage + 1;
        mRefreshLayout.finishLoadMore();
        mAdapter.addDatas(movies);
        no_video_data.setVisibility(View.GONE);
    }

    @Override
    public void showMsg(CharSequence text) {
        if (text == null) {
            return;
        }
        ToastUtil.showToast(this, text.toString());
    }

    private void onMovieChange(int newIndex) {
        KLog.w(TAG, "onMovieChange" + newIndex);
        int oldIndex = mConfig.getIndex();
        if (newIndex == oldIndex) {
            return;
        }

        if (newIndex > oldIndex) {
            // ????????????????????????
            if (mAdapter.getItemCount() - newIndex < 5) {
                loadMoreData(false);
            }
        }

        KLog.w(TAG, "mConfig.setIndex = " + newIndex);
        mConfig.setIndex(newIndex);

        CategoryMovieBean movie = mAdapter.getItem(newIndex);
        updateUI(movie);
        mExFunFragment.onMovieChange(movie);

        if (!unPlay) {
            playVideoByPosition();
            //????????????
            long newplayLength = System.currentTimeMillis();
            if (newplayLength - oldplayLength > 5000) {
                HashMap<String, Object> params = new HashMap<>();
                CategoryMovieBean oldmovie = mAdapter.getItem(oldIndex);
                params.put("media_id", oldmovie.getId());
                params.put("action", "2");
                params.put("duration", String.valueOf((newplayLength - oldplayLength) / 1000));
                setUserAction(params);
            }
            oldplayLength = newplayLength;
        }
    }


    @Override
    public void onRight2LeftFling(View ivyVideoView, float deltaX, float deltaY, float totalMoveX, float totalMoveY) {
        CategoryMovieBean movieBean = mAdapter.getItem(mConfig.getIndex());
        if (movieBean != null) {
            mPresenter.jump2UserInfoInfoNeed(mContext, movieBean.getUid(), totalMoveX, totalMoveY, ivyVideoView.getWidth(), ivyVideoView.getHeight());
        }
    }

    @Override
    public void onDoubleTap(View ivyVideoView, MotionEvent event) {
        mExFunFragment.onClickDianZan();
    }

    @Override
    public void onLeft2RightFling(View ivyVideoView, float deltaX, float deltaY, float totalMoveX, float totalMoveY) {
    }

    @Override
    protected void onDownLoadListCollapsed() {

    }

    @Override
    protected void onDownLoadListExpanded() {

    }

    @Override
    protected void onDownLoadListDragging() {
        if (getMenuState() != IMenu.State.CLOSED) {
            collepsedMenu(true);

        }
    }

    @Override
    protected void onDownLoadListSetting() {
        if (getMenuState() != IMenu.State.CLOSED) {
            collepsedMenu(true);
        }
    }

    @Override
    protected void onDownLoadListHidden() {

    }

    @Override
    protected void onDownLoadListHalfExpanded() {

    }



    private Runnable mPlayMovieRunnable = null;

    private void playVideoByPosition() {
        if(videoPlayer != null) videoPlayer.release();

        GSYVideoType.setShowType(-4);
        mHandler.removeCallbacks(mPlayMovieRunnable);
        mPlayMovieRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "playVideoByPosition: ");
                View childAt = mLayoutManager.findViewByPosition(mLayoutManager.findCurrentPosition());
                GSYVideoType.setShowType(-4);
                if (childAt != null) {
                    videoPlayer = childAt.findViewById(R.id.video_view);


                    videoPlayer.setIfCurrentIsFullscreen(true);
                    videoPlayer.startWindowFullscreen(PlayerOtherActivity.this,false,false);
                    videoPlayer.setIfCurrentIsFullscreen(true);

                    Log.e("7894566",  "");

                    videoPlayer.setNeedLockFull(true);

                    //DLLog.d("MovieAdapter","xw????????????:"+ mAdapter.getItem(mLayoutManager.findCurrentPosition()).getTitle() +  " " +getCurrentTime());
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Host", UserInfoManager.getString(IvyConstants.CDN_URL, ""));
                    videoPlayer.setMapHeadData(headers);
                    videoPlayer.startPlayLogic();
                    videoPlayer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    videoPlayer.setVideoAllCallBack(new VideoAllCallBack() {
                        @Override
                        public void onStartPrepared(String url, Object... objects) {

                        }

                        @Override
                        public void onPrepared(String url, Object... objects) {

                        }

                        @Override
                        public void onClickStartIcon(String url, Object... objects) {

                        }

                        @Override
                        public void onClickStartError(String url, Object... objects) {

                        }

                        @Override
                        public void onClickStop(String url, Object... objects) {

                        }

                        @Override
                        public void onClickStopFullscreen(String url, Object... objects) {
                            videoPlayer.setIfCurrentIsFullscreen(true);

                            //??????????????????

                            handler.removeCallbacks(runnable);
                         /*   ObjectAnimator animator = ObjectAnimator.ofFloat(mLayoutToolBarR,"alpha",0,0.5f,1.0f);
                            animator.setDuration(1000);
                            animator.start();
                            ObjectAnimator animator1 = ObjectAnimator.ofFloat(mLayoutToolbarBG,"alpha",0,0.5f,1.0f);
                            animator1.setDuration(1000);
                            animator1.start();
                            ObjectAnimator animator2 = ObjectAnimator.ofFloat(layout_left,"alpha",0,0.5f,1.0f);
                            animator2.setDuration(1000);
                            animator2.start();*/
                        }

                        @Override
                        public void onClickResume(String url, Object... objects) {

                        }

                        @Override
                        public void onClickResumeFullscreen(String url, Object... objects) {
                            //??????????????????

                           /* ObjectAnimator animator = ObjectAnimator.ofFloat(mLayoutToolBarR,"alpha",1.0f,0.5f,0);
                            animator.setDuration(1000);
                            animator.start();
                            ObjectAnimator animator1 = ObjectAnimator.ofFloat(mLayoutToolbarBG,"alpha",1.0f,0.5f,0);
                            animator1.setDuration(1000);
                            animator1.start();
                            ObjectAnimator animator2 = ObjectAnimator.ofFloat(layout_left,"alpha",1.0f,0.5f,0);
                            animator2.setDuration(1000);
                            animator2.start();*/


                        }

                        @Override
                        public void onClickSeekbar(String url, Object... objects) {

                        }

                        @Override
                        public void onClickSeekbarFullscreen(String url, Object... objects) {

                        }

                        @Override
                        public void onAutoComplete(String url, Object... objects) {

                        }

                        @Override
                        public void onEnterFullscreen(String url, Object... objects) {

                        }

                        @Override
                        public void onQuitFullscreen(String url, Object... objects) {

                        }

                        @Override
                        public void onQuitSmallWidget(String url, Object... objects) {

                        }

                        @Override
                        public void onEnterSmallWidget(String url, Object... objects) {

                        }

                        @Override
                        public void onTouchScreenSeekVolume(String url, Object... objects) {

                        }

                        @Override
                        public void onTouchScreenSeekPosition(String url, Object... objects) {

                        }

                        @Override
                        public void onTouchScreenSeekLight(String url, Object... objects) {

                        }

                        @Override
                        public void onPlayError(String url, Object... objects) {

                        }

                        @Override
                        public void onClickStartThumb(String url, Object... objects) {


                        }

                        @Override
                        public void onClickBlank(String url, Object... objects) {

                        }

                        @Override
                        public void onClickBlankFullscreen(String url, Object... objects) {

                        }
                    });
                    videoPlayer.setGSYVideoProgressListener(new GSYVideoProgressListener() {
                        @Override
                        public void onProgress(int progress, int secProgress, int currentPosition, int duration) {


                            if(progress >= 90 && !markProgress ){
                                markProgress = true;
                                shareView.setImageDrawable(getResources().getDrawable(R.mipmap.icon_btn_wechat_default));

                              /*  ObjectAnimator animator = ObjectAnimator.ofFloat(mLayoutToolbarBG,"alpha",0,0.5f,1.0f);
                                animator.setDuration(2500);
                                animator.start();
                                ObjectAnimator animator1 = ObjectAnimator.ofFloat(mLayoutToolBarR,"alpha",0,0.5f,1.0f);
                                animator1.setDuration(2500);
                                animator1.start();

                                ObjectAnimator animator2 = ObjectAnimator.ofFloat(layout_left,"alpha",0,0.5f,1.0f);
                                animator2.setDuration(2500);
                                animator2.start();
                                shareView.setImageDrawable(getResources().getDrawable(R.mipmap.icon_btn_wechat_default));
                                ObjectAnimator  objectAnimator = ObjectAnimator.ofFloat(shareView,"rotationY",0,360);
                                objectAnimator.setDuration(3000);
                                objectAnimator.setRepeatCount(0);
                                objectAnimator.start();
                                HideButton();*/
                            }

                            newProgress = progress;
                        }
                    });


                } else {
                    Log.i(TAG, "first completely visible position:  item view is null" + childAt);
                }
            }
        };
        mHandler.postDelayed(mPlayMovieRunnable, 300);

    }

    private CustomProgress mCustomProgress;

    @Override
    public void showLoginConfirmDialog() {
        mCustomProgress.show(getString(R.string.common_tips_no_login),
                getString(R.string.common_tips_title),
                getString(R.string.common_cancel),
                getString(R.string.common_ensure),
                view -> mCustomProgress.cancel(),
                view -> {
                    mCustomProgress.cancel();
                },
                true, null);
    }

    @Override
    public void onFollowUserResult(long uid, boolean success) {
        CategoryMovieBean movieBean = mAdapter.getItem(mConfig.getIndex());
        if (movieBean == null && movieBean.getUid() != uid) {
            KLog.i(TAG, "onFollowUserResult: id ?????????. uid = " + uid + ", movie.getUid() = " + movieBean.getUid());
            return;
        }

        if (success) {
            movieBean.setIsAttention(CategoryMovieBean.IS_ATTENTION);
            updateUserRelation(movieBean);

            //??????????????????????????????????????????????????????????????????
            IvyApp.getInstance().getACache().remove(Global.cache_key_userBeanResponse + uid);
            IvyApp.getInstance().getACache().remove(Global.cache_key_userMovieResponse + uid);

            try {
                List<CategoryMovieBean> datas = mAdapter.getDatas();
                for (int i = 0; i < datas.size(); i++) {
                    CategoryMovieBean movie = datas.get(i);
                    if (movie.getUid() == uid) {
                        movie.setIsAttention(CategoryMovieBean.IS_ATTENTION);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IvyConstants.REQUEST_CODE_USER_UPDATE_VIDEO_CALL) {
            Object tag = mIvToVideoCall.getTag();
            if (tag != null && tag instanceof CategoryMovieBean) {
                CategoryMovieBean movieBean = (CategoryMovieBean) tag;
                requestVCUserInfo(movieBean.getUid(), movieBean.getUsername(), IvyConstants.REQUEST_CODE_USER_UPDATE_VIDEO_CALL);
            }
        }

        if (requestCode == IvyConstants.REQUEST_CODE_20006_ABOUT_MEMBER){
            //?????????????????????
           // UserInfoManager.putUserIsMember(true);
        }


    }

    @Override
    public void onAddRelative(long uid) {
        List<CategoryMovieBean> movies = mAdapter.getDatas();
        if (movies == null) {
            return;
        }
        for (CategoryMovieBean movie : movies) {
            if (movie.getUid() == uid) {
                movie.setIsFriend(CategoryMovieBean.IS_FRIEND);
            }
        }

        updateUiInfo(mAdapter.getItem(mLayoutManager.findCurrentPosition()));
    }

    @Override
    public void onUserRelationShipChange(long uid, int caseWhat) {
        if (uid <= 0) {
            return;
        }

        List<CategoryMovieBean> movies = mAdapter.getDatas();
        if (movies == null) {
            return;
        }

        for (CategoryMovieBean movie : movies) {
            if (movie.getUid() == uid) {
                switch (caseWhat) {
                    case Global.CASE_FOLLOW:
                        movie.setIsAttention(CategoryMovieBean.IS_ATTENTION);
                        break;
                    case Global.CASE_UNFOLLOW:
                        movie.setIsAttention(CategoryMovieBean.UN_ATTENTION);
                        break;
                    case Global.CASE_ADD_FRIEND:
                        movie.setIsFriend(CategoryMovieBean.IS_FRIEND);
                        break;
                    case Global.CASE_DEL_FRIEND:
                        movie.setIsFriend(CategoryMovieBean.UN_FRIEND);
                        break;
                }
            }
        }
        updateUiInfo(mAdapter.getItem(mLayoutManager.findCurrentPosition()));
    }

    @Override
    public void updateMyPortrait(String newUrl) {
        List<CategoryMovieBean> movies = mAdapter.getDatas();
        if (movies == null) {
            return;
        }

        long myUid = UserInfoManager.getUid();
        for (CategoryMovieBean movie : movies) {
            if (movie.getUid() == myUid) {
                movie.setAvatar(newUrl);
            }
        }

        try {
            updateUiInfo(movies.get(mLayoutManager.findCurrentPosition()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * ????????????
     * @param versionBean   ????????????
     * @param isForceUpdate ??????????????????
     */
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

    /**
     * ????????????????????????
     * @param throwable
     */
    @Override
    public void checkVersionFailed(Throwable throwable) {
        String message = throwable.getMessage();
        if (!TextUtils.isEmpty(message)) {
            KLog.e("-------errorMessage:" + message);
        }
    }

    /**
     * ??????????????????
     * @param d
     */
    @Override
    public void onRequestDataSuccess(BaseResponse<UserBean> d) {
        Log.e("123456789",d.toString());
    }

    @Override
    public void onNetworkError(Throwable throwable) {
        Log.e("123456789","----");
    }

    @Override
    public void onRequestFailed(Throwable throwable) {
        String message = throwable.getMessage();
        Log.e("123456789","----" + message);

        mBtntop1.setVisibility(View.VISIBLE);
        mBtntop2.setVisibility(View.GONE);
        mBtntop3.setVisibility(View.VISIBLE);
        mBtntop4.setVisibility(View.GONE);
        mBtntop5.setVisibility(View.GONE);
       // if (!TextUtils.isEmpty(message)) {
           // showToast(message);
       // }
    }

    private void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        ToastUtil.showToast(this, msg);
    }

    private void updateUserRelation(CategoryMovieBean movieBean) {
        if (UserInfoManager.getHxIsLogin()) {
            mIvToVideoCall.setVisibility(View.VISIBLE);
        } else {
            mIvToVideoCall.setVisibility(View.GONE);
        }
        if (movieBean.getUid() == UserInfoManager.getUid()) {
            mTvTab.setVisibility(View.GONE);
            mBtnFollowUser.setVisibility(View.GONE);
            mIvToVideoCall.setVisibility(View.GONE);
        } else if (movieBean.getIsFriend() == CategoryMovieBean.IS_FRIEND) {
            mTvTab.setText(getString(R.string.player_user_have_friend));
            mTvTab.setVisibility(View.VISIBLE);
            mBtnFollowUser.setVisibility(View.GONE);
        } else if (movieBean.getIsAttention() == CategoryMovieBean.IS_ATTENTION) {
            mTvTab.setText(getString(R.string.player_user_have_attention));
            mTvTab.setVisibility(View.VISIBLE);
            mBtnFollowUser.setVisibility(View.GONE);

        } else {
            mTvTab.setVisibility(View.GONE);
            mBtnFollowUser.setVisibility(View.VISIBLE);
        }
    }

    private PlayerExFunFragment.MenuStateListener mSlidMenuStateListener = new PlayerExFunFragment.MenuStateListener() {
        @Override
        public void onDrawerOpened() {
            if (getMenuState() != null) {
                collepsedMenu(true);
            }
        }

        @Override
        public void onDrawerClosed() {

        }
    };


    private void setUserAction(HashMap<String, Object> params) {
        NetworkRequest.INSTANCE.postMap(Api.Action.USER_ACTION, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonElement>() {
                    @Override
                    public void accept(JsonElement jsonElement) throws Exception {
                        BaseResponse response = GsonHelper.getsGson().fromJson(jsonElement, BaseResponse.class);
                        if (response.getStatus() == 1) {
                            KLog.d("????????????????????????");
                        } else {
                            KLog.d("????????????????????????");
                        }

                    }
                });
    }

    private CircleMenu.State getMenuState() {
        return mMenu == null ? CircleMenu.State.UNKNOWN : mMenu.getState();
    }

    private Runnable mMenuChangeRunnable = null;

    public void expendedMenu(boolean animated) {
        mHandler.removeCallbacks(mMenuChangeRunnable);
        mMenuChangeRunnable = new Runnable() {
            @Override
            public void run() {
                mMenu.open(animated);
            }
        };
        mHandler.postDelayed(mMenuChangeRunnable, 300);
    }

    public void collepsedMenu(boolean animated) {
        mHandler.removeCallbacks(mMenuChangeRunnable);
        mMenuChangeRunnable = new Runnable() {
            @Override
            public void run() {
                if (mMenu != null) {
                    mMenu.close(animated);
                }
            }
        };
        mHandler.postDelayed(mMenuChangeRunnable, 300);
    }

    private void initFloatMenu() {
        mBtnAction = new ImageView(this);
        mBtnCamera = new ImageView(this);
        mBtnMine = new ImageView(this);
        mBtnHome = new ImageView(this);

        mBtnCamera.setId(R.id.btn_main_menu_camera);
        mBtnMine.setId(R.id.btn_main_menu_mine);
        mBtnHome.setId(R.id.btn_main_menu_home);

        mBtnAction.setImageResource(R.drawable.btn_icon_circle_menu_collected_selector);
        mBtnCamera.setImageResource(R.drawable.btn_icon_circle_menu_camera_selector);
        mBtnMine.setImageResource(R.drawable.btn_icon_circle_menu_mine_selector);
        mBtnHome.setImageResource(R.drawable.btn_icon_circle_menu_home_selector);

        int btnSizePx = ViewUtil.dp2px(this, 60);

        mMenu.setActionItem(new CircleMenu.Item(mBtnAction, btnSizePx, btnSizePx));
        mMenu.setItems(Arrays.asList(
                new CircleMenu.Item(mBtnHome, btnSizePx, btnSizePx),
                new CircleMenu.Item(mBtnMine, btnSizePx, btnSizePx),
                new CircleMenu.Item(mBtnCamera, btnSizePx, btnSizePx)));
        mMenu.setAnchor(CircleMenu.Anchor.RIGHT_BOTTOM);
        mMenu.setAnchorOffsetX(ViewUtil.dp2px(getBaseContext(), -50));
        mMenu.setAnchorOffsetY(ViewUtil.dp2px(getBaseContext(), -45));
        mMenu.setMenuRadiusMax(ViewUtil.dp2px(getBaseContext(), 125));
        mMenu.setItemRadius(ViewUtil.dp2px(getBaseContext(), 85));
        mMenu.setMenuRadiusMin(0);
        mMenu.setStartAngle(170);
        mMenu.setSweepAngle(170);
        mMenu.setMenuColor(0xA0000000);
    }

    /**
     * ????????????
     * @param url
     */
    private void showDownloadView(String url) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // ??????ProgressDialog ??????
        progressDialog.setTitle(getResources().getString(R.string.download_prompt));
        // ??????ProgressDialog ????????????
        progressDialog.setMessage(getResources().getString(R.string.download_progress));
        // ??????ProgressDialog ?????????????????????????????????
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
                AppUtils.installApp(PlayerOtherActivity.this, absolutePath, "com.xianghe.ivy.fileprovider", 1000);
            }
        });
    }



    /**
     * ?????????????????????????????????
     */
    protected void hideBottomUIMenu() {
        //?????????????????????????????????
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    /**
     * ??????
     */
    private  void huanxin(){
            // ?????????????????????????????????????????????????????????????????????
            if (UserInfoManager.isLogin()) {
                EMManager.INSTANCE.login(UserInfoManager.getHX_userName(), UserInfoManager.getHX_password(), 0);// ??????????????????????????????????????????
            }

    }

    //6?????????????????????
    Handler handler=new Handler();
    Runnable runnable =new Runnable() {
        @Override
        public void run() {
            if(mLayoutToolBarR != null){
              /*  ObjectAnimator animator = ObjectAnimator.ofFloat(mLayoutToolBarR,"alpha",1.0f,0.5f,0);
                animator.setDuration(2500);
                animator.start();
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(mLayoutToolbarBG,"alpha",1.0f,0.5f,0);
                animator1.setDuration(2500);
                animator1.start();
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(layout_left,"alpha",1.0f,0.5f,0);
                animator2.setDuration(2500);
                animator2.start();*/


            }
        }
    };
    private void HideButton(){
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 6000);
    }

    //???????????????????????????
    private class MainPermissionListener implements PermissionManager.PermissionListener {

        @Override
        public void doAfterGrand(String[] permission, String tag) {
            if (IvyConstants.PERMISSION_REQUEST_CONTACTS_INVITE.equals(tag)) {
                //?????????????????????

            }
        }

        @Override
        public void doAfterDeniedCancel(String[] permission, String tag) {

        }

        @Override
        public void doAfterDeniedEnsure(String[] permission, String tag) {

        }
    }



}
