package com.xianghe.ivy.ui.module.videopush;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.greendao.GreenDaoManager;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.constant.RxBusCode;
import com.xianghe.ivy.entity.dao.MovieItemDbDao;
import com.xianghe.ivy.entity.db.MovieItemDb;
import com.xianghe.ivy.entity.db.TagsCategory;
import com.xianghe.ivy.entity.db.UploadTaskCache;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.manager.PermissionManager;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.ActivityDetailsBean;
import com.xianghe.ivy.model.HuoDongInfoBean;
import com.xianghe.ivy.model.HuoDongListBean;
import com.xianghe.ivy.model.LocationInfo;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.mvp.loadPager.BaseMVPLoadingActivity;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.ui.media.play.video.VideoPlayerUtils;
import com.xianghe.ivy.ui.module.main.mvp.ActivityContentContract;
import com.xianghe.ivy.ui.module.main.mvp.presenter.ActivityContentPresenter;
import com.xianghe.ivy.ui.module.record.RecordActivity;
import com.xianghe.ivy.ui.module.record.model.MovieEditModel;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.ui.module.videoedit.VideoEditActivity;
import com.xianghe.ivy.ui.module.videopush.adapter.MovieActorAdapter;
import com.xianghe.ivy.ui.module.videopush.adapter.MovieTagsAdapter;
import com.xianghe.ivy.ui.module.videopush.dialog.VPInputDialog;
import com.xianghe.ivy.ui.module.videopush.dialog.VideoJoinActivityDialog;
import com.xianghe.ivy.ui.module.videopush.dialog.VideoPushLocationDialog;
import com.xianghe.ivy.utils.FileUtill;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.LanguageUtil;
import com.xianghe.ivy.utils.LocationUtils;
import com.xianghe.ivy.utils.NoDoubleClickUtils;
import com.xianghe.ivy.utils.ToastUtil;
import com.xianghe.ivy.weight.ClearEditText;
import com.xianghe.ivy.weight.CustomProgress;
import com.xianghe.ivy.weight.SimpleTextWatcher;
import com.xianghe.ivy.weight.clicklistener.NoDoubleClickListener;
import com.xianghe.ivy.weight.dialog.LoadingDialog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import gorden.rxbus2.RxBus;
import icepick.State;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.xianghe.ivy.app.IvyConstants.VIDEO_EDIT;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author: ycl
 * @Date: 2018/11/6 12:22
 * @Desc:
 */
public class VideoPushActivity extends BaseMVPLoadingActivity<VideoPushContract.View, VideoPushPresenter>
        implements VideoPushContract.View, View.OnClickListener, ActivityContentContract.View {

    private SurfaceView mVpSurfaceview;
    private ImageView mVpIvBack;
    private ImageView mIVAddDirector;
    private ImageView mIVAddActor;
    private ImageView mVpIvEdit;
    private ClearEditText mVpCetTitle;
    private EditText mVpCetDesc;
    private RecyclerView mVpRecyclerView;
    private RecyclerView mVpRVDirector;
    private RecyclerView mVpRVActor;
    private TextView mVpTvLocation;
    private TextView mTVInputCount;
    private TextView mVpTvPush;
    private RadioGroup mVpRgType;
    private TextView mTvCache;
    private RelativeLayout mJoinActivityRl;
    private ImageView mJoinActivityYiwenIv;

    private VideoPlayerUtils mVideoPlayerUtils;
    private LocationManager mLocationManager;
    private VideoPushLocationDialog mVideoPushLocationDialog;
    private CustomProgress mBackProgress;

    private Toast mNoticeToast;
    private LoadingDialog mLoadingDialog;
    private boolean isSelectActivity = true;

    // ------------------------ permission start  -----------------------
    public static final String REQUEST_CODE_PERMISSIONS_1003 = "1003";// 第一次进入页面定位
    public static final String REQUEST_CODE_PERMISSIONS_1004 = "1004";// 定位+搜索
    private PermissionManager.PermissionListener mPermissionListener;
    private String[] locationNeedPermissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };
    // ------------------------ permission end  -----------------------
    // 视频路径
    private String mVideoPath = null;
    private String mFirstFramePath = null;
    private boolean mOpenAdapterWH; // 本地视频需要视频宽高比
    // tag adapter
    private List<TagsCategory> mTagsCategoryList;
    private MovieTagsAdapter mMovieTagsAdapter;
    // 导演 演员
    private static final int REQUEST_CODE_DIRECTOR_NAME = 0;
    private static final int REQUEST_CODE_ACTOR_NAME = 1;
    private MovieActorAdapter mDirectorAdapter;
    private MovieActorAdapter mActorAdapter;
    private int mDirectorRightMargin;
    private int mActorRightMargin;

    private boolean isShowNetError;

    private int mIsScreenRecord;

    @State
    MovieItemDb mMovieItemDb;

    @State
    int mFrom;

    @State
    ArrayList<MovieItemDb> mMovieItemDbs;

    @State
    boolean isPic;
    private ActivityContentPresenter mActivityPresenter;            //活动详情presenter
    private CustomProgress mLoadProgress;
    private boolean mIsChinese;


    @NotNull
    @Override
    public VideoPushPresenter createPresenter() {
        return new VideoPushPresenter();
    }

    private void initView() {
        mVpSurfaceview = (SurfaceView) findViewById(R.id.vp_surfaceview);
        mVpIvBack = (ImageView) findViewById(R.id.vp_iv_back);
        mIVAddDirector = (ImageView) findViewById(R.id.iv_add_director);
        mIVAddActor = (ImageView) findViewById(R.id.iv_add_actor);
        mVpCetTitle = (ClearEditText) findViewById(R.id.vp_cet_title);
        mVpIvEdit = (ImageView) findViewById(R.id.vp_iv_edit);
        mVpCetDesc = (EditText) findViewById(R.id.vp_cet_desc);
        mVpRecyclerView = (RecyclerView) findViewById(R.id.vp_recyclerView);
        mVpRVDirector = (RecyclerView) findViewById(R.id.vp_rv_director);
        mVpRVActor = (RecyclerView) findViewById(R.id.vp_rv_actor);
        mVpTvLocation = (TextView) findViewById(R.id.vp_tv_location);
        mVpTvPush = (TextView) findViewById(R.id.vp_tv_push);
        mTVInputCount = (TextView) findViewById(R.id.tv_input_count);
        mVpRgType = (RadioGroup) findViewById(R.id.vp_rg_type);
        mTvCache = findViewById(R.id.vp_tv_cache);
        mJoinActivityRl = findViewById(R.id.vp_join_activity_rl);
        mJoinActivityYiwenIv = findViewById(R.id.vp_join_activity_yiwen_iv);
    }

    @Override
    public void initListener() {
        mVpIvBack.setOnClickListener(this);
        mVpTvLocation.setOnClickListener(this);
        mVpTvPush.setOnClickListener(this);
        mTvCache.setOnClickListener(this);
        mJoinActivityRl.setOnClickListener(this);
        mJoinActivityYiwenIv.setOnClickListener(this);
        mVpRgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.vp_rb_private) {
                isSelectActivity = false;
            } else if (checkedId == R.id.vp_rb_friend) {
                isSelectActivity = false;
            } else if (checkedId == R.id.vp_rb_public) {
                isSelectActivity = true;
            }
            if (isSelectActivity) {
                mJoinActivityRl.setVisibility(View.VISIBLE);
            } else {
                mJoinActivityRl.setVisibility(View.GONE);
            }
            mJoinActivityRl.setSelected(isSelectActivity);
        });

        mVpCetDesc.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (TextUtils.isEmpty(s)) {
                    if (mVpIvEdit.getVisibility() == View.GONE) {
                        mVpIvEdit.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mVpIvEdit.getVisibility() == View.VISIBLE) {
                        mVpIvEdit.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                int length = s.length();
                int maxLength = 0;

                InputFilter[] filters = mVpCetDesc.getFilters();
                for (InputFilter filter : filters) {
                    if (filter instanceof InputFilter.LengthFilter) {
                        maxLength = ((InputFilter.LengthFilter) filter).getMax();
                    }
                }
                mTVInputCount.setText(length + "/" + maxLength);
                if (length >= maxLength) {
                    mTVInputCount.setTextColor(getResources().getColor(R.color.red));
                } else {
                    mTVInputCount.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        getMovieTags();
        // 权限注册
        mPermissionListener = new PermissionManager.SimplePermissionListener() {
            @Override
            public void doAfterGrand(String[] permission, String tag) {
                super.doAfterGrand(permission, tag);
                switch (tag) {
                    // 定位
                    case REQUEST_CODE_PERMISSIONS_1003:

                        break;
                    // 定位+搜索
                    case REQUEST_CODE_PERMISSIONS_1004:

                        break;
                }
            }

            @Override
            public void doAfterDeniedEnsure(String[] permission, String tag) {
                super.doAfterDeniedEnsure(permission, tag);
                if (mPermissionManager != null) {
                    mPermissionManager.requestPermissions(VideoPushActivity.this, this,
                            permission, tag, true);
                }
            }
        };
        mPermissionManager.requestPermissions(this, mPermissionListener, locationNeedPermissions, REQUEST_CODE_PERMISSIONS_1003);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initView();
        initAdapter();
        mActivityPresenter = new ActivityContentPresenter(this);
        Intent intent = getIntent();
        mIsChinese = LanguageUtil.isSimplifiedChinese(this);
        //判断来自哪里
        mMovieItemDb = (MovieItemDb) intent.getSerializableExtra("record_model");
        mMovieItemDbs = (ArrayList<MovieItemDb>) intent.getSerializableExtra("list_model");
        mFrom = intent.getIntExtra("from", 0);
        isPic = intent.getBooleanExtra("pic", false);
        if (mMovieItemDb != null) {
            mVideoPath = mMovieItemDb.getFilePath();
            mFirstFramePath = mMovieItemDb.getFilPicPath();
            mOpenAdapterWH = mMovieItemDb.getVideo_from();
            //判断是否有数据有数据的话显示数据
            setCacheData();
        } else {
            mJoinActivityRl.setSelected(isSelectActivity);
            mJoinActivityRl.setVisibility(View.VISIBLE);
        }
        KLog.i("mVideoPath: " + mVideoPath + " mFirstFramePath:" + mFirstFramePath + " mOpenAdapterWH: " + mOpenAdapterWH);

        mVideoPlayerUtils = new VideoPlayerUtils(mVpSurfaceview, mVideoPath, 1.0f);
        mVideoPlayerUtils.setSizeAdapter(mOpenAdapterWH);
        mBackProgress = new CustomProgress(this);

        //判断语言显示地图
        if (mIsChinese) {
            initLocationManager();
            getHuoDongs();
        } else {
            initLocationEnManager();
            mJoinActivityRl.setVisibility(View.GONE);
            isSelectActivity = false;
        }

        mNoticeToast = createNoticeToast();
        mLoadingDialog = new LoadingDialog(this);
        mLoadingDialog.setBackListener(new LoadingDialog.onBackCallBackListener() {
            @Override
            public void onBackCallBack() {
                showUploadVideoBack();
            }
        });
        mVpTvPush.setEnabled(true);
    }


    private void getHuoDongs() {
        Disposable str = NetworkRequest.INSTANCE.postMap(Api.Route.Index.ACTIVITY_INDEX, new HashMap<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<JsonElement, BaseResponse<HuoDongListBean>>() {
                    @Override
                    public BaseResponse<HuoDongListBean> apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, new TypeToken<BaseResponse<HuoDongListBean>>() {
                        }.getType());
                    }
                })
                .subscribe(new Consumer<BaseResponse<HuoDongListBean>>() {
                    @Override
                    public void accept(BaseResponse<HuoDongListBean> response) throws Exception {
                        if (response.getStatus() == BaseResponse.Status.OK) {
                            List<HuoDongInfoBean> huoDongs = response.getData().getList();
                            boolean showHuoDongPic =false;
                            if (huoDongs != null && huoDongs.size() > 0) {
                                final long currentTimeMillis = System.currentTimeMillis();
                                for (HuoDongInfoBean huoDong : huoDongs) {
                                    if (huoDong.getActivityType() == HuoDongInfoBean.ACTIVITY_TYPE_JI_ZHAN) {
                                        if (huoDong.getStartTimeByMillisecond() < currentTimeMillis && huoDong.getEndTimeByMillisecond() > currentTimeMillis) {
                                            showHuoDongPic = true;
                                        }
                                        break;
                                    }
                                }
                            }
                            if(showHuoDongPic){
                                mJoinActivityRl.setVisibility(View.VISIBLE);
                            }else{
                                mJoinActivityRl.setVisibility(View.GONE);
                            }
                            isSelectActivity= showHuoDongPic;
                        }
                    }
                });
    }

    private void setCacheData() {
        String video_name = mMovieItemDb.getVideo_name();
        String video_desc = mMovieItemDb.getVideo_desc();
        String video_director = mMovieItemDb.getVideo_director();
        String video_actor = mMovieItemDb.getVideo_actor();
        String location = mMovieItemDb.getLocation();
        // 1==私密收藏   2==亲友圈   3==公开发布
        int push_type = mMovieItemDb.getPush_type();
        if (mVpCetTitle != null && !TextUtils.isEmpty(video_name)) {
            mVpCetTitle.setText(video_name);
        }

        if (mVpCetDesc != null && !TextUtils.isEmpty(video_desc)) {
            mVpCetDesc.setText(video_desc);

            int maxLength = 0;
            InputFilter[] filters = mVpCetDesc.getFilters();
            for (InputFilter filter : filters) {
                if (filter instanceof InputFilter.LengthFilter) {
                    maxLength = ((InputFilter.LengthFilter) filter).getMax();
                }
            }
            mTVInputCount.setText(video_desc.length() + "/" + maxLength);
        }

        if (mVpTvLocation != null && !TextUtils.isEmpty(location)) {
            mVpTvLocation.setText(location);
        }

        if (mVpRgType != null && push_type != 0) {
            ((RadioButton) mVpRgType.getChildAt(push_type - 1)).setChecked(true);
            if (push_type == 3) {
                mJoinActivityRl.setSelected(isSelectActivity);
                mJoinActivityRl.setVisibility(View.VISIBLE);
            } else {
                isSelectActivity = false;
                mJoinActivityRl.setSelected(false);
                mJoinActivityRl.setVisibility(View.GONE);
            }
        } else {
            mJoinActivityRl.setSelected(isSelectActivity);
            mJoinActivityRl.setVisibility(View.VISIBLE);
        }


        if (!TextUtils.isEmpty(video_director)) {
            String[] split = video_director.split(",");
            mDirectorAdapter.setNewData(new ArrayList<>(Arrays.asList(split)));
            if (mDirectorAdapter.getItemCount() >= 2) {
                hideBtnAddDirector();
            }
        }

        if (!TextUtils.isEmpty(video_actor)) {
            String[] split = video_actor.split(",");
            mActorAdapter.setNewData(new ArrayList<>(Arrays.asList(split)));
            if (mActorAdapter.getItemCount() >= 8) {
                hideBtnAddActor();
            }
        }
    }

    private void initLocationManager() {
    }

    private void initLocationEnManager() {
    }

    private void initAdapter() {
        //  --  影片类别 srart -----
        mVpRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTagsCategoryList = new ArrayList<>();
        mMovieTagsAdapter = new MovieTagsAdapter(mTagsCategoryList);
        mVpRecyclerView.setAdapter(mMovieTagsAdapter);
        View retryView = View.inflate(this, R.layout.view_load_retry, null);
        TextView tv_retry = (TextView) retryView.findViewById(R.id.tv_retry);
        tv_retry.setText(getString(R.string.common_tags_category_failed));
        tv_retry.setTextColor(ContextCompat.getColor(this, R.color.white));
        tv_retry.setBackgroundResource(R.drawable.shape_rectangle_soild_4dffff_c6dp);
        retryView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(@NotNull View v) {
                isShowNetError = true;
                getMovieTags();
            }
        });
        mMovieTagsAdapter.setEmptyView(retryView);
        mMovieTagsAdapter.bindToRecyclerView(mVpRecyclerView);
        mMovieTagsAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            List<TagsCategory> data = adapter.getData();
            KLog.i("data: " + data.size() + " " + position);
            if (position >= 0 && position < data.size()) {
                Object obj = data.get(position);
                if (null != obj && obj instanceof TagsCategory) {
                    TagsCategory tagsCategory = (TagsCategory) obj;
                    tagsCategory.setSelected(!tagsCategory.isSelected());
                    adapter.notifyItemChanged(position);
                }
            }
        });
        //  --  影片类别 end -----

        //  --  导演 start -----
        List<String> directors = mPresenter.getCacheDirectors(UserInfoManager.getUid());
        mDirectorAdapter = new MovieActorAdapter(directors) {
            @Override
            public void onClick(int pos, String content) {
                mVpCetTitle.clearFocus();
                mVpCetDesc.clearFocus();
                VPInputDialog.from(mContext)
                        //.setText(content)
                        .setHint(mContext.getString(R.string.dialog_cast_list_director_name))
                        .setTag(pos)
                        .show(VideoPushActivity.this, REQUEST_CODE_DIRECTOR_NAME);
            }

            @Override
            public void remove() {
                if (mDirectorAdapter != null && mDirectorAdapter.getData().size() < 2 && mIVAddDirector.getVisibility() == View.GONE) {
                    showBtnAddDirector();
                }
            }
        };
        mVpRVDirector.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mVpRVDirector.setAdapter(mDirectorAdapter);
        mIVAddDirector.setOnClickListener(v -> {
            if (mDirectorAdapter != null && mDirectorAdapter.getData().size() >= 2) {
                showNoticeToast(getString(R.string.common_director_less_than_2));
                return;
            }
            mVpCetTitle.clearFocus();
            mVpCetDesc.clearFocus();
            VPInputDialog.from(mContext)
                    .setHint(mContext.getString(R.string.dialog_cast_list_director_name))
                    .show(VideoPushActivity.this, REQUEST_CODE_DIRECTOR_NAME);
        });
        mVpRVDirector.scrollToPosition(mDirectorAdapter.getItemCount() - 1);

        if (mDirectorAdapter.getItemCount() >= 2) {
            hideBtnAddDirector();
        }

        //  --  导演 end -----

        //  --  演员 start -----
        List<String> actors = mPresenter.getCacheActors(UserInfoManager.getUid());
        mActorAdapter = new MovieActorAdapter(actors) {
            @Override
            public void onClick(int pos, String content) {
                mVpCetTitle.clearFocus();
                mVpCetDesc.clearFocus();
                VPInputDialog.from(mContext)
                        //.setText(content)
                        .setHint(mContext.getString(R.string.dialog_cast_list_casts_name))
                        .setTag(pos)
                        .show(VideoPushActivity.this, REQUEST_CODE_ACTOR_NAME);
            }

            @Override
            public void remove() {
                if (mActorAdapter != null && mActorAdapter.getData().size() < 8 && mIVAddActor.getVisibility() == View.GONE) {
                    showBtnAddActor();
                }
            }
        };
        mVpRVActor.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mVpRVActor.setAdapter(mActorAdapter);
        mIVAddActor.setOnClickListener(v -> {
            if (mActorAdapter != null && mActorAdapter.getData().size() >= 8) {
                showNoticeToast(getString(R.string.common_actor_less_than_8));
                return;
            }


            mVpCetTitle.clearFocus();
            mVpCetDesc.clearFocus();
            VPInputDialog.from(mContext)
                    .setHint(mContext.getString(R.string.dialog_cast_list_casts_name))
                    .show(VideoPushActivity.this, REQUEST_CODE_ACTOR_NAME);
        });
        mVpRVActor.scrollToPosition(mActorAdapter.getItemCount() - 1);
        if (mActorAdapter.getItemCount() >= 8) {
            hideBtnAddActor();
        }
        //  --  演员 end -----
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_DIRECTOR_NAME:
                    InputResult(data, mVpRVDirector, mDirectorAdapter, 2);
                    break;
                case REQUEST_CODE_ACTOR_NAME:
                    InputResult(data, mVpRVActor, mActorAdapter, 8);
                    break;
            }
        }
    }

    private void InputResult(Intent data, RecyclerView recyclerView, MovieActorAdapter adapter, int limit) {
        String inputText = VPInputDialog.parseResult(data).trim();
        if (TextUtils.isEmpty(inputText)) {
            showNoticeToast(getString(R.string.common_input_content_is_empty));
            return;
        }

        Serializable tag = VPInputDialog.parseTag(data);
        if (tag != null && tag instanceof Integer) {
            int pos = (int) tag;
            if (adapter != null) {
                adapter.setData(pos, inputText);
            }
            return;
        }

        if (adapter != null) {
            adapter.addData(inputText);

            if (limit == 2 && adapter.getData().size() >= 2 && mIVAddDirector.getVisibility() == View.VISIBLE) {
                hideBtnAddDirector();
            } else if (limit == 8 && adapter.getData().size() >= 8 && mIVAddActor.getVisibility() == View.VISIBLE) {
                hideBtnAddActor();
            }
        }
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(adapter.getData().size());
        }
    }

    @Override
    public String getFirstFilPicPath() {
        return mFirstFramePath;
    }

    private void getMovieTags() {
        if (mPresenter != null) {
            mPresenter.getMovieTags();
        }
    }

    private void showUploadVideoBack() {
        if (mBackProgress != null) {
            mBackProgress.show(getString(R.string.video_edit_upload_video_back),
                    getString(R.string.common_tips_title),
                    getString(R.string.common_cancel),
                    getString(R.string.common_ensure),
                    v -> mBackProgress.dismiss(), v -> {
                        mBackProgress.dismiss();
                        if (mPresenter != null) {
                            mPresenter.cancleAllRequest();
                            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                                mLoadingDialog.dismiss();
                            }
                        }
                    }, true, dialog -> mBackProgress.dismiss());
        }
    }


    private void dismissBackDialog() {
        if (mBackProgress != null && mBackProgress.isShowing()) {
            mBackProgress.dismiss();
        }
    }

    private void showLocationDialog(List<LocationInfo> list) {
        //筛选小于20的地址数据
        mVideoPushLocationDialog = new VideoPushLocationDialog(this, choiceListInfo(list),
                locationInfo -> {
                    KLog.i("locationInfo: " + locationInfo.getTitle());
                    mVpTvLocation.setText(locationInfo.getTitle());
                    mVpTvLocation.setTag(locationInfo);
                });
        mVideoPushLocationDialog.show();
    }

    private ArrayList<LocationInfo> choiceListInfo(List<LocationInfo> list) {
        ArrayList<LocationInfo> locationInfos = new ArrayList<>();
        for (LocationInfo locationInfo : list) {
            //添加小于等于20的地址
            if (locationInfo.getTitle().length() <= 20) {
                locationInfos.add(locationInfo);
            }
        }
        return locationInfos;
    }

    private void showBackNotice() {
        if (mNoticeToast != null) {
            mNoticeToast.cancel();
        }
        //判断是不是缓存来的
        if (mFrom == RecordActivity.CACHE) {
            Intent intent = new Intent(this, VideoEditActivity.class);
            ArrayList<MovieItemDb> listModel = getListModel();
            intent.putExtra("record_model", listModel);
            intent.putExtra("edit_model", getEditMode(listModel));
            intent.putExtra("from", mFrom);
            intent.putExtra("pic", isPic);
            //删除当前的数据库数据和文件
            clearDb();
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.putExtra("record_model", mMovieItemDb);
            RxBus.get().send(RxBusCode.ACT_VIDEO_PUSH_REFRESH_BACK, intent);
        }

        finish();
    }

    /*
     *草稿箱部分---------------------------------------------------------------------------------------
     **/
    private MovieEditModel getEditMode(ArrayList<MovieItemDb> movieItemDbArrayList) {
        String videoFile = FileUtill.createSaveFile(mContext, FileUtill.getDefaultDir(mContext) + "/clip");
        if (movieItemDbArrayList != null && movieItemDbArrayList.size() > 0) {
            ArrayList<MovieItemModel> movieItemModels = initRecordList(new ArrayList<>(), movieItemDbArrayList);
            return new MovieEditModel(videoFile, movieItemModels);
        }
        return new MovieEditModel(videoFile, new ArrayList<>());
    }

    private ArrayList<MovieItemModel> initRecordList(ArrayList<MovieItemModel> movieItemModels, ArrayList<MovieItemDb> movieItemDaoList) {
        if (movieItemDaoList != null && movieItemDaoList.size() > 0) {
            for (MovieItemDb movieItemDb : movieItemDaoList) {
                movieItemModels.add(new MovieItemModel(movieItemDb.getFilePath(), BigDecimal.valueOf(movieItemDb.getVideoTime()), movieItemDb.getFilPicPath()));
            }
        }
        return movieItemModels;
    }

    private ArrayList<MovieItemDb> getListModel() {
        if (mMovieItemDbs != null && mMovieItemDbs.size() > 0) {
            MovieItemDbDao movieItemDao = GreenDaoManager.getInstance().getMovieItemDao();
            for (MovieItemDb movieItemDb : mMovieItemDbs) {
                movieItemDb.setFrom(VIDEO_EDIT);
                movieItemDao.update(movieItemDb);
            }
        }
        return mMovieItemDbs;
    }

    private void clearDb() {
        if (mMovieItemDb != null) {
            MovieItemDbDao movieItemDao = GreenDaoManager.getInstance().getMovieItemDao();
            String filePath = mMovieItemDb.getFilePath();
            File file = new File(filePath);
            Log.e("发布页合成视频后的文件名===", mMovieItemDb.getFilePath());
            if (file.exists()) {
                file.delete();
            }
            movieItemDao.delete(mMovieItemDb);
        }
    }

    @NotNull
    @Override
    public Object onCreateContentView(@Nullable Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey(Global.PARAMS)) {
                mVideoPath = bundle.getString(Global.PARAMS);
            }
            if (bundle.containsKey(Global.PARAMS1)) {
                mFirstFramePath = bundle.getString(Global.PARAMS1);
            }
            if (bundle.containsKey(Global.PARAMS2)) {
                mOpenAdapterWH = bundle.getBoolean(Global.PARAMS2);
            }
        }
        return R.layout.activity_video_push;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            if (mVideoPath != null && !TextUtils.isEmpty(mVideoPath)) {
                outState.putString(Global.PARAMS, mVideoPath);
            }
            if (mFirstFramePath != null && !TextUtils.isEmpty(mFirstFramePath)) {
                outState.putString(Global.PARAMS1, mFirstFramePath);
            }
            outState.putBoolean(Global.PARAMS2, mOpenAdapterWH);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoPlayerUtils != null) {
            mVideoPlayerUtils.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoPlayerUtils != null) {
            mVideoPlayerUtils.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNoticeToast != null) {
            mNoticeToast.cancel();
        }
        if (mVideoPushLocationDialog != null && mVideoPushLocationDialog.isShowing()) {
            mVideoPushLocationDialog.dismiss();
        }
        dismissBackDialog();
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        if (mVideoPlayerUtils != null) {
            mVideoPlayerUtils.destroy();
        }
/*        if (mLocationManager != null) {
            mLocationManager.destroy();
        }
        if (mLocationEnManager != null) {
            mLocationEnManager.stopLocation();
        }*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showBackNotice();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public <E> void onError(@Nullable E e) {
        super.onError(e);
        mVpTvPush.setEnabled(true);
        dismissLoading();
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vp_iv_back: // back
                NoDoubleClickUtils.isDoubleClick(() -> {
                    showBackNotice();
                });
                break;
            case R.id.vp_tv_location:// location
                NoDoubleClickUtils.isDoubleClick(() -> {
                    if (!LocationUtils.isLocationEnabled(getApplicationContext())) {
                        showPremissionLocation();
                        return;
                    }
                    mPermissionManager.requestPermissions(VideoPushActivity.this, mPermissionListener, locationNeedPermissions, REQUEST_CODE_PERMISSIONS_1004);
                });
                break;
            case R.id.vp_tv_push: // push
                NoDoubleClickUtils.isDoubleClick(1500, () -> {
                    try {
                        onClickBtnPush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                break;
            case R.id.vp_tv_cache:
                //发送通知关闭之前的页面
                //关闭页面
                //记录数据到数据库
                saveToCache();
                break;

            case R.id.vp_join_activity_rl:                  //是否选中参与活动
                mJoinActivityRl.setSelected(isSelectActivity = !isSelectActivity);
                break;

            case R.id.vp_join_activity_yiwen_iv:            //点击参与活动的问号--详情
                mLoadProgress = new CustomProgress(this);
                mLoadProgress.show(getResources().getString(R.string.signature_loading), false, null);
//                活动类型(不传返回所有有效的活动，1：邀请活动;2:集赞活动)
                mActivityPresenter.getContentDetails(this, 2);
                break;

        }
    }

    private void saveToCache() {
        if (mVpCetTitle != null && mVpCetDesc != null && mMovieTagsAdapter != null && mDirectorAdapter != null && mActorAdapter != null &&
                mVpTvLocation != null && mVpRgType != null) {
            String vpCet = Objects.requireNonNull(mVpCetTitle.getText()).toString().trim();
            String vpDesc = Objects.requireNonNull(mVpCetDesc.getText()).toString().trim();
            String location = Objects.requireNonNull(mVpTvLocation.getText()).toString().trim();

            StringBuilder stringBuilder = new StringBuilder();
            List<TagsCategory> data = mMovieTagsAdapter.getData();
            if (data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    TagsCategory tagsCategory = data.get(i);
                    if (tagsCategory.isSelected()) {
                        stringBuilder.append(tagsCategory.getTags_name());
                    }
                    if (i < data.size() - 1) {
                        stringBuilder.append(",");
                    }
                }
            }

            String dirString = mDirectorAdapter.getAppendItem();

            String actString = mActorAdapter.getAppendItem();

            int privateType = 3;
            switch (mVpRgType.getCheckedRadioButtonId()) {
                case R.id.vp_rb_private:
                    privateType = 1;
                    break;
                case R.id.vp_rb_friend:
                    privateType = 2;
                    break;
                case R.id.vp_rb_public:
                    privateType = 3;
                    break;
            }

            //更新数据库
            if (mMovieItemDb != null) {
                MovieItemDbDao movieItemDao = GreenDaoManager.getInstance().getMovieItemDao();
                mMovieItemDb.setVideo_name(vpCet);
                mMovieItemDb.setVideo_desc(vpDesc);
                mMovieItemDb.setLocation(location);
                mMovieItemDb.setVideo_types(stringBuilder.toString());
                mMovieItemDb.setVideo_director(dirString);
                mMovieItemDb.setVideo_actor(actString);
                mMovieItemDb.setPush_type(privateType);
                movieItemDao.update(mMovieItemDb);
                RxBus.get().send(RxBusCode.ACT_VIDEO_PUSH_REFRESH_CACHE);
                finish();
            }
        }
    }

    private synchronized void onClickBtnPush() {
        if (!mVpTvPush.isEnabled()) {
            KLog.d("拦截事件 isEnabled false");
            return;
        }
        if (mVideoPath == null || TextUtils.isEmpty(mVideoPath)) {
            showNoticeToast(getString(R.string.re_enter_the_current_page));
            return;
        }
        if (!UserInfoManager.isLogin()) {
            showLogin();
            return;
        }
        String title = mVpCetTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            showNoticeToast(getString(R.string.common_media_name_is_empty));
            return;
        }
        if (title.length() > 16) {
            showNoticeToast(getString(R.string.common_media_name_length_less_than_16));
            return;
        }
        String desc = mVpCetDesc.getText().toString().trim();
        if (desc.length() > 50) {
            showNoticeToast(getString(R.string.common_media_intro_less_than_50));
            return;
        }

        if (mMovieTagsAdapter == null || mMovieTagsAdapter.getData() == null) {
            showNoticeToast(getString(R.string.common_no_tags_category));
            return;
        }
        StringBuilder movieTypeString = new StringBuilder();
        for (TagsCategory tagsCategory : mMovieTagsAdapter.getData()) {
            if (tagsCategory.isSelected()) {
                movieTypeString.append(tagsCategory.getTid());
                movieTypeString.append(",");
            }
        }
        if (movieTypeString.length() == 0) {
            showNoticeToast(getString(R.string.common_select_media_tags_category));
            return;
        }

        String director = mDirectorAdapter.getAppendItem();
        String player = mActorAdapter.getAppendItem();
        if (TextUtils.isEmpty(director) && !TextUtils.isEmpty(player)) {
            showNoticeToast(getString(R.string.common_input_director));
            return;
        } else if (!TextUtils.isEmpty(director) && TextUtils.isEmpty(player)) {
            showNoticeToast(getString(R.string.common_input_actor));
            return;
        }

        // 上传操作
        String tag = movieTypeString.substring(0, movieTypeString.length() - 1);

        // 是否录屏视频
        mIsScreenRecord = mMovieItemDb != null ? mMovieItemDb.getIsScreenRecord() : 0;

        String location = mVpTvLocation.getText().toString().trim();
        String province = "";
        String city = "";
        // location不为空时候，必须传入province，city，否则不需要传入
        if (null != location && !TextUtils.isEmpty(location)) {
            if (getString(R.string.common_no_open_location).equals(location)) {
                location = "";
            } else if (mVpTvLocation.getTag() != null && mVpTvLocation.getTag() instanceof LocationInfo) {
                province = ((LocationInfo) mVpTvLocation.getTag()).getProvince();
                city = ((LocationInfo) mVpTvLocation.getTag()).getCity();

                province = province == null ? "" : province;
                city = city == null ? "" : city;
            }
        }
        String privateType = "0";
        switch (mVpRgType.getCheckedRadioButtonId()) {
            case R.id.vp_rb_private:
                privateType = "1";
                break;
            case R.id.vp_rb_friend:
                privateType = "2";
                break;
            case R.id.vp_rb_public:
                privateType = "0";
                break;
        }
        // 是否参与活动(0:未参与(默认);1:参与)，公开的视频
        int is_participate_activity = isSelectActivity ? 1 : 0;

        KLog.i("title: " + title + " tag: " + tag +
                " desc: " + desc + " director: " + director + " player: " + player +
                " location: " + location + " province: " + province + " city: " + city +
                " privateType: " + privateType + "isSelectActivity==" + isSelectActivity);
        if (mPresenter != null) {
            mVpTvPush.setEnabled(false);
            if (mLoadingDialog != null) mLoadingDialog.show();
            mPresenter.getMediaUpload(mVideoPath, title, desc,
                    director, player, location,
                    province, city, privateType,
                    tag, mIsScreenRecord, is_participate_activity);
        }
    }

    private void showPremissionLocation() {
        if (mCustomProgress == null) {
            mCustomProgress = new CustomProgress(this);
        }
        mCustomProgress.show(getString(R.string.iscan_to_openlocation),
                getString(R.string.common_tips_title),
                getString(R.string.common_cancel),
                getString(R.string.to_open_location),
                v -> mCustomProgress.dismiss(), v -> {
                    mCustomProgress.dismiss();
                    LocationUtils.toLocationSetting(this);
                }, true, dialog -> mCustomProgress.dismiss());
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
    public void onMovieTagSuccess(@NotNull List<? extends TagsCategory> data) {
        if (mTagsCategoryList != null) {
            mTagsCategoryList.clear();
            if (!data.isEmpty()) {
                mTagsCategoryList.addAll(data);
                //添加选中的条目
                setSelectItem();
            } else {
                if (isShowNetError) {
                    ToastUtil.showToast(this, getString(R.string.common_network_not_found));
                }
            }
        }
        if (mMovieTagsAdapter != null) {
            mMovieTagsAdapter.notifyDataSetChanged();
        }
    }

    private void setSelectItem() {
        if (mMovieItemDb != null && mTagsCategoryList != null && mTagsCategoryList.size() > 0) {
            String video_types = mMovieItemDb.getVideo_types();
            if (!TextUtils.isEmpty(video_types)) {
                //先切割字符串
                String[] split = video_types.split(",");
                //遍历集合判断是否存在
                for (int i = 0; i < mTagsCategoryList.size(); i++) {
                    String tags_name = mTagsCategoryList.get(i).getTags_name();
                    TagsCategory tagsCategory = mTagsCategoryList.get(i);
                    for (int j = 0; j < split.length; j++) {
                        if (tags_name.equals(split[j])) {
                            tagsCategory.setIsSelected(true);
                            break;
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onStartVideoUpload(@NotNull String mediaId, @NotNull String videoPath, @NotNull String imagePath, @NotNull String title,
                                   @NotNull String description, @NotNull String director, @NotNull String player, @NotNull String location) {
        // 发布之后，释放资源，关闭前几个界面
        if (mVideoPlayerUtils != null) {
            mVideoPlayerUtils.stop();
        }
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }

        // 关闭前几个录制界面
        RxBus.get().send(RxBusCode.ACT_FINISH_VIDEO_RECORD, "true");
        RxBus.get().send(RxBusCode.ACT_FINISH_VIDEO_EDIT, "true");

        //删除拍摄的文件
        removeRecordFile();

        finish();

        // 删除所有本地资源 - 除了合成的视频,临时图片除外 ，正在上传的任务也不能删除其文件
//        List<String> paths = UploadTaskManager.getInstance().getTasksFilePathLists();
//        paths.add(imagePath);
//        paths.add(mVideoPath);
//        KLog.i("paths: " + paths.toString());
//        FileUtils.deleteFilesInRecordExpertFilter(paths);

        // 3 . 上传视频文件
        /*UploadTaskCache task = UploadTaskCache.build(mediaId, videoPath, UploadManager.uploadType.VIDEO, title, imagePath,
                description, director, player, location);
        UploadTaskManager.getInstance().addTask(task);
        UploadTaskManager.getInstance().startTask(task);
*/
        mVpTvPush.setEnabled(true);
    }

    private void removeRecordFile() {
        MovieItemDbDao movieItemDao = GreenDaoManager.getInstance().getMovieItemDao();
        //查询数据库中是否还有这个文件
        List<MovieItemDb> list = movieItemDao.queryBuilder()
                .orderDesc(MovieItemDbDao.Properties.Key)
                .where(MovieItemDbDao.Properties.Uid.eq(String.valueOf(UserInfoManager.getUid()))).list();
        //删除文件以及移除数据库
        if (mMovieItemDbs != null && mMovieItemDbs.size() > 0) {
            for (int i = 0; i < mMovieItemDbs.size(); i++) {
                MovieItemDb movieItemDb = mMovieItemDbs.get(i);
                if (!list.contains(movieItemDb)) {
                    deleteCacheFile(movieItemDb, true, true);
                }
            }
        }

        movieItemDao.deleteInTx(mMovieItemDbs);
        movieItemDao.delete(mMovieItemDb);
    }

    private void hideBtnAddDirector() {
        ViewGroup.LayoutParams params = mVpRVDirector.getLayoutParams();
        if (params != null && params instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) params;
            mDirectorRightMargin = p.rightMargin;
            p.rightMargin = 0;
            mVpRVDirector.setLayoutParams(p);
        }
        mIVAddDirector.setVisibility(View.GONE);
    }

    private void showBtnAddDirector() {
        ViewGroup.LayoutParams params = mVpRVDirector.getLayoutParams();
        if (params != null && params instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) params;
            p.rightMargin = mDirectorRightMargin;
            mVpRVDirector.setLayoutParams(p);
        }
        mIVAddDirector.setVisibility(View.VISIBLE);
    }

    private void hideBtnAddActor() {
        ViewGroup.LayoutParams params = mVpRVActor.getLayoutParams();
        if (params != null && params instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) params;
            mActorRightMargin = p.rightMargin;
            p.rightMargin = 0;
            mVpRVActor.setLayoutParams(p);
        }
        mIVAddActor.setVisibility(View.GONE);
    }

    private void showBtnAddActor() {
        ViewGroup.LayoutParams params = mVpRVActor.getLayoutParams();
        if (params != null && params instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) params;
            p.rightMargin = mActorRightMargin;
            mVpRVActor.setLayoutParams(p);
        }
        mIVAddActor.setVisibility(View.VISIBLE);
    }

    private void deleteCacheFile(Object o, boolean needDelete, boolean deleteFirst) {
        if (o instanceof MovieItemDb) {
            //判断是否是本地视频
            if (!((MovieItemDb) o).getVideo_from()) {
                String filePath = ((MovieItemDb) o).getFilePath();
                //因为封面图是当前第一个视频的图片所以不能删除
                if (!TextUtils.isEmpty(filePath)) {
                    File file = new File(filePath);

                    if (file.exists()) {
                        file.delete();
                    }
                }

                if (needDelete) {
                    String filPicPath = ((MovieItemDb) o).getFilPicPath();
                    if (!TextUtils.isEmpty(filPicPath)) {
                        File file = new File(filPicPath);

                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
            }
        } else if (o instanceof ArrayList) {
            //获取第一个数据再判断
            for (int i = 0; i < ((ArrayList) o).size(); i++) {
                Object itemO = ((ArrayList) o).get(i);
                if (itemO instanceof MovieItemDb) {
                    if (i == 0) {
                        if (deleteFirst) {
                            deleteCacheFile(itemO, true, true);
                        } else {
                            deleteCacheFile(itemO, false, true);
                        }
                    } else {
                        deleteCacheFile(itemO, true, true);
                    }
                }
            }
        }
    }

    @Override
    public void setContentDetails(ActivityDetailsBean contentDetails) {
        if (contentDetails != null && contentDetails.getList() != null && contentDetails.getList().size() > 0) {
            List<ActivityDetailsBean.ListBean> list = contentDetails.getList();
            for (int i = 0; i < list.size(); i++) {
                // 活动类型(1:邀请活动;2:集赞活动)
                if (list.get(i).getActivity_type() != null && list.get(i).getActivity_type().equals("2") && list.get(i).getActivity_photos() != null) {
                    VideoJoinActivityDialog dialog = new VideoJoinActivityDialog(this, getString(R.string.common_join_title_dialog), "", list.get(i).getActivity_photos().toString());
                    if (mLoadProgress != null) {
                        mLoadProgress.dismiss();
                    }
                    dialog.show();
                    Window dialogWindow = dialog.getWindow();
                    WindowManager m = getWindowManager();
                    Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                    WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
                    p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.8
                    p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.8
                    dialogWindow.setAttributes(p);
                }
            }
        }
    }

    @Override
    public void onNetworkError(Throwable throwable) {
        if (mLoadProgress != null) {
            mLoadProgress.dismiss();
        }
        showError();
    }

    @Override
    public void onFailed(Throwable throwable) {
        if (mLoadProgress != null) {
            mLoadProgress.dismiss();
        }
        showToast(throwable.getMessage());
    }

    private void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        ToastUtil.showToast(this, msg);
    }
}
