package com.xianghe.ivy.weight.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.RankingAdapter;
import com.xianghe.ivy.adapter.RankingTypeAdapter;
import com.xianghe.ivy.adapter.recyclerview.IRecyclerItemClickListener;
import com.xianghe.ivy.adapter.recyclerview.RecyclerHolder;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.HuoDongListBean;
import com.xianghe.ivy.model.RankingUserBean;
import com.xianghe.ivy.model.MonthFormatBean;
import com.xianghe.ivy.model.RankingInfo;
import com.xianghe.ivy.model.WeekFormatBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.ui.module.record.RecordActivity;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.weight.popup.PopupRecycler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RankingDialog extends DialogFragment {
    private static final String TAG = "RankingDialog";
    private static final String TAG_FRAGMENT_LOGIN_CONFIRM = "tag_fragment_login_confirm";

    private static final int DEFAULT_DATE_FORMAT = -1;
    private static final int HUO_DONG_TYPE_WEEK = 1;
    private static final int HUO_DONG_TYPE_MONTH = 2;

    private View mLayoutRoot;
    private View mBtnJoin;
    private View mLayoutRankingTypeWeek;
    private View mLayoutRankingTypeMonth;
    private TextView mTvRankingTypeWeek;
    private TextView mTvRankingTypeMonth;
    private View mBtnRankingTypeWeek;
    private View mBtnRankingTypeMonth;
    private RecyclerView mViewList;

    private RankingTypeAdapter<WeekFormatBean> mWeekTypeAdapter;
    private RankingTypeAdapter<MonthFormatBean> mMonthsTypeAdapter;
    private RankingAdapter mRankingAdapter = null;

    private int mWeekDateFormat = DEFAULT_DATE_FORMAT;
    private int mMonthDateFormat = DEFAULT_DATE_FORMAT;

    private RankingInfo mRankingInfo;
    private HuoDongListBean mHuoDongInfos;

    private PopupRecycler mWeekPopup;
    private PopupRecycler mMonthPopup;
    private MyLayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_ranking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view, savedInstanceState);
        initView(view, savedInstanceState);
        initListener(view, savedInstanceState);
    }

    private void init() {
        Context context = getContext();
        mWeekTypeAdapter = new RankingTypeAdapter<WeekFormatBean>(null) {

            @Override
            protected void onBindViewData(RecyclerHolder holder, int position, List<WeekFormatBean> datas) {
                WeekFormatBean weekFormat = mWeekTypeAdapter.getItem(position);
                holder.setText(R.id.tv_text, weekFormat.getTitle());
            }
        };
        mMonthsTypeAdapter = new RankingTypeAdapter<MonthFormatBean>(null) {

            @Override
            protected void onBindViewData(RecyclerHolder holder, int position, List<MonthFormatBean> datas) {
                MonthFormatBean monthFormat = mMonthsTypeAdapter.getItem(position);
                holder.setText(R.id.tv_text, monthFormat.getTitle());
            }
        };

        mLayoutManager = new MyLayoutManager(context, 6);
        mLayoutManager.setSpanSizeLookup(mSpanSizeLookup);
        mRankingAdapter = new RankingAdapter(context, mLayoutManager);

        loadActivityRanking(HUO_DONG_TYPE_WEEK, mWeekDateFormat);
    }

    private void findView(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mLayoutRoot = view.findViewById(R.id.layout_root);

        mBtnJoin = view.findViewById(R.id.btn_join);

        mLayoutRankingTypeWeek = view.findViewById(R.id.layout_ranking_type_week);
        mLayoutRankingTypeMonth = view.findViewById(R.id.layout_ranking_type_month);

        mTvRankingTypeWeek = view.findViewById(R.id.tv_ranking_type_week);
        mTvRankingTypeMonth = view.findViewById(R.id.tv_ranking_type_month);

        mBtnRankingTypeWeek = view.findViewById(R.id.btn_ranking_type_week);
        mBtnRankingTypeMonth = view.findViewById(R.id.btn_ranking_type_month);

        mViewList = view.findViewById(R.id.view_list);
    }

    private void initView(View view, Bundle savedInstanceState) {
        final Context context = getContext();

        mTvRankingTypeWeek.setSelected(true);
        mBtnRankingTypeWeek.setSelected(true);

        mViewList.setLayoutManager(mLayoutManager);
        mViewList.setAdapter(mRankingAdapter);
    }

    private void initListener(View view, Bundle savedInstanceState) {
        mLayoutRoot.setOnClickListener(mOnClickListener);
        mBtnJoin.setOnClickListener(mOnClickListener);
        mTvRankingTypeWeek.setOnClickListener(mOnClickListener);
        mTvRankingTypeMonth.setOnClickListener(mOnClickListener);
        mBtnRankingTypeWeek.setOnClickListener(mOnClickListener);
        mBtnRankingTypeMonth.setOnClickListener(mOnClickListener);

        mWeekTypeAdapter.setItemClickListener(mWeekItemClickListener);
        mMonthsTypeAdapter.setItemClickListener(mMonthItemClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }

        dialog.setCanceledOnTouchOutside(true); //
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.dimAmount = 0;
        window.setAttributes(layoutParams);
    }

    /**
     * @param type       排行榜类型：默认为1，1=周排行榜， 2=月排行榜
     * @param dateFormat 周期: 默认本周（不填）， week_format（month_format）属性下的date_format 字段
     */
    @SuppressLint("CheckResult")
    private void loadActivityRanking(int type, int dateFormat) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.TYPE, type);
        if (dateFormat > 0) {
            params.put(Api.Key.DATE_FORMAT, dateFormat);
        }
        NetworkRequest.INSTANCE.postMap(Api.Route.Index.ACTIVITY_RANKING, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<JsonElement, BaseResponse<RankingInfo>>() {
                    @Override
                    public BaseResponse<RankingInfo> apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, new TypeToken<BaseResponse<RankingInfo>>() {
                        }.getType());
                    }
                })
                .subscribe(new Consumer<BaseResponse<RankingInfo>>() {
                    @Override
                    public void accept(BaseResponse<RankingInfo> response) throws Exception {
                        if (response.getStatus() == BaseResponse.Status.OK) {
                            mRankingInfo = response.getData();
                            updateRankingInfo(mRankingInfo, dateFormat);
                            loadActivityInfo(2);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void loadActivityInfo(int activityType) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.ACTIVITY_TYPE, activityType);
        NetworkRequest.INSTANCE.postMap(Api.Route.Index.ACTIVITY_INDEX, params)
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
                            mHuoDongInfos = response.getData();
                            updateHuoDongInfo(mHuoDongInfos);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void updateHuoDongInfo(HuoDongListBean huoDongInfos) {
        KLog.i(TAG, huoDongInfos);
        if (huoDongInfos == null || huoDongInfos.getList() == null || huoDongInfos.getList().size() <= 0) {
            return;
        }
        mRankingAdapter.setHuoDong(huoDongInfos.getList().get(0));
    }

    private void updateRankingInfo(RankingInfo rankingInfo, int dateFormat) {
        if (rankingInfo == null) {
            return;
        }

        int type = rankingInfo.getType();
        if (type == HUO_DONG_TYPE_WEEK) {
            mWeekDateFormat = dateFormat;
        } else if (type == HUO_DONG_TYPE_MONTH) {
            mMonthDateFormat = dateFormat;
        }

        List<WeekFormatBean> weekList = rankingInfo.getWeekFormat();
        List<MonthFormatBean> monthList = rankingInfo.getMonthFormat();
        List<RankingUserBean> rankingList = rankingInfo.getList();

        switchHuoType(type, dateFormat);

        mWeekTypeAdapter.setDatas(weekList);
        mMonthsTypeAdapter.setDatas(monthList);
        mRankingAdapter.setRankings(rankingList);
    }

    private void onClickBtnJoin(View v) {
        Context context = getContext();
        Resources res = context.getResources();
        if (!isLogin()) {
            new ConfirmDialog.Builder()
                    .setTitleText(getString(R.string.common_tips_title))
                    .setContentText(getString(R.string.common_tips_no_login))
                    .setBtnPositiveText(getString(R.string.common_cancel))
                    .setBtnNagtiveText(getString(R.string.common_ensure))
                    .setBtnNagtiveTextColor(res.getColor(R.color.red_color_FF4855))
                    .setBtnClickListener(new ConfirmDialog.BtnClickListener() {
                        @Override
                        public void onPositiveBtnClick(ConfirmDialog dialog, View view) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onNagtiveBtnClick(ConfirmDialog dialog, View view) {
                            dialog.dismiss();
//                            Intent intent = new Intent(getContext(), LoginActivity.class);
//                            startActivity(intent);
                        }
                    }).build().show(getChildFragmentManager(), TAG_FRAGMENT_LOGIN_CONFIRM);
            return;
        }
        Intent intent = new Intent(getContext(), RecordActivity.class);
        startActivity(intent);
    }

    private void onClickTvRankingTypeWeek(View v) {
        if (mRankingInfo != null) {
            if (mRankingInfo.getType() == HUO_DONG_TYPE_WEEK) {
                // 当前就是 week 信息
                return;
            }
        }
        loadActivityRanking(HUO_DONG_TYPE_WEEK, mWeekDateFormat);
    }

    private void onClickTvRankingTypeMonth(View v) {
        if (mRankingInfo != null) {
            if (mRankingInfo.getType() == HUO_DONG_TYPE_MONTH) {
                // 当前就是 week 信息
                return;
            }
        }
        loadActivityRanking(HUO_DONG_TYPE_MONTH, mMonthDateFormat);
    }

    private void onClickBtnRankingTypeWeek(View v) {
        View anchorView = mLayoutRankingTypeWeek;
        mWeekPopup = new PopupRecycler(getContext(), mWeekTypeAdapter, anchorView.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        mWeekPopup.showAsDropDown(anchorView);
    }

    private void onClickBtnRankingTypeMonth(View v) {
        View anchorView = mLayoutRankingTypeMonth;
        mMonthPopup = new PopupRecycler(getContext(), mMonthsTypeAdapter, anchorView.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        mMonthPopup.showAsDropDown(anchorView);
    }

    private void onClickLayoutRoot(View v) {
        dismiss();
    }

    private void switchHuoType(int type, int dateFormat) {
        if (type == HUO_DONG_TYPE_MONTH) {
//            mTvRankingTypeWeek.setSelected(false);
//            mBtnRankingTypeWeek.setSelected(false);
//            mTvRankingTypeMonth.setSelected(true);
//            mBtnRankingTypeMonth.setSelected(true);
            mLayoutRankingTypeWeek.setSelected(false);
            mLayoutRankingTypeMonth.setSelected(true);
        } else if (type == HUO_DONG_TYPE_WEEK) {
//            mTvRankingTypeWeek.setSelected(true);
//            mBtnRankingTypeWeek.setSelected(true);
//            mTvRankingTypeMonth.setSelected(false);
//            mBtnRankingTypeMonth.setSelected(false);
            mLayoutRankingTypeWeek.setSelected(true);
            mLayoutRankingTypeMonth.setSelected(false);
        }

        final RankingInfo rankingInfo = mRankingInfo;

        // 设置选中标题
        // 如果默认, 使用列表中的第一个Format
        if (rankingInfo != null) {
            if (type == HUO_DONG_TYPE_MONTH) {
                List<MonthFormatBean> monthFormats = rankingInfo.getMonthFormat();
                if (monthFormats != null || monthFormats.size() > 0) {
                    if (dateFormat == DEFAULT_DATE_FORMAT) {
                        MonthFormatBean monthFormat = monthFormats.get(0);
                        mTvRankingTypeMonth.setText(monthFormat.getTitle());
                    } else {
                        for (MonthFormatBean monthFormat : monthFormats) {
                            if (monthFormat.getDateFormat() == dateFormat) {
                                mTvRankingTypeMonth.setText(monthFormat.getTitle());
                                break;
                            }
                        }
                    }
                } else {
                    KLog.w(TAG, "rankingInfo.getMonthFormat() is null or size <= 0");
                }
            } else if (type == HUO_DONG_TYPE_WEEK) {
                List<WeekFormatBean> weekFormats = rankingInfo.getWeekFormat();
                if (weekFormats != null || weekFormats.size() > 0) {
                    if (dateFormat == DEFAULT_DATE_FORMAT) {
                        WeekFormatBean weekFormat = weekFormats.get(0);
                        mTvRankingTypeWeek.setText(weekFormat.getTitle());
                    } else {
                        for (WeekFormatBean weekFormat : weekFormats) {
                            if (weekFormat.getDateFormat() == dateFormat) {
                                mTvRankingTypeWeek.setText(weekFormat.getTitle());
                                break;
                            }
                        }
                    }
                } else {
                    KLog.w(TAG, "rankingInfo.getWeekFormat() is null or size <= 0");
                }
            }
        }
    }

    private boolean isLogin() {
        return UserInfoManager.isLogin();
    }

    private IRecyclerItemClickListener mWeekItemClickListener = new IRecyclerItemClickListener() {
        @Override
        public void onItemClick(RecyclerView parent, int position, RecyclerView.ViewHolder holder, View view) {
            if (mWeekPopup != null) {
                mWeekPopup.dismiss();
            }
            WeekFormatBean formatBean = mWeekTypeAdapter.getItem(position);
            mTvRankingTypeWeek.setText(formatBean.getTitle());
            loadActivityRanking(HUO_DONG_TYPE_WEEK, formatBean.getDateFormat());
        }
    };

    private IRecyclerItemClickListener mMonthItemClickListener = new IRecyclerItemClickListener() {
        @Override
        public void onItemClick(RecyclerView parent, int position, RecyclerView.ViewHolder holder, View view) {
            if (mMonthPopup != null) {
                mMonthPopup.dismiss();
            }
            MonthFormatBean formatBean = mMonthsTypeAdapter.getItem(position);
            mTvRankingTypeMonth.setText(formatBean.getTitle());
            loadActivityRanking(HUO_DONG_TYPE_MONTH, formatBean.getDateFormat());
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_join:
                    onClickBtnJoin(v);
                    break;
                case R.id.tv_ranking_type_week:
                    onClickTvRankingTypeWeek(v);
                    break;
                case R.id.tv_ranking_type_month:
                    onClickTvRankingTypeMonth(v);
                    break;
                case R.id.btn_ranking_type_week:
                    onClickBtnRankingTypeWeek(v);
                    break;
                case R.id.btn_ranking_type_month:
                    onClickBtnRankingTypeMonth(v);
                    break;
                default:
                    onClickLayoutRoot(v);
                    break;
            }
        }
    };


    /**
     * 控制 GradLayoutManager item 占的 span
     */
    private GridLayoutManager.SpanSizeLookup mSpanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int i) {
            switch (mRankingAdapter.getItemViewType(i)) {
                case RankingAdapter.ITEM_VIEW_TYPE_TOP3: {
                    switch (mRankingAdapter.getRankingListSize()) {
                        case 1:
                            return 6;
                        case 2:
                            return 3;
                        default:
                            return 2;
                    }
                }
                default:
                    return 6;
            }
        }
    };

    public static class MyLayoutManager extends GridLayoutManager {
        public boolean canScrollVertically = true;

        public MyLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        @Override
        public boolean canScrollVertically() {
            return canScrollVertically && super.canScrollVertically();
        }
    }
}
