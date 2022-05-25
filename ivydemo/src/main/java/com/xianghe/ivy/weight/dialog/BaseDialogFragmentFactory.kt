package com.xianghe.ivy.weight.dialog

import android.os.Bundle
import androidx.annotation.LayoutRes

/**
 * @Author:Ycl
 * @Date:2017-09-14 10:38
 * @Desc:  baseDialog 工厂类
 */

class BaseDialogFragmentFactory : BaseDialogFragment() {
    private var convertListener: ViewConvertListener? = null

    override fun intLayoutId(): Int {
        return layoutId
    }

    override fun convertView(holder: ViewHolder, dialog: BaseDialogFragment) {
            convertListener?.convertView(holder, dialog)
    }


    fun setLayoutId(@LayoutRes layoutId: Int): BaseDialogFragmentFactory {
        this.layoutId=layoutId
        return this
    }

    fun setConvertListener(convertListener: ViewConvertListener): BaseDialogFragmentFactory {
        this.convertListener = convertListener
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            convertListener = savedInstanceState.getParcelable<ViewConvertListener>("listener")
        }
    }

    /**
     * 保存接口

     * @param outState
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("listener", convertListener)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        convertListener = null
    }

    companion object {

        fun init(): BaseDialogFragmentFactory {
            return BaseDialogFragmentFactory()
        }
    }
}
