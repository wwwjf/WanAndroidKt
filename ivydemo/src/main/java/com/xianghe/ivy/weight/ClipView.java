package com.xianghe.ivy.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseMultiItemQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.ui.module.preview_movie.MovieClipHelper;
import com.xianghe.ivy.ui.module.record.local_media.LocalItemBean;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.utils.ActivityUtils;
import com.xianghe.ivy.utils.FileUtill;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import icepick.State;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ClipView extends FrameLayout {

    private View mLeftMovieBg;                                                                      //左边的黑色背景

    private View mRightMovieBg;                                                                     //右边的黑色背景

    private View mClipLayout;                                                                       //整个裁剪的布局

    private TextView mClipTime;                                                                     //中间显示的时间

    private View mLeftClipBtn;                                                                      //左边的滑块

    private View mRightClipBtn;                                                                     //右边的滑块

    private BigDecimal mTime;

    private Context mContext;

    private LinearLayout mImageLayout;

    private MovieClipHelper mMovieClipHelper;

    private final int TYPE_LEVEL_0 = 0;

    private final int TYPE_LEVEL_1 = 1;

    private RecyclerView mRecyclerView;

    @State
    String mClipFileDir;

    @State
    int mSecondWidth;

    @State
    int mImageWidth;

    @State
    double mEndTime;

    @State
    double mStartTime;

    @State
    double mScrollStartTime;

    @State
    double mScrollEndTime;

    @State
    int mClipTotalWidth;

    @State
    int mScrollWidth;

    @State
    boolean isFirstScroll = true;

    @State
    double mMaxTime = 1;

    @State
    double mLimitTime = 1;

    @State
    BigDecimal mTotalTime;

    @State
    BigDecimal mScrollTime = BigDecimal.ZERO;

    private MovieItemModel mMovieItemModel;

    private FrameLayout mMClipRootView;

    private ClipListener mClipListener;

    private ClipViewAdapter mClipViewAdapter;

    public ClipView(@NonNull Context context) {
        this(context, null);
    }

    public ClipView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        initView(context);

        initListener();

        //计算0.1秒对应的宽度
        claWidth();
    }

    private void claWidth() {
        mLeftClipBtn.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mClipTotalWidth = mClipLayout.getWidth();
                BigDecimal imageWidthDecimal = BigDecimal.valueOf(mClipTotalWidth).divide(BigDecimal.valueOf(20), 3, BigDecimal.ROUND_HALF_UP);
                mImageWidth = imageWidthDecimal.intValue();
                mSecondWidth = imageWidthDecimal.multiply(BigDecimal.valueOf(20)).divide(BigDecimal.valueOf(1800), 1, BigDecimal.ROUND_HALF_UP).intValue();
                mLeftClipBtn.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (mClipListener != null) {
                    mClipListener.onClipInflated();
                }
            }
        });
    }

    private void initListener() {
        initLeftTouch();
        initRightTouch();
    }

    private void initLeftTouch() {
        mLeftClipBtn.setOnTouchListener(new OnTouchListener() {

            private RelativeLayout.LayoutParams mTimeLayoutParams;
            private BigDecimal mSecondMiddleWidth;
            private int mRightClipWidth;
            private int mLeftClipWidth;
            private RelativeLayout.LayoutParams mRightLayoutParams;
            private float mLeftDownX;
            private RelativeLayout.LayoutParams mLeftClipParams;
            private LayoutParams mLeftMovieParams;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //获取左右两个滑动控件的宽度
                        mLeftClipWidth = mLeftClipBtn.getWidth();
                        mRightClipWidth = mRightClipBtn.getWidth();
                        mSecondMiddleWidth = calCanMoveMiddleWidth(mLeftClipWidth, mRightClipWidth);
                        mLeftClipParams = (RelativeLayout.LayoutParams) mLeftClipBtn.getLayoutParams();
                        mRightLayoutParams = (RelativeLayout.LayoutParams) mRightClipBtn.getLayoutParams();
                        mLeftMovieParams = (LayoutParams) mLeftMovieBg.getLayoutParams();
                        mTimeLayoutParams = (RelativeLayout.LayoutParams) mClipTime.getLayoutParams();
                        mLeftDownX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //计算时间
                        mLeftClipParams.leftMargin += (int) (event.getX() - mLeftDownX);
                        if (mLeftClipParams.leftMargin < 0) {
                            mLeftClipParams.leftMargin = 0;
                        }

                        //计算左两边的时间值
                        //根据右边的时间计算左边最多能滑动多少
                        int maxLeftMargin = calCanScrollWidth(mLeftClipWidth, mRightClipWidth, mSecondMiddleWidth, mRightLayoutParams.rightMargin).intValue();

                        if (mLeftClipParams.leftMargin >= maxLeftMargin) {
                            mLeftClipParams.leftMargin = maxLeftMargin;
                        }

                        //滑动时间
                        mTimeLayoutParams.leftMargin = mLeftClipParams.leftMargin;
                        mClipTime.setLayoutParams(mTimeLayoutParams);

                        //计算左边的时间
                        calLeftCurrentTime(mLeftClipWidth, mRightClipWidth, BigDecimal.valueOf(mLeftClipParams.leftMargin), mRightLayoutParams.rightMargin);

                        mLeftClipBtn.setLayoutParams(mLeftClipParams);

                        //让左边的剪切view显示
                        mLeftMovieParams.width = mLeftClipParams.leftMargin;
                        if (mLeftMovieParams.width < 0) {
                            mLeftMovieParams.width = 0;
                        }
                        mLeftMovieBg.setLayoutParams(mLeftMovieParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        //设置播放的位置
                        if (mClipListener != null) {
                            mClipListener.onClipStopListener(mStartTime, mEndTime);
                        }
                        break;
                }
                return true;
            }
        });
    }


    private void initRightTouch() {
        mRightClipBtn.setOnTouchListener(new OnTouchListener() {
            private RelativeLayout.LayoutParams mClipTimeLayoutParams;
            private RelativeLayout.LayoutParams mLeftLayoutParams;
            private BigDecimal mSecondMiddleWidth;
            private int mRightClipWidth;
            private int mLeftClipWidth;
            private float mRightDownX;
            private RelativeLayout.LayoutParams mRightClipParams;
            private LayoutParams mRightMovieParams;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //获取左右两个滑动控件的宽度
                        //获取左右两个滑动控件的宽度
                        mLeftClipWidth = mLeftClipBtn.getWidth();
                        mRightClipWidth = mRightClipBtn.getWidth();
                        mSecondMiddleWidth = calCanMoveMiddleWidth(mLeftClipWidth, mRightClipWidth);

                        mRightDownX = event.getX();
                        mLeftLayoutParams = (RelativeLayout.LayoutParams) mLeftClipBtn.getLayoutParams();
                        mRightClipParams = (RelativeLayout.LayoutParams) mRightClipBtn.getLayoutParams();
                        mRightMovieParams = (LayoutParams) mRightMovieBg.getLayoutParams();
                        mClipTimeLayoutParams = (RelativeLayout.LayoutParams) mClipTime.getLayoutParams();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mRightClipParams.rightMargin += mRightDownX - event.getX();
                        if (mRightClipParams.rightMargin < 0) {
                            mRightClipParams.rightMargin = 0;
                        }

                        //计算左两边的时间值
                        //根据右边的时间计算左边最多能滑动多少
                        int maxRightMargin = calCanScrollWidth(mLeftClipWidth, mRightClipWidth, mSecondMiddleWidth, mLeftLayoutParams.leftMargin).intValue();
                        if (mRightClipParams.rightMargin >= maxRightMargin) {
                            mRightClipParams.rightMargin = maxRightMargin;
                        }
                        mRightClipBtn.setLayoutParams(mRightClipParams);

                        //改变textView的宽度就能让文字移动了
                        mClipTimeLayoutParams.rightMargin = mRightClipParams.rightMargin;
                        mClipTime.setLayoutParams(mClipTimeLayoutParams);

                        //计算右边的时间
                        calRightCurrentTime(mLeftClipWidth, mRightClipWidth, BigDecimal.valueOf(mLeftLayoutParams.leftMargin), mRightClipParams.rightMargin);
                        //显示右边的view
                        mRightMovieParams.width = mRightClipParams.rightMargin;
                        if (mRightMovieParams.width < 0) {
                            mRightMovieParams.width = 0;
                        }
                        mRightMovieBg.setLayoutParams(mRightMovieParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        //设置播放的位置
                        if (mClipListener != null) {
                            mClipListener.onClipStopListener(mStartTime, mEndTime);
                        }
                        break;
                }
                return true;
            }
        });
    }

    public double getStartTime() {
        return mStartTime;
    }

    public double getEndTime() {
        return mEndTime;
    }


    /**
     * 计算中间可以有的宽度
     */
    public BigDecimal calCanMoveMiddleWidth(int mLeftClipWidth, int mRightClipWidth) {
        if (mLimitTime != 1) {
            //获取一秒对应的宽度
            BigDecimal secondMiddleWidthDecimal = getSecondMiddleWidth(mLeftClipWidth, mRightClipWidth);
            if (secondMiddleWidthDecimal != null) {
                return BigDecimal.valueOf(mLimitTime).multiply(secondMiddleWidthDecimal);
            }
        } else {
            if (mMaxTime != 1) {
                if (mTotalTime.compareTo(BigDecimal.valueOf(mMaxTime).subtract(BigDecimal.valueOf(1))) < 0) {
                    //获取一秒对应的宽度
                    BigDecimal secondMiddleWidthDecimal = getSecondMiddleWidth(mLeftClipWidth, mRightClipWidth);
                    if (secondMiddleWidthDecimal != null) {
                        return BigDecimal.valueOf(mMaxTime).subtract(mTotalTime).multiply(secondMiddleWidthDecimal);
                    }
                } else {
                    //获取一秒对应的宽度
                    BigDecimal secondMiddleWidthDecimal = getSecondMiddleWidth(mLeftClipWidth, mRightClipWidth);
                    if (secondMiddleWidthDecimal != null) {
                        return secondMiddleWidthDecimal;
                    }
                }
            } else {
                //获取一秒对应的宽度
                BigDecimal secondMiddleWidthDecimal = getSecondMiddleWidth(mLeftClipWidth, mRightClipWidth);
                if (secondMiddleWidthDecimal != null) {
                    return secondMiddleWidthDecimal;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 计算当前中间的时间
     */
    private void calLeftCurrentTime(int leftClipWidth, int rightClipWidth, BigDecimal leftClipMargin, int rightClipMargin) {
        BigDecimal middleWidth = calCanScrollWidth(leftClipWidth, rightClipWidth, leftClipMargin, rightClipMargin);
        BigDecimal secondMiddleWidth = getSecondMiddleWidth(leftClipWidth, rightClipWidth);

        if (secondMiddleWidth != null && secondMiddleWidth.doubleValue()>0) {
            BigDecimal currentTime = middleWidth.divide(secondMiddleWidth, 2, BigDecimal.ROUND_UP).setScale(1, BigDecimal.ROUND_DOWN);
            mClipTime.setText(String.format(mContext.getString(R.string.common_second), currentTime.toString()));
            //获取右边的时间,加上宽度遮罩的大小
            BigDecimal rightLeaveTime = BigDecimal.valueOf(rightClipMargin).divide(secondMiddleWidth, 1, BigDecimal.ROUND_HALF_UP);
            //用总时间减去右边和中间的就是左边的时间
            BigDecimal totalTime = mMovieItemModel.getVideoTime().compareTo(BigDecimal.valueOf(180)) > 0 ? BigDecimal.valueOf(180.0) : mMovieItemModel.getVideoTime();
            mStartTime = totalTime.subtract(rightLeaveTime).subtract(currentTime).add(mScrollTime).doubleValue();
        }
    }

    private void calScrollCurrentTime(double startTime, double endTime, int scrollWidth) {
        if (mEndTime != 0) {
            BigDecimal secondMiddleWidth = getScrollTime();
            if (secondMiddleWidth != null && secondMiddleWidth.doubleValue()>0) {
                mScrollTime = BigDecimal.valueOf(scrollWidth).divide(secondMiddleWidth, 3, BigDecimal.ROUND_HALF_UP).setScale(1, BigDecimal.ROUND_DOWN);
                mStartTime = BigDecimal.valueOf(startTime).add(mScrollTime).doubleValue();
                mEndTime = BigDecimal.valueOf(endTime).add(mScrollTime).doubleValue();
            }
        }
    }

    private BigDecimal getSecondMiddleWidth(int leftClipWidth, int rightClipWidth) {
        if (mMovieItemModel != null && mMovieItemModel.getVideoTime().doubleValue()>0) {
            return BigDecimal.valueOf(mClipTotalWidth).
                    subtract(BigDecimal.valueOf(leftClipWidth)).
                    subtract(BigDecimal.valueOf(rightClipWidth))
                    .divide(mMovieItemModel.getVideoTime().compareTo(BigDecimal.valueOf(180)) > 0 ? BigDecimal.valueOf(180) : mMovieItemModel.getVideoTime(), 3, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    private BigDecimal getScrollTime() {
        if (mMovieItemModel != null) {
            return getCanScrollWidth()
                    .divide(mMovieItemModel.getVideoTime().subtract(BigDecimal.valueOf(180)), 3, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    /**
     * 计算右边的时间
     */
    private void calRightCurrentTime(int leftClipWidth, int rightClipWidth, BigDecimal leftClipMargin, int rightClipMargin) {
        BigDecimal middleWidth = calCanScrollWidth(leftClipWidth, rightClipWidth, leftClipMargin, rightClipMargin);
        BigDecimal secondMiddleWidth = getSecondMiddleWidth(leftClipWidth, rightClipWidth);

        if (secondMiddleWidth != null && secondMiddleWidth.doubleValue()>0) {
            BigDecimal currentTime = middleWidth.divide(secondMiddleWidth, 2, BigDecimal.ROUND_UP).setScale(1, BigDecimal.ROUND_DOWN);
            mClipTime.setText(String.format(mContext.getString(R.string.common_second), currentTime.toString()));
            //获取左边的时间，右边的偏移量加上宽度遮罩的大小
            BigDecimal rightLeaveTime = leftClipMargin.divide(secondMiddleWidth, 1, BigDecimal.ROUND_HALF_UP);
            //用总时间减去左边和中间的就是左边的时间
            BigDecimal totalTime = mMovieItemModel.getVideoTime().compareTo(BigDecimal.valueOf(180)) > 0 ? BigDecimal.valueOf(180.0) : mMovieItemModel.getVideoTime();
            mEndTime = totalTime.subtract(totalTime.subtract(rightLeaveTime).subtract(currentTime)).add(mScrollTime).doubleValue();
        }
    }

    /**
     * 计算能够滑动多少距离
     */
    private BigDecimal calCanScrollWidth(int leftClipWidth, int rightClipWidth, BigDecimal middleWidth, int clipMargin) {
        //根据左边右边中间的宽度计算左边最大的margin
        //判断片段时间是不是小于一秒
        return BigDecimal.valueOf(mClipTotalWidth)
                .subtract(BigDecimal.valueOf(leftClipWidth))
                .subtract(BigDecimal.valueOf(rightClipWidth))
                .subtract(middleWidth)
                .subtract(BigDecimal.valueOf(clipMargin)).compareTo(BigDecimal.ONE)>=0?BigDecimal.valueOf(mClipTotalWidth)
                .subtract(BigDecimal.valueOf(leftClipWidth))
                .subtract(BigDecimal.valueOf(rightClipWidth))
                .subtract(middleWidth)
                .subtract(BigDecimal.valueOf(clipMargin)):BigDecimal.ZERO;
    }

    public void setTime(MovieItemModel movieItemModel, String videoFileName, int position, double limitTime, boolean needRefresh) {
//        position == 5
        mMovieItemModel = movieItemModel;
        mTime = movieItemModel.getVideoTime();
        mLimitTime = limitTime;
        initTime();
        //判断时间
        if (mTime.compareTo(BigDecimal.valueOf(180)) > 0) {
            mClipTime.setText(String.format(mContext.getString(R.string.common_second), 180.0 + ""));
            //时间大于180秒
            //添加recyclerView加上滑动效果
            //计算出0.1秒对应的宽度
            if (mRecyclerView == null) {
                mRecyclerView = new RecyclerView(mContext);
                mRecyclerView.addOnScrollListener(new OnRecyclerScrollListener());
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                mMClipRootView.addView(mRecyclerView, 0);
            }
            getVideoPicList(movieItemModel, position, videoFileName);
        } else {
            //小于等于180秒
            //只显示一页的数据
            if (mImageLayout != null) {
                mMClipRootView.removeView(mImageLayout);
            }
            mClipTime.setText(String.format(mContext.getString(R.string.common_second), mMovieItemModel.getVideoTime()));
            mImageLayout = new LinearLayout(mContext);
            mMClipRootView.addView(mImageLayout, 0);
            getVideoPicList(movieItemModel, position, videoFileName);
        }

        if (needRefresh) {
            RelativeLayout.LayoutParams leftLayoutParams = (RelativeLayout.LayoutParams) mLeftClipBtn.getLayoutParams();
            RelativeLayout.LayoutParams rightLayoutParams = (RelativeLayout.LayoutParams) mRightClipBtn.getLayoutParams();
            FrameLayout.LayoutParams leftMoveLayoutParams = (FrameLayout.LayoutParams) mLeftMovieBg.getLayoutParams();
            FrameLayout.LayoutParams rightMoveLayoutParams = (FrameLayout.LayoutParams) mRightMovieBg.getLayoutParams();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mClipTime.getLayoutParams();
            leftLayoutParams.leftMargin = 0;
            rightLayoutParams.rightMargin = 0;
            leftMoveLayoutParams.width = 0;
            rightMoveLayoutParams.width = 0;
            layoutParams.rightMargin = 0;
            layoutParams.leftMargin = 0;
            mClipTime.setLayoutParams(layoutParams);
            mLeftClipBtn.setLayoutParams(leftLayoutParams);
            mRightClipBtn.setLayoutParams(rightLayoutParams);
            mLeftMovieBg.setLayoutParams(leftMoveLayoutParams);
            mRightMovieBg.setLayoutParams(rightMoveLayoutParams);
        }
    }

    public void setTime(MovieItemModel movieItemModel, String videoFileName, int position, double limitTime) {
        setTime(movieItemModel, videoFileName, position, limitTime, false);
    }

    public void setTime(MovieItemModel movieItemModel, String videoFileName, int position) {
        setTime(movieItemModel, videoFileName, position, 1);
    }

    public void setTime(MovieItemModel movieItemModel, String videoFileName, int position, boolean needRefresh) {
        setTime(movieItemModel, videoFileName, position, 1, needRefresh);
    }

    public void setTime(MovieItemModel movieItemModel, String videoFileName, int position, double totalTime, double maxTime) {
        mMaxTime = maxTime;
        mTotalTime = BigDecimal.valueOf(totalTime).subtract(movieItemModel.getVideoTime());
        setTime(movieItemModel, videoFileName, position);
    }

    private void initTime() {
        mStartTime = 0.0;
        mEndTime = mTime.compareTo(BigDecimal.valueOf(180)) > 0 ? 180.0 : mTime.doubleValue();
    }

    private void getVideoPicList(MovieItemModel movieItemModel, int position, String videoFileName) {
        if (movieItemModel != null) {
            mMovieClipHelper = new MovieClipHelper.Builder(mContext)
                    .setSaveFileDir(videoFileName)
                    .setPicCount(0)
                    .setDefaultPicPath(movieItemModel.getFilPicPath())
                    .setTotalTime(movieItemModel.getVideoTime()
                            .intValue() > 20 ? 20 : movieItemModel.getVideoTime()
                            .intValue())
                    .setVideoPath(movieItemModel.getFilePath())
                    .setFilePosition(position)
                    .Build((pathList, fileDir, isDefault, disposable) -> {
                        mClipFileDir = fileDir;
                        if (isDefault || (disposable != null && !disposable.isDisposed())) {
                            if (mTime.compareTo(BigDecimal.valueOf(180)) > 0) {
                                showRecyclerPicList(pathList);
                            } else {
                                showPicList(pathList);
                            }
                        }
                    });
        }
    }

    private void showRecyclerPicList(ArrayList<String> pathList) {
        ArrayList<LocalItemBean> arrayList = new ArrayList<>();
        int clipTime = mTime.multiply(BigDecimal.valueOf(20)).divide(BigDecimal.valueOf(180), 0, BigDecimal.ROUND_DOWN).intValue();
        for (int i = 0; i < clipTime; i++) {
            LocalItemBean localItemBean = new LocalItemBean(pathList.get(0), 0);
            arrayList.add(localItemBean);
        }

        if (mTime.doubleValue() % 180 != 0) {
            LocalItemBean localItemBean = new LocalItemBean(pathList.get(0), 1);
            arrayList.add(localItemBean);
        }
        mClipViewAdapter = new ClipViewAdapter(arrayList);
        mRecyclerView.setAdapter(mClipViewAdapter);
    }

    public void destroy() {
        if (mMovieClipHelper != null) {
            mMovieClipHelper.onDestroy();
        }
        if (!TextUtils.isEmpty(mClipFileDir)) {
            FileUtill.deleteFile(new File(mClipFileDir));
        }
    }

    public void onStopClip() {
        if (mMovieClipHelper != null) {
            mMovieClipHelper.onDestroy();
        }
    }

    /**
     * 根据图片的多少创建view显示图片
     *
     * @param pathList 图片集合
     */
    private void showPicList(ArrayList<String> pathList) {
        if (mImageLayout != null) {
            mImageLayout.removeAllViews();
            //跳过内存缓存;
            RequestOptions requestOptions = new RequestOptions().skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, MATCH_PARENT, 1);
            for (int i = 0; i < pathList.size(); i++) {
                //判断是否已经移除了
                ImageView imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                try {
                    if (ActivityUtils.assertNotDestroyed(this)) {
                        Glide.with(this).load(pathList.get(i)).apply(requestOptions).into(imageView);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mImageLayout.addView(imageView, layoutParams);
            }
        }
    }

    private void initView(Context context) {
        View rootView = View.inflate(context, R.layout.view_clip_view, null);
        mMClipRootView = rootView.findViewById(R.id.fl_view_clip_rootView);
        mLeftMovieBg = rootView.findViewById(R.id.view_clip_view_left_movie_bg);
        mRightMovieBg = rootView.findViewById(R.id.view_clip_view_right_movie_bg);
        mClipLayout = rootView.findViewById(R.id.rl_clip_view_clip_layout);
        mClipTime = rootView.findViewById(R.id.tv_clip_view_clip_time);
        mLeftClipBtn = rootView.findViewById(R.id.view_clip_view_left_clip);
        mRightClipBtn = rootView.findViewById(R.id.view_clip_view_right_clip);
        addView(rootView);
    }

    public void setClipListener(ClipListener clipListener) {
        mClipListener = clipListener;
    }

    private class OnRecyclerScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (newState == SCROLL_STATE_IDLE) {
                if (mClipListener != null) {
                    mClipListener.onClipStopListener(mStartTime, mEndTime);
                    isFirstScroll = true;
                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (mEndTime > 0 && isFirstScroll) {
                //获取宽度
                //获取recyclerView的宽度
                mScrollStartTime = mStartTime;
                mScrollEndTime = mEndTime;
                mScrollWidth = 0;
                isFirstScroll = false;
            }
            mScrollWidth += dx;

            calScrollCurrentTime(mScrollStartTime, mScrollEndTime, mScrollWidth);
        }
    }

    private BigDecimal getCanScrollWidth() {
        BigDecimal totalWidth = BigDecimal.valueOf(mImageWidth);
        if (mClipViewAdapter != null && mClipViewAdapter.getData().size() > 0) {
            totalWidth = totalWidth.multiply(BigDecimal.valueOf(mClipViewAdapter.getData().size() - 1));
            if (mTime.doubleValue() % 180 != 0) {
                totalWidth = totalWidth.add(BigDecimal.valueOf(mTime.doubleValue() % 180 * mSecondWidth));
            } else {
                totalWidth = totalWidth.add(BigDecimal.valueOf(mImageWidth));
            }
        }
        return totalWidth.subtract(BigDecimal.valueOf(mClipTotalWidth));
    }


    private class ClipViewAdapter extends BaseMultiItemQuickAdapter<LocalItemBean, BaseViewHolder> {
        ClipViewAdapter(List<LocalItemBean> data) {
            super(data);
            addItemType(TYPE_LEVEL_0, R.layout.item_clip_view);
            addItemType(TYPE_LEVEL_1, R.layout.item_clip_view);
        }

        @Override
        protected void convert(BaseViewHolder helper, LocalItemBean item) {
            ImageView view = helper.getView(R.id.iv_item_clip_image);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            switch (item.getItemType()) {
                case TYPE_LEVEL_0:
                    //计算宽度
                    layoutParams.width = mImageWidth;
                    break;
                case TYPE_LEVEL_1:
                    //计算宽度
                    layoutParams.width = (int) (mTime.doubleValue() % 180 * mSecondWidth);
                    break;
            }
            view.setLayoutParams(layoutParams);
            //设置图片
            helper.setFileImage(mContext, R.id.iv_item_clip_image, item.getFilePath());
        }
    }

    public interface ClipListener {
        void onClipStopListener(double startTime, double endTime);

        void onClipInflated();
    }
}
