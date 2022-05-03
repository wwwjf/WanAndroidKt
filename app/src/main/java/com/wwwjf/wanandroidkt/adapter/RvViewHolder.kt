package com.wwwjf.wanandroidkt.adapter

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.wwwjf.wanandroidkt.R

class RvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var mContext: Context
    private lateinit var mConvertView: View
    private lateinit var mViews: SparseArray<View>

    constructor(context: Context, itemView: View) : this(itemView) {
        mContext = context
        mConvertView = itemView
        mViews = SparseArray<View>()
    }

    companion object {

        @JvmStatic
        fun get(context: Context, parent: ViewGroup, layoutId: Int): RvViewHolder {
            val itemView = LayoutInflater.from(context).inflate(layoutId, parent, false)
            return RvViewHolder(context, itemView)
        }
    }

    fun <T:View> getView(viewId:Int):T{

        var view = mViews.get(viewId)
        if (view == null){
            view = mConvertView.findViewById(viewId)
            mViews.put(viewId,view)
        }
        return view as T
    }

}