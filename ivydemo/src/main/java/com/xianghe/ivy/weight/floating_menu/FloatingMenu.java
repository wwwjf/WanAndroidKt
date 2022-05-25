package com.xianghe.ivy.weight.floating_menu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xianghe.ivy.R;
import com.xianghe.ivy.weight.floating_menu.animation.DefaultAnimationHandler;
import com.xianghe.ivy.weight.floating_menu.animation.MenuAnimationHandler;

import java.util.ArrayList;
import java.util.Iterator;

public class FloatingMenu {
    private View mainActionView;
    private int startAngle;
    private int endAngle;
    private int radius;
    private ArrayList<Item> subActionItems;
    private MenuAnimationHandler animationHandler;
    private FloatingMenu.MenuStateChangeListener stateChangeListener;
    private boolean animated;
    private boolean open;

    public FloatingMenu(View mainActionView, int startAngle, int endAngle, int radius, ArrayList<FloatingMenu.Item> subActionItems, MenuAnimationHandler animationHandler, boolean animated, FloatingMenu.MenuStateChangeListener stateChangeListener) {
        this.mainActionView = mainActionView;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.radius = radius;
        this.subActionItems = subActionItems;
        this.animationHandler = animationHandler;
        this.animated = animated;
        this.open = false;
        this.stateChangeListener = stateChangeListener;
        this.mainActionView.setClickable(true);
        this.mainActionView.setOnClickListener(new FloatingMenu.ActionViewClickListener());
        if (animationHandler != null) {
            animationHandler.setMenu(this);
        }

        Iterator i$ = subActionItems.iterator();

        while (true) {
            FloatingMenu.Item item;
            do {
                if (!i$.hasNext()) {
                    return;
                }

                item = (FloatingMenu.Item) i$.next();
            } while (item.width != 0 && item.height != 0);

            ((ViewGroup) this.getActivityContentView()).addView(item.view);
            item.view.setAlpha(0.0F);
            item.view.post(new FloatingMenu.ItemViewQueueListener(item));
        }
    }

    public void open(boolean animated) {
        Point center = this.getActionViewCenter();

        this.calculateItemPositions();
        int i;
        FrameLayout.LayoutParams params;

        if (animated && this.animationHandler != null) {
            if (this.animationHandler.isAnimating()) {
                return;
            }

            for (i = 0; i < this.subActionItems.size(); ++i) {
                if (((FloatingMenu.Item) this.subActionItems.get(i)).view.getParent() != null) {
                    throw new RuntimeException("All of the sub action items have to be independent from a parent.");
                }

                params = new FrameLayout.LayoutParams(((FloatingMenu.Item) this.subActionItems.get(i)).width, ((FloatingMenu.Item) this.subActionItems.get(i)).height, 51);
                params.setMargins(center.x - ((FloatingMenu.Item) this.subActionItems.get(i)).width / 2, center.y - ((FloatingMenu.Item) this.subActionItems.get(i)).height / 2, 0, 0);
                ((ViewGroup) this.getActivityContentView()).addView(((FloatingMenu.Item) this.subActionItems.get(i)).view, params);
            }

            this.animationHandler.animateMenuOpening(center);
        } else {
            for (i = 0; i < this.subActionItems.size(); ++i) {
                params = new FrameLayout.LayoutParams(((FloatingMenu.Item) this.subActionItems.get(i)).width, ((FloatingMenu.Item) this.subActionItems.get(i)).height, 51);
                params.setMargins(((FloatingMenu.Item) this.subActionItems.get(i)).x, ((FloatingMenu.Item) this.subActionItems.get(i)).y, 0, 0);
                ((FloatingMenu.Item) this.subActionItems.get(i)).view.setLayoutParams(params);
                ((ViewGroup) this.getActivityContentView()).addView(((FloatingMenu.Item) this.subActionItems.get(i)).view, params);
            }
        }
        this.open = true;
        if (this.stateChangeListener != null) {
            this.stateChangeListener.onMenuOpened(this);
        }

    }

    public void close(boolean animated) {
        if (animated && this.animationHandler != null) {
            if (this.animationHandler.isAnimating()) {
                return;
            }

            this.animationHandler.animateMenuClosing(this.getActionViewCenter());
        } else {
            for (int i = 0; i < this.subActionItems.size(); ++i) {
                ((ViewGroup) this.getActivityContentView()).removeView(((FloatingMenu.Item) this.subActionItems.get(i)).view);
            }
        }

        this.open = false;
        if (this.stateChangeListener != null) {
            this.stateChangeListener.onMenuClosed(this);
        }

    }

    public void toggle(boolean animated) {
        if (this.open) {
            this.close(animated);
        } else {
            /**
             *  java.lang.RuntimeException: All of the sub action items have to be independent from a parent.
             *         at com.xianghe.ivy.weight.floating_menu.FloatingMenu.open(FloatingMenu.java:82)
             *         at com.xianghe.ivy.weight.floating_menu.FloatingMenu.toggle(FloatingMenu.java:130)
             *         at com.xianghe.ivy.weight.floating_menu.FloatingMenu$ActionViewClickListener.onClick(FloatingMenu.java:355)
             */
            try {
                this.open(animated);
            } catch (Exception e) {
                e.printStackTrace();
                // 异常暂时不处理
            }
        }
    }

    public boolean isOpen() {
        return this.open;
    }

    public void updateItemPositions() {
        if (this.isOpen()) {
            this.calculateItemPositions();

            for (int i = 0; i < this.subActionItems.size(); ++i) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(((FloatingMenu.Item) this.subActionItems.get(i)).width, ((FloatingMenu.Item) this.subActionItems.get(i)).height, 51);
                params.setMargins(((FloatingMenu.Item) this.subActionItems.get(i)).x, ((FloatingMenu.Item) this.subActionItems.get(i)).y, 0, 0);
                ((FloatingMenu.Item) this.subActionItems.get(i)).view.setLayoutParams(params);
            }

        }
    }

    private Point getActionViewCoordinates() {
        int[] coords = new int[2];
        this.mainActionView.getLocationInWindow(coords);
        Rect activityFrame = new Rect();
        this.getActivityContentView().getWindowVisibleDisplayFrame(activityFrame);
        coords[0] -= this.getScreenSize().x - this.getActivityContentView().getMeasuredWidth();
        coords[1] -= activityFrame.height() + activityFrame.top - this.getActivityContentView().getMeasuredHeight();
        return new Point(coords[0], coords[1]);
    }

    public Point getActionViewCenter() {
//        Point point = this.getActionViewCoordinates();
//        point.x += this.mainActionView.getMeasuredWidth() / 2;
//        point.y += this.mainActionView.getMeasuredHeight() / 2;

        // by luwei -- Start --
        // 这样修改，mainActionView 只能放在与 contentView 重叠的布局上。
        // 否则可能会出现错乱的效果
        Point point = new Point();
        point.x = (int) (mainActionView.getX() + mainActionView.getMeasuredWidth() / 2);
        point.y = (int) (mainActionView.getY() + mainActionView.getMeasuredHeight() / 2);
        // by luwei --  End  --
        return point;
    }

    private void calculateItemPositions() {
        Point center = this.getActionViewCenter();
        RectF area = new RectF((float) (center.x - this.radius), (float) (center.y - this.radius), (float) (center.x + this.radius), (float) (center.y + this.radius));
        Path orbit = new Path();
        orbit.addArc(area, (float) this.startAngle, (float) (this.endAngle - this.startAngle));
        PathMeasure measure = new PathMeasure(orbit, false);
        int divisor;
        if (Math.abs(this.endAngle - this.startAngle) < 360 && this.subActionItems.size() > 1) {
            divisor = this.subActionItems.size() - 1;
        } else {
            divisor = this.subActionItems.size();
        }

        for (int i = 0; i < this.subActionItems.size(); ++i) {
            float[] coords = new float[]{0.0F, 0.0F};
            measure.getPosTan((float) i * measure.getLength() / (float) divisor, coords, (float[]) null);
            ((FloatingMenu.Item) this.subActionItems.get(i)).x = (int) coords[0] - ((FloatingMenu.Item) this.subActionItems.get(i)).width / 2;
            ((FloatingMenu.Item) this.subActionItems.get(i)).y = (int) coords[1] - ((FloatingMenu.Item) this.subActionItems.get(i)).height / 2;
        }

    }

    public int getRadius() {
        return this.radius;
    }

    public ArrayList<FloatingMenu.Item> getSubActionItems() {
        return this.subActionItems;
    }

    public View getActivityContentView() {
        return ((Activity) this.mainActionView.getContext()).getWindow().getDecorView().findViewById(android.R.id.content);
    }

    private Point getScreenSize() {
        Point size = new Point();
        ((Activity) this.mainActionView.getContext()).getWindowManager().getDefaultDisplay().getSize(size);
        return size;
    }

    public void setStateChangeListener(FloatingMenu.MenuStateChangeListener listener) {
        this.stateChangeListener = listener;
    }

    public static class Builder {
        private int startAngle;
        private int endAngle;
        private int radius;
        private View actionView;
        private ArrayList<FloatingMenu.Item> subActionItems = new ArrayList();
        private MenuAnimationHandler animationHandler;
        private boolean animated;
        private FloatingMenu.MenuStateChangeListener stateChangeListener;

        public Builder(Activity activity) {
            this.radius = activity.getResources().getDimensionPixelSize(R.dimen.action_menu_radius);
            this.startAngle = 180;
            this.endAngle = 270;
            this.animationHandler = new DefaultAnimationHandler();
            this.animated = true;

        }

        public FloatingMenu.Builder setStartAngle(int startAngle) {
            this.startAngle = startAngle;
            return this;
        }

        public FloatingMenu.Builder setEndAngle(int endAngle) {
            this.endAngle = endAngle;
            return this;
        }

        public FloatingMenu.Builder setRadius(int radius) {
            this.radius = radius;
            return this;
        }

        public FloatingMenu.Builder addSubActionView(View subActionView, int width, int height) {
            this.subActionItems.add(new FloatingMenu.Item(subActionView, width, height));
            return this;
        }

        public FloatingMenu.Builder addSubActionView(View subActionView) {
            return this.addSubActionView(subActionView, 0, 0);
        }

        public FloatingMenu.Builder addSubActionView(int resId, Context context) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(resId, (ViewGroup) null, false);
            view.measure(0, 0);
            return this.addSubActionView(view, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        public FloatingMenu.Builder setAnimationHandler(MenuAnimationHandler animationHandler) {
            this.animationHandler = animationHandler;
            return this;
        }

        public FloatingMenu.Builder enableAnimations() {
            this.animated = true;
            return this;
        }

        public FloatingMenu.Builder disableAnimations() {
            this.animated = false;
            return this;
        }

        public FloatingMenu.Builder setStateChangeListener(FloatingMenu.MenuStateChangeListener listener) {
            this.stateChangeListener = listener;
            return this;
        }

        public FloatingMenu.Builder attachTo(View actionView) {
            this.actionView = actionView;
            return this;
        }

        public FloatingMenu build() {
            return new FloatingMenu(this.actionView, this.startAngle, this.endAngle, this.radius, this.subActionItems, this.animationHandler, this.animated, this.stateChangeListener);
        }
    }

    public interface MenuStateChangeListener {
        void onMenuOpened(FloatingMenu var1);

        void onMenuClosed(FloatingMenu var1);
    }

    public static class Item {
        public int x;
        public int y;
        public int width;
        public int height;
        public View view;

        public Item(View view, int width, int height) {
            this.view = view;
            this.width = width;
            this.height = height;
            this.x = 0;
            this.y = 0;
        }
    }

    private class ItemViewQueueListener implements Runnable {
        private static final int MAX_TRIES = 10;
        private FloatingMenu.Item item;
        private int tries;

        public ItemViewQueueListener(FloatingMenu.Item item) {
            this.item = item;
            this.tries = 0;
        }

        public void run() {
            if (this.item.view.getMeasuredWidth() == 0 && this.tries < 10) {
                this.item.view.post(this);
            } else {
                this.item.width = this.item.view.getMeasuredWidth();
                this.item.height = this.item.view.getMeasuredHeight();
                this.item.view.setAlpha(1.0F);
                ((ViewGroup) FloatingMenu.this.getActivityContentView()).removeView(this.item.view);
            }
        }
    }

    public class ActionViewClickListener implements View.OnClickListener {

        private long lastClick;

        public void onClick(View v) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClick < 400) {
                return;
            }
            lastClick = currentTime;
            FloatingMenu.this.toggle(FloatingMenu.this.animated);
        }
    }
}