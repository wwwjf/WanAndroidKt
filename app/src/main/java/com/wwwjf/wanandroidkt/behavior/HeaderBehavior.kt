package com.wwwjf.wanandroidkt.behavior

import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout

class HeaderBehavior(): CoordinatorLayout.Behavior<TextView>() {


    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: TextView,
        layoutDirection: Int
    ): Boolean {

        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: TextView,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return super.onStartNestedScroll(
            coordinatorLayout,
            child,
            directTargetChild,
            target,
            axes,
            type
        )
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: TextView,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }
}