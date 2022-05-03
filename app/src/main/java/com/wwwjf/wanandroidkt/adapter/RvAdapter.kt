package com.wwwjf.wanandroidkt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlin.properties.Delegates

abstract class RvAdapter<T>(): RecyclerView.Adapter<RvViewHolder>() {

    private lateinit var mContext: Context
    private var mLayoutId by Delegates.notNull<Int>()
    private lateinit var mDataList: MutableList<T>

    constructor(context:Context, layoutId:Int, data:MutableList<T>) : this() {

        mContext = context
        mLayoutId = layoutId
        mDataList = data
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvViewHolder {

        return RvViewHolder.get(mContext,parent,mLayoutId)
    }

    override fun onBindViewHolder(holder: RvViewHolder, position: Int) {

        convert(holder,mDataList.get(position))
    }


    abstract fun convert(holder: RvViewHolder, t: T)

    override fun getItemCount(): Int = mDataList.size
}