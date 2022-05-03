package com.wwwjf.wanandroidkt.behavior

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView

class ScrollBehavior():CoordinatorLayout.Behavior<RecyclerView>() {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: RecyclerView,
        dependency: View
    ): Boolean {
        return super.layoutDependsOn(parent, child, dependency)
    }
}