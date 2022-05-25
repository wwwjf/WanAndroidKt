package com.xianghe.ivy.mvp.loadPager

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import android.widget.Toolbar
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.Unbinder
import com.xianghe.ivy.R
import com.xianghe.ivy.http.request.RequestError
import com.xianghe.ivy.mvp.BasePresenter
import com.xianghe.ivy.mvp.IBaseConctact
import com.xianghe.ivy.mvp.LoadingPager
import com.xianghe.ivy.ui.base.DownloadActivity
import org.jetbrains.anko.findOptional
import org.jetbrains.anko.toast

/**
 * @Author:  ycl
 * @Date:  2018/10/23 10:50
 * @Desc:
 */
abstract class BaseMVPLoadingActivity<V : IBaseConctact.IBaseView, T : BasePresenter<V>> : DownloadActivity<V, T>() {

    @JvmField
    protected var mUnbinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initContentView(savedInstanceState)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (isNeedButterKnife()) {
            mUnbinder?.unbind()
        }
    }


    abstract fun initData(savedInstanceState: Bundle?)
    abstract fun initListener()

    open fun isNeedToolBar(): Boolean = false
    open fun isNeedRefresh(): Boolean = false
    open fun isNeedButterKnife(): Boolean = false

    // -------------------  视图封装 start ----------------------
    protected fun initContentView(savedInstanceState: Bundle?) {
        val content = onCreateContentView(savedInstanceState)
        addBaseContentView()
        when (content) {
            is Int, is View -> {
                if (isNeedToolBar()) {
                    findOptional<ViewStub>(R.id.toolBarStub)?.run {
                        // 对其子控件动态设置高度即可
                        /*val params = layoutParams
                        params.height = 200
                        layoutParams = params*/
                        val toolBarId = onCreateToolBar()
                        if (toolBarId != 0) {
                            layoutResource = toolBarId
                        }
                        inflate()

                    }
                }
                if (content is Int) {
                    if (content == 0) {
                        getUIController()?.showSuccess()
                    } else {
                        getUIController()?.setSuccessView(content)
                    }
                }
                if (content is View) {
                    getUIController()?.addView(content)
                }
            }
            is Fragment -> {
                getUIController()?.showSuccess()
                supportFragmentManager
                        .beginTransaction()
                        .add(R.id.uiController, content)
                        .commit()
            }
        }
        if (isNeedButterKnife()) {
            mUnbinder = ButterKnife.bind(this)
        }
        initData(savedInstanceState)
        initListener()
    }

    private fun addBaseContentView() {


        if (isNeedToolBar()) {
            if (isNeedRefresh()) {
                setContentView(R.layout.activity_base_refresh)
            } else {
                setContentView(R.layout.activity_base_no_refresh)
            }
        } else {
            if (isNeedRefresh()) {
                setContentView(R.layout.activity_base_refresh_no_toolbar)
            } else {
                setContentView(R.layout.activity_base_no_refresh_no_toolbar)
            }
        }
    }

    abstract fun onCreateContentView(bundle: Bundle?): Any

//    open fun displayHomeAsUpEnable(): Boolean = true

    // -------------------  视图封装 end ----------------------


    // -------------------- getView  start  --------------------

    open fun getUIController(): LoadingPager? {
        return findOptional(R.id.uiController)
    }

    open fun onCreateToolBar(): Int = R.layout.layout_tool_bar
    open fun getToolBar() = findOptional<Toolbar>(R.id.toolbar)

    // -------------------- getView  end  --------------------


    // -------------------- 视图部分  start  --------------------

    fun changeUIState(change: LoadingPager.() -> Unit) {
        getUIController()?.change()
    }

    fun showLoading() {
        getUIController()?.showLoading()
    }

    fun dismissLoading() {
        showSuccess()
    }

    fun showEmpty() {
        getUIController()?.showEmpty()
    }

    fun showEmptyView(view: View) {
        getUIController()?.setEmptyView(view)
    }

    fun showEmptyView(@LayoutRes layoutResource: Int) {
        getUIController()?.setEmptyView(layoutResource)
    }


    fun showError() {
        getUIController()?.showError()
    }

    fun showErrorView(view: View) {
        getUIController()?.setErrorView(view)
    }

    fun showErrorView(@LayoutRes layoutResource: Int) {
        getUIController()?.setErrorView(layoutResource)
    }

    fun setLoadingView(@LayoutRes layoutResource: Int) {
        getUIController()?.setLoadingView(layoutResource)
    }

    fun setLoadingView(view: View) {
        getUIController()?.setLoadingView(view)
    }

    fun showSuccess() {
        getUIController()?.showSuccess()
    }
    // -------------------- 视图部分  end  --------------------


    private fun <E> parseError(e: E): String {
        return if (e is String) {
            e
        } else if (e is Throwable && e.message != null) {
            e.printStackTrace()
            e.message!!
        } else if (e is Exception && e.message != null) {
            e.printStackTrace()
            e.message!!
        } else if (e is RequestError && e.message != null) {
            e.printStackTrace()
            e.message!!
        } else {
            e.toString()
        }
    }

    open fun <E> onError(e: E?) {
        if (!this@BaseMVPLoadingActivity.isFinishing) {
            if (e != null) {
                try {
                    parseError(e).let {
                        if (it != null && it.isNotEmpty()) {
                            toast(it)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    open fun <D> onSuccess(d: D?) {

    }


    open override fun onDownLoadListCollapsed() {

    }

    open override fun onDownLoadListExpanded() {

    }

    open override fun onDownLoadListDragging() {

    }

    open override fun onDownLoadListSetting() {

    }

    open override fun onDownLoadListHidden() {

    }

    open override fun onDownLoadListHalfExpanded() {

    }
}
