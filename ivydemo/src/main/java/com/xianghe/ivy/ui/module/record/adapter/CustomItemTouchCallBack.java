package com.xianghe.ivy.ui.module.record.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.ViewUtil;

import java.util.Collections;
import java.util.List;

public class CustomItemTouchCallBack extends ItemTouchHelper.Callback {

    private List<MovieItemModel> mDatas;

    private BaseQuickAdapter mAdapter;

    private Vibrator mVibrator;

    private int scaleSize;

    private onSwipeListener mListener;

    private OnTouchStatuListener mOnTouchStatuListener;

    public CustomItemTouchCallBack(List<MovieItemModel> datas, BaseQuickAdapter adapter, Activity activity) {
        mDatas = datas;
        mAdapter = adapter;
        mVibrator = (Vibrator) (activity).getSystemService(Context.VIBRATOR_SERVICE);//震动
        scaleSize = ViewUtil.dp2px(activity, 10);
    }

    public void setDatas(List<MovieItemModel> datas){
        mDatas = datas;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        KLog.d("CustomItemTouchCallBack","getMovementFlags");
        int dragFlags = 0;//dragFlags 是拖拽标志
        int swipeFlags = 0;//swipeFlags是侧滑标志，我们把swipeFlags 都设置为0，表示不处理滑动操作
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else {
            dragFlags = ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
        int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
        if (fromPosition < 0 || fromPosition >= mDatas.size()) {
            return false;
        }
         if (toPosition < 0 || toPosition >= mDatas.size()) {
            return false;
        }

        if (fromPosition < toPosition) { //分别把中间所有的item的位置重新交换
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mDatas, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mDatas, i, i - 1);
            }
        }
        mAdapter.notifyItemMoved(fromPosition, toPosition);
        if (mListener != null) {
            mListener.swipe();
        }
        //返回true表示执行拖动
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            mVibrator.vibrate(60);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewHolder.itemView.findViewById(R.id.iv_item_movie_show_list_pic).getLayoutParams();
            layoutParams.width = layoutParams.width + scaleSize;
            layoutParams.height = layoutParams.height + scaleSize;
            viewHolder.itemView.findViewById(R.id.iv_item_movie_show_list_pic).setLayoutParams(layoutParams);
            if (mOnTouchStatuListener != null){
                mOnTouchStatuListener.touchStatu(TouchStatus.DOWN);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewHolder.itemView.findViewById(R.id.iv_item_movie_show_list_pic).getLayoutParams();
        layoutParams.width = layoutParams.width - scaleSize;
        layoutParams.height = layoutParams.height - scaleSize;
        viewHolder.itemView.findViewById(R.id.iv_item_movie_show_list_pic).setLayoutParams(layoutParams);
        if (mOnTouchStatuListener != null){
            mOnTouchStatuListener.touchStatu(TouchStatus.UP);
        }
        super.clearView(recyclerView, viewHolder);
    }


    public void setListener(onSwipeListener listener) {
        this.mListener = listener;
    }


    public void setOnTouchStatuListener(OnTouchStatuListener listener){
        mOnTouchStatuListener = listener;
    }

    public interface onSwipeListener {
        void swipe();
    }

    public interface OnTouchStatuListener{
       void touchStatu(TouchStatus state);
    }

    public enum TouchStatus{
        DOWN,UP
    }
}
