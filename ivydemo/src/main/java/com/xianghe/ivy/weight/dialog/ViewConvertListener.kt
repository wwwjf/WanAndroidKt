package com.xianghe.ivy.weight.dialog

import android.os.Parcel
import android.os.Parcelable

/**
 * @Author:Ycl
 * @Date:2017-09-14 10:38
 * @Desc:  baseDialog绑定的监听抽象类
 */

abstract class ViewConvertListener : Parcelable {

    internal abstract fun convertView(holder: ViewHolder, dialog: BaseDialogFragment)

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {}

    constructor() {}

    protected constructor(`in`: Parcel) {}

    companion object {
        val CREATOR: Parcelable.Creator<ViewConvertListener> = object : Parcelable.Creator<ViewConvertListener> {
            override fun createFromParcel(source: Parcel): ViewConvertListener {
                return object : ViewConvertListener(source) {
                    override fun convertView(holder: ViewHolder, dialog: BaseDialogFragment) {

                    }
                }
            }

            override fun newArray(size: Int): Array<ViewConvertListener?> {
                return arrayOfNulls(size)
            }
        }
    }
}
