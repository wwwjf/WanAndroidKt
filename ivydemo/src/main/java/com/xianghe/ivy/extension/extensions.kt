package com.birdsport.cphome.extension

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

/**
 * @Author:Ycl
 * @Date:2017-03-03 18:19
 * @Desc:
 */
fun Toast.show(message: String) {
    setText(message)
    duration = Toast.LENGTH_SHORT
    show()
}

fun Toast.show(message: Int) {
    view?.let {
        show(view!!.context.getString(message))}
}

fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(message: Int) {
    Toast.makeText(this, this.getString(message), Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(message: Int) {
    Toast.makeText(this.context, this.getString(message), Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(message: String) {
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
}


@Suppress("UNCHECKED_CAST")
fun <S, R> S.support(versionNO: Int, support: S.(S) -> R, nonsupport: S.(S) -> R = { Unit as R }): R {
    return if (Build.VERSION.SDK_INT >= versionNO) this.support(this) else this.nonsupport(this)
}

@Suppress("UNCHECKED_CAST")
fun <S, R> S.support(versionNO: Int, support: S.(S) -> R): R {
    return support(versionNO, support, { Unit as R })
}

/** show/hide status bar must call before setContentView,otherwise it's not working
 * */
fun Activity.fullscreen(fullscreen: Boolean = true, showStatusBar: Boolean = false) {
    if (fullscreen) {
        //全屏显示
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (showStatusBar) {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
}

fun Activity.adaptActivity(userSizeAdapter: Boolean = true) {
    if (userSizeAdapter) {
        //SizeAdapterUtils.adaptActivity(IvyApp.getInstance(), this)
    }
}

/**
 *  由于某些原因, 屏幕旋转后 Fragment 的重建, 会导致框架对 Fragment 的自定义适配参数失去效果
所以如果您的 Fragment 允许屏幕旋转, 则请在 onCreateView 手动调用一次 AutoSize.autoConvertDensity()
 */
fun Fragment.adaptFragment(userSizeAdapter: Boolean = true) {
    if (userSizeAdapter) {
        //SizeAdapterUtils.adaptActivity(IvyApp.getInstance(), this.activity)
    }
}

/**
 *  isTranslucentStatusBar   控制是否沉浸
 *  color   未沉浸时，设置stateBar的颜色
 */
fun Activity.isTranslucentStatusBar(isTranslucentStatusBar: Boolean, color: Int = -1) {
    // 是否开启沉浸模式
    if (isTranslucentStatusBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0以上使用原生方法 去除黑色遮罩
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        return
    }
    // 设置状态栏颜色
    if (color != -1) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0以上使用原生方法
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, color)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4-5.0使用三方工具类
//            val tintManager = SystemBarTintManager(this)
//            tintManager.isStatusBarTintEnabled = true
//            tintManager.setStatusBarTintColor(ContextCompat.getColor(this, color))
        }
    }
}

/**
 * id:资源id，args:字符串资源需要进行格式化时传入
 *@author Chris
 *created at 2017/2/9 009
 */
fun TextView.text(id: Int = -1, vararg args: Any) {
    if (id > 0) {
        if (args.isNotEmpty()) {
            text = context.getString(id, *args)
        } else {
            text = context.getString(id)
        }
    }
}

fun EditText.text(id: Int = -1, vararg args: Any) {
    if (id > 0) {
        if (args.isNotEmpty()) {
            setText(context.getString(id, *args))
        } else {
            setText(context.getString(id))
        }
    }
}

/**
 *  ttfPath 格式 : "fonts/XXX.ttf"
 */
fun TextView.setTypeface(ttfPath: String? = null) {
    if (ttfPath == null) {
        this.typeface = Typeface.createFromAsset(this.context.assets, "fonts/AccidentalPresidency.ttf")
    } else {
        this.typeface = Typeface.createFromAsset(this.context.assets, ttfPath)
    }
}

fun TextView.setDefaultTypeface() {
    this.typeface = Typeface.DEFAULT
}

fun TextView.setBold() {
    this.paint.isFakeBoldText = true
}

/**
 * 代码设置textView的textAppearance
 *@author Chris
 *created at 2017/2/9 009
 */
fun TextView.textAppearance(resId: Int = -1) {
    if (resId != -1) TextViewCompat.setTextAppearance(this, resId)
}


fun RecyclerView.clearDefaultItemAnimator() {
    this.itemAnimator.apply {
        this!!.addDuration = 0
        changeDuration = 0
        moveDuration = 0
        removeDuration = 0
    }
    (this.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
}

/**
 * 设置view可见，如果设置返回true，不需要设置返回false-->本身就是可见的
 *@author Chris
 *created at 2016/10/24 024
 */
fun View.visible(): Boolean {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
        return true
    } else {
        return false
    }
}

/**
 * 设置view不可见，如果设置返回true，不需要设置返回false-->本身就是不可见的
 *@author Chris
 *created at 2016/10/24 024
 */
fun View.invisible(): Boolean {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
        return true
    } else {
        return false
    }
}

/**
 * 设置view不可见，如果设置返回true，不需要设置返回false-->本身就是不可见的
 *@author Chris
 *created at 2016/10/24 024
 */
fun View.gone(): Boolean {
    if (visibility != View.GONE) {
        visibility = View.GONE
        return true
    } else {
        return false
    }
}

/*fun ImageView.showImageBlur(url: String, radio: Int = 10, placeDrawable: Int = 0, errorDrawable: Int = 0): Unit {
    ImageLoaderUtils.displayImageBlur(this, url, radio,
            if (placeDrawable != 0) this.context.resources.getDrawable(placeDrawable) else null,
            if (errorDrawable != 0) this.context.resources.getDrawable(errorDrawable) else null)
}

fun ImageView.showImage(url: String, placeDrawable: Int, errorDrawable: Int): Unit {
    ImageLoaderUtils.displayImage(this, url,
            if (placeDrawable != 0) this.context.resources.getDrawable(placeDrawable) else null,
            if (errorDrawable != 0) this.context.resources.getDrawable(errorDrawable) else null)
}

fun CircleImageView.showImage(url: String, placeDrawable: Int, errorDrawable: Int): Unit {
    ImageLoaderUtils.displayImage(this, url,
            if (placeDrawable != 0) this.context.resources.getDrawable(placeDrawable) else null,
            if (errorDrawable != 0) this.context.resources.getDrawable(errorDrawable) else null)
}*/


fun Context.checkNetwork(): Boolean {
    val conn: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = conn.activeNetworkInfo
    if (info?.isAvailable != true) return false
    else {
        when (info.type) {
            ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_MOBILE ->
                return info.isConnected
            else -> return false
        }
    }
}

fun Context.keyboardToggle(view: View, show: Boolean = false) {
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (show) {
        view.requestFocus()
        inputManager.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    } else {
        view.clearFocus()
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun View.keyboardToggle(show: Boolean = false, view: View? = null) {
    context.keyboardToggle(view ?: this, show)
}

fun View.index(): Int {
    val parentView = parent
    if (parentView is ViewGroup) {
        return parentView.indexOfChild(this)
    } else {
        return 0
    }
}
