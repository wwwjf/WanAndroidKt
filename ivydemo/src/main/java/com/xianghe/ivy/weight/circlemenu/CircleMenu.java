package com.xianghe.ivy.weight.circlemenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.xianghe.ivy.R;
import com.xianghe.ivy.utils.KLog;

import java.util.ArrayList;
import java.util.List;

public class CircleMenu extends ViewGroup implements IMenu {
    private static final String TAG = "CircleMenu";

    private final float INVALID_RADIUS = -1;    // 无效radius

    public static final Property<CircleMenu, Float> MENU_RADIUS = new Property<CircleMenu, Float>(Float.class, "menu_radius") {

        @Override
        public void set(CircleMenu object, Float value) {
            object.setMenuRadius(value);
        }

        @Override
        public Float get(CircleMenu object) {
            return object.getMenuRadius();
        }
    };

    public enum Anchor {
        CENTER,
        CENTER_LEFT,
        CENTER_TOP,
        CENTER_RIGHT,
        CENTER_BOTTOM,
        LEFT_TOP,
        LEFT_BOTTOM,
        RIGHT_TOP,
        RIGHT_BOTTOM,
    }

    // menu
    private State mState = State.CLOSED;
    private Item mActionItem = null;
    private List<IMenu.IItem> mItems = new ArrayList<>();

    // anchor
    private Anchor mAnchor = Anchor.CENTER;
    private int mAnchorOffsetX = 0;
    private int mAnchorOffsetY = 0;

    // radius
    private float mMenuRadiusMax = INVALID_RADIUS;
    private float mMenuRadiusMin = INVALID_RADIUS;
    private float mMenuRadius = INVALID_RADIUS;

    private float mItemRadius = INVALID_RADIUS;

    // angle
    private float mStartAngle = 180;
    private float mSweepAngle = 360;

    // menu color
    private final int DEFAULT_MENU_COLOR = Color.WHITE;

    // animated
    private Animator mAnimator;
    private IAnimatedAdapter<CircleMenu> mAnimatedAdapter = new CircleMenuAnimatorAdapter();        // 动画适配器

    // listener
    private OnStateChangeListener mStateChangeListener = null;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    public CircleMenu(Context context) {
        this(context, null);
    }

    public CircleMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setWillNotDraw(false);
        mPaint.setColor(DEFAULT_MENU_COLOR);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mMenuRadiusMax == INVALID_RADIUS) {
            mMenuRadiusMax = Math.min(w, h) / 2;
        }

        if (mMenuRadiusMin == INVALID_RADIUS) {
            mMenuRadiusMin = mMenuRadiusMax / 4;
        }

        if (mItemRadius == INVALID_RADIUS) {
            mItemRadius = mMenuRadiusMax * 0.7f;
        }

        if (getState() == State.CLOSED) {
            mMenuRadius = mMenuRadiusMin;

        } else if (getState() == State.OPENED) {
            mMenuRadius = mMenuRadiusMax;
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Point point = getAnchorPoint();
        int anchorX = point.x;
        int anchorY = point.y;

        if (mActionItem != null) {
            View actionView = mActionItem.getView();
            LayoutParams params = actionView.getLayoutParams();
            int width = mActionItem.getWidht();
            int height = mActionItem.getHeight();
            actionView.layout(anchorX - width / 2, anchorY - height / 2, anchorX + width / 2, anchorY + height / 2);
        }

        for (IItem item : mItems) {
            int horizontalSize = item.getWidht() / 2;
            int verticalSize = item.getHeight() / 2;
            View itemView = item.getView();
            itemView.layout(anchorX - horizontalSize, anchorY - verticalSize, anchorX + horizontalSize, anchorY + verticalSize);
        }

        if (mAnimatedAdapter != null) {
            Animator animator = mAnimatedAdapter.onLayout(this);
            if (animator != null) {
                animator.start();
            }
        }
    }

    @NonNull
    public Point getAnchorPoint() {
        int l = getLeft();
        int t = getTop();
        int r = getRight();
        int b = getBottom();

        int anchorX = mAnchorOffsetX;
        int anchorY = mAnchorOffsetY;

        switch (mAnchor) {
            case CENTER:
                anchorX += (r - l) / 2;
                anchorY += (b - t) / 2;
                break;
            case CENTER_LEFT:
                anchorX += l;
                anchorY += (b - t) / 2;
                break;
            case CENTER_TOP:
                anchorX += (r - l) / 2;
                anchorY += t;
                break;
            case CENTER_RIGHT:
                anchorX += r;
                anchorY += (b - t) / 2;
                break;
            case CENTER_BOTTOM:
                anchorX += (r - l) / 2;
                anchorY += b;
                break;
            case LEFT_TOP:
                anchorX += l;
                anchorY += t;
                break;
            case LEFT_BOTTOM:
                anchorX += l;
                anchorY += b;
                break;
            case RIGHT_TOP:
                anchorX += r;
                anchorY += t;
                break;
            case RIGHT_BOTTOM:
                anchorX += r;
                anchorY += b;
                break;
        }
        return new Point(anchorX, anchorY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mActionItem != null) {
            View actionView = mActionItem.getView();
            int anchorX = (actionView.getRight() + actionView.getLeft()) / 2;
            int anchorY = (actionView.getBottom() + actionView.getTop()) / 2;

            canvas.drawCircle(anchorX, anchorY, mMenuRadius, mPaint);
        }

    }

    @Override
    public void open(boolean animated) {
        State state = getState();
        if (state == State.OPENING || state == State.OPENED || state == State.START_OPEN) {
            return;
        }
        cancelAnimated();
        changeState(State.START_OPEN);

        mAnimator = mAnimatedAdapter.obtainOpenAnimator(this);
        if (mAnimator != null) {
            mAnimator.addListener(mMenuOpenListener);
            mAnimator.start();
            if (!animated) {
                mAnimator.end();
            }
        }
    }


    @Override
    public void close(boolean animated) {
        KLog.d(TAG, "animated = " + animated);
        State state = getState();
        KLog.d(TAG, "state = " + state.toString());
        if (state == State.CLOSING || state == State.CLOSED || state == State.START_CLOSE) {
            return;
        }
        cancelAnimated();
        changeState(State.START_CLOSE);
        mAnimator = mAnimatedAdapter.obtainCloseAnimator(this);
        if (mAnimator != null) {
            mAnimator.addListener(mMenuCloseListener);
            mAnimator.start();
            if (!animated) {
                mAnimator.end();
            }
        }
    }

    private void cancelAnimated() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    private void changeState(State state) {
        mState = state;
        if (mStateChangeListener != null) {
            mStateChangeListener.onMenuStateChange(this, state);
        }
    }

    @NonNull
    public Anchor getAnchor() {
        return mAnchor;
    }

    public void setAnchor(Anchor anchor) {
        if (anchor == null) {
            return;
        }
        if (mAnimator != null) {
            mAnimator.end();
        }
        mAnchor = anchor;
        requestLayout();
    }

    public int getAnchorOffsetX() {
        return mAnchorOffsetX;
    }

    public void setAnchorOffsetX(int anchorOffsetX) {
        mAnchorOffsetX = anchorOffsetX;
        requestLayout();
    }

    public int getAnchorOffsetY() {
        return mAnchorOffsetY;
    }

    public void setAnchorOffsetY(int anchorOffsetY) {
        mAnchorOffsetY = anchorOffsetY;
        requestLayout();
    }

    public Item getActionItem() {
        return mActionItem;
    }

    public void setActionItem(Item actionItem) {
        if (mActionItem != null) {
            removeView(mActionItem.getView());
        }

        mActionItem = actionItem;
        if (actionItem != null) {
            View actionView = actionItem.getView();
            if (!actionView.hasOnClickListeners()) {
                actionView.setOnClickListener(mActionViewClickListener);
            }
            addView(actionView);
        }
    }

    public List<IItem> getItems() {
        return mItems;
    }

    public void setItems(List<IItem> items) {
        if (items == null) {
            mItems.clear();
        } else {
            mItems = items;
        }

        for (IItem item : mItems) {
            addView(item.getView());
        }
    }

    public float getMenuRadius() {
        return mMenuRadius;
    }

    public void setMenuRadius(float menuRadius) {
        mMenuRadius = menuRadius;
    }

    @Override
    public void postInvalidate() {
        Point anchorPoint = getAnchorPoint();
        int left = (int) (anchorPoint.x - mMenuRadiusMax);
        int top = (int) (anchorPoint.y - mMenuRadiusMax);
        int right = (int) (anchorPoint.x + mMenuRadiusMax);
        int bottom = (int) (anchorPoint.y + mMenuRadiusMax);
        super.postInvalidate(left, top, right, bottom);
    }

    public float getMenuRadiusMax() {
        return mMenuRadiusMax;
    }

    public void setMenuRadiusMax(float menuRadiusMax) {
        mMenuRadiusMax = menuRadiusMax;
    }

    public float getMenuRadiusMin() {
        return mMenuRadiusMin;
    }

    public void setMenuRadiusMin(float menuRadiusMin) {
        mMenuRadiusMin = menuRadiusMin;
    }

    public float getItemRadius() {
        return mItemRadius;
    }

    public void setItemRadius(float itemRadius) {
        mItemRadius = itemRadius;
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(float startAngle) {
        mStartAngle = startAngle;
        requestLayout();
    }

    public float getSweepAngle() {
        return mSweepAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        mSweepAngle = sweepAngle;
        requestLayout();
    }

    public int getMenuColor() {
        return mPaint.getColor();
    }

    public void setMenuColor(int menuColor) {
        mPaint.setColor(menuColor);
    }

    public State getState() {
        return mState;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                State state = getState();
                if (state == State.OPENED || state == State.OPENING || state == State.START_OPEN) {
                    close(true);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public OnStateChangeListener getStateChangeListener() {
        return mStateChangeListener;
    }

    public void setStateChangeListener(OnStateChangeListener stateChangeListener) {
        mStateChangeListener = stateChangeListener;
    }

    /**
     * open animator listener
     */
    private final AnimatorListenerAdapter mMenuOpenListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            changeState(State.OPENING);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            changeState(State.OPENED);
        }
    };

    /**
     * close animator listener
     */
    private final AnimatorListenerAdapter mMenuCloseListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            KLog.d(TAG, "关闭动画开始");
            changeState(State.CLOSING);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            KLog.d(TAG, "关闭动画结束");
            changeState(State.CLOSED);

            // oppo手机 去掉下面代码老是显示有点问题，
            // item 动画
            if (getActionItem() != null && getActionItem().getView() != null) {
                View actionView = getActionItem().getView();
                List<IMenu.IItem> items = getItems();

                if (items != null && items.size() > 0) {
                    float anchorX = actionView.getX();
                    float anchorY = actionView.getY();
                    for (int i = 0; i < items.size(); i++) {
                        View itemView = items.get(i).getView();
                        itemView.setX(anchorX);
                        itemView.setY(anchorY);
                        itemView.setScaleX(0);
                        itemView.setScaleY(0);
                        itemView.setAlpha(0);
                        itemView.setVisibility(View.GONE);
                    }
                }
            }
            // menu
            setMenuRadius(mMenuRadiusMin);
            // action item 动画
            if (getActionItem() != null && getActionItem().getView() != null) {
                ImageView actionView = (ImageView) getActionItem().getView();
                actionView.setImageResource(R.drawable.btn_icon_circle_menu_collected_selector);
            }
        }
    };

    private final OnClickListener mActionViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (getState()) {
                case OPENED:
                    close(true);
                    break;
                case CLOSED:
                    open(true);
                    break;
            }
        }
    };

    public static class Item implements IItem {
        private final View mView;
        private int mWidth;
        private int mHeight;

        public Item(View view, int width, int height) {
            mView = view;
            mWidth = width;
            mHeight = height;
        }

        @Override
        public int getWidht() {
            return mWidth;
        }

        @Override
        public int getHeight() {
            return mHeight;
        }

        @Override
        public View getView() {
            return mView;
        }
    }
}
