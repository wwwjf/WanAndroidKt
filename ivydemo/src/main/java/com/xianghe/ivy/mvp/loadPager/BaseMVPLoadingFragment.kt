package com.xianghe.ivy.mvp.loadPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.birdsport.cphome.extension.toast
import com.xianghe.ivy.R
import com.xianghe.ivy.mvp.BaseFragment
import com.xianghe.ivy.mvp.BasePresenter
import com.xianghe.ivy.mvp.IBaseConctact
import com.xianghe.ivy.mvp.LoadingPager

import org.jetbrains.anko.findOptional

/**
 * @Author:  ycl
 * @Date:  2018/10/23 11:07
 * @Desc:
 */
abstract class BaseMVPLoadingFragment<V : IBaseConctact.IBaseView, T : BasePresenter<V>> : BaseFragment() {

    @JvmField
    protected var mPresenter: T? = null

    protected var isUIVisible = false//是否可见

    protected var isPrepared = false//view是否创建完成

    protected var isLoaded = false//是否已经加载过数据


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
    }

    private fun initPresenter() {
        mPresenter = createPresenter()
        mPresenter?.attachView(this as V)
    }

    override fun onDestroy() {
        mPresenter?.detachView()
        super.onDestroy()
    }

    abstract fun createPresenter(): T?
    abstract fun initEvent()
    abstract fun initData()
    open fun isNeedRefresh(): Boolean = false
    open fun isNeedLazyLoad() = false


    // -------------------  视图封装 start ----------------------

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val content = onCreateContentView(inflater, container, savedInstanceState)
        when (content) {
            is Int, is View -> {
                val root = inflateBaseContentView(inflater, container)
                root.isClickable = true//设置根布局可以点击，这样点击事件不会传递给后面的层级
                val uiController =
                        root.findOptional<LoadingPager>(R.id.uiController)
                if (content is Int) {
                    if (content == 0) {
                        showSuccess()
                    } else {
                        uiController?.setSuccessView(content)
                    }
                }
                if (content is View) {
                    if (content.layoutParams == null) {
                        content.layoutParams = ViewGroup.MarginLayoutParams(
                                ViewGroup.MarginLayoutParams.MATCH_PARENT,
                                ViewGroup.MarginLayoutParams.MATCH_PARENT)
                    }
                    uiController?.setSuccessView(content)
                }
                uiController?.setLoadingCallback { initData() }
                isPrepared = true
                isLoaded = false
                return root
            }
            else -> return null
        }
    }

    abstract fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup?,
                                     state: Bundle?): Any


    protected open fun inflateBaseContentView(inflater: LayoutInflater,
                                              container: ViewGroup?): View {
        return if (isNeedRefresh()) {
            inflater.inflate(R.layout.fragment_base_refresh, container, false)
                    .apply {
                        findOptional<SwipeRefreshLayout>(R.id.refreshLayout)
                                ?.setOnRefreshListener {

                                }
                        findOptional<SwipeRefreshLayout>(R.id.refreshLayout)
                                ?.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary)

                    }
        } else {
            inflater.inflate(R.layout.fragment_base_no_refresh, container, false)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!isNeedLazyLoad() && !isLoaded) {
            initFun()
        } else if (isLoaded) {
            getUIController()?.showSuccess()
        }
    }

    open fun initFun() {
        isLoaded = true
        initEvent()
        getUIController()?.loadData() ?: initData()
    }

    // Fragment 数据的缓加载(可见时才进行数据加载操作)
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isUIVisible = isVisibleToUser
        if (isUIVisible && isPrepared) {
            onUIVisible()
        } else {
            onUIInvisible()
        }
    }

    @Synchronized
    open fun onUIVisible() {
        val loaded = isLoaded
        val prepared = isPrepared
        val uiVisible = isUIVisible
        if (!loaded && prepared && uiVisible) {
            isLoaded = true
            initFun()
        } else {
            onUIVisibleRefresh()
        }
    }

    open fun onUIVisibleRefresh() {

    }

    open fun onUIInvisible() {

    }


    // -------------------  视图封装 end ----------------------


    // -------------------- getView  start  --------------------

    protected fun getUIController() = view?.findOptional<LoadingPager>(R.id.uiController)

    open fun getRefreshLayout() = view?.findOptional<SwipeRefreshLayout>(R.id.refreshLayout)

    // -------------------- getView  end  --------------------


    // -------------------- 视图部分  start  --------------------
    fun showLoading() {
        getUIController()?.showLoading()
    }

    fun showLoadingView(view: View) {
        getUIController()?.setLoadingView(view)
    }

    fun dismissLoading() {
        getUIController()?.showSuccess()
        getRefreshLayout()?.isRefreshing = false
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

    fun showSuccess() {
        getUIController()?.showSuccess()
    }

    // -------------------- 视图部分  end  --------------------


    protected fun <E> parseError(e: E): String {
        if (e is String) {
            return e
        } else if (e is Throwable && e.message != null) {
            e.printStackTrace()
            return e.message!!
        } else if (e is Exception && e.message != null) {
            e.printStackTrace()
            return e.message!!
        } else {
            return e.toString()
        }
    }

    protected fun <E> showToast(e: E?) {
        if (e != null) {
            try {
                parseError(e).let {
                    if (it != null && it.isNotEmpty()) {
                        toast(it)
//                        Toasty.normal(getUIController()!!.context,it).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    open fun <D> onSuccess(d: D?) {
    }

    open fun <E> onError(e: E?) {
        showToast(e)
    }
}

