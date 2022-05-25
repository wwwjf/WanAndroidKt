package com.leochuan;

import static com.leochuan.ScrollHelper.smoothScrollToPosition;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

public class MyPageSnapHelper extends CenterSnapHelper {
        private static final String TAG = "MyPageSnapHelper";
    
        @Override
        public boolean onFling(int velocityX, int velocityY) {
            //判断滑动的速度跟宽度
    
            ViewPagerLayoutManager layoutManager = (ViewPagerLayoutManager) mRecyclerView.getLayoutManager();
            if (layoutManager == null) {
                return false;
            }
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter == null) {
                return false;
            }
    
            if (!layoutManager.getInfinite() &&
                    (layoutManager.mOffset == layoutManager.getMaxOffset()
                            || layoutManager.mOffset == layoutManager.getMinOffset())) {
                return false;
            }
    
            final int minFlingVelocity = mRecyclerView.getMinFlingVelocity();
            Log.d(TAG, "minFlingVelocity:" + minFlingVelocity);
            mGravityScroller.fling(0, 0, velocityX, velocityY,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
    
            if (layoutManager.mOrientation == ViewPagerLayoutManager.VERTICAL
                    && Math.abs(velocityY) > minFlingVelocity) {
                final int currentPosition = layoutManager.getCurrentPositionOffset();
                final int offsetPosition = (int) Math.sqrt(mGravityScroller.getFinalY() /
                        layoutManager.mInterval / layoutManager.getDistanceRatio());
                smoothScrollToPosition(mRecyclerView, layoutManager, layoutManager.getReverseLayout() ?
                        -currentPosition - offsetPosition : currentPosition + offsetPosition);
                return true;
            } else if (layoutManager.mOrientation == ViewPagerLayoutManager.HORIZONTAL
                    && Math.abs(velocityX) > minFlingVelocity) {
    //            final int currentPosition = layoutManager.getCurrentPositionOffset();
    //            float offsetPosition = mGravityScroller.getFinalX() / layoutManager.mInterval / layoutManager.getDistanceRatio();
    //
    //            int finalOffsetPosition;
    //            if (offsetPosition < 0) {
    //                finalOffsetPosition = -(int) Math.sqrt(Math.sqrt(Math.abs(offsetPosition)));
    //
    //            } else {
    //                finalOffsetPosition = (int) Math.sqrt(Math.sqrt(offsetPosition));
    //                finalOffsetPosition /= 2;
    //            }
    //            KLog.w(TAG, "finalOffsetPosition = " + finalOffsetPosition);
    //            ScrollHelper.smoothScrollToPosition(mRecyclerView, layoutManager, layoutManager.getReverseLayout() ?
    //                    -currentPosition - finalOffsetPosition : currentPosition + finalOffsetPosition);
                //判断滑动的距离
                if (velocityX> minFlingVelocity){
                    if (velocityX <= 18000){
                        //判断是不是滑动状态
                        mRecyclerView.smoothScrollToPosition(layoutManager.getCurrentPosition()+1);
                    }else if (velocityX <= 20000){
                        mRecyclerView.smoothScrollToPosition(layoutManager.getCurrentPosition()+2);
                    }else{
                        mRecyclerView.smoothScrollToPosition(layoutManager.getCurrentPosition()+3);
                    }
                }else{
                    if (velocityX < minFlingVelocity && velocityX>=-18000){
                        if (layoutManager.getCurrentPosition()>=1){
                            mRecyclerView.smoothScrollToPosition(layoutManager.getCurrentPosition()-1);
                        }
                    }else if (velocityX <-18000 && velocityX>=-20000){
                        if (layoutManager.getCurrentPosition() >= 2){
                            mRecyclerView.smoothScrollToPosition(layoutManager.getCurrentPosition()-2);
                        }
                    }else{
                        if (layoutManager.getCurrentPosition()>=3){
                            mRecyclerView.smoothScrollToPosition(layoutManager.getCurrentPosition()-3);
                        }
                    }
                }
                return true;
            }
    
            return true;
        }
    }