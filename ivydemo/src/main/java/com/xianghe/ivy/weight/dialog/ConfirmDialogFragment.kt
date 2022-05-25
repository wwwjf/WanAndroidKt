package com.xianghe.ivy.weight.dialog

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.xianghe.ivy.R


/**
 * @Author:  Ycl
 * @Date:  2018-01-24 15:52
 * @Desc:
 */
class ConfirmDialogFragment : BaseDialogFragment() {
    init {
        isCancelable = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setMargin(60)
        super.onCreate(savedInstanceState)
    }

    override fun intLayoutId(): Int = R.layout.dialog_makesure

    override fun convertView(holder: ViewHolder, dialog: BaseDialogFragment) {
        with(holder) {
            holder.getView<TextView>(R.id.dl_tv_content).run {
                text = "title"
            }
            holder.setOnClickListener(R.id.dl_btn_confirm, View.OnClickListener { dialog.dismiss() })
        }
    }
}