package com.xianghe.ivy.app.em

import android.content.Context
import android.os.Handler
import com.xianghe.ivy.manager.UserInfoManager
import com.xianghe.ivy.model.VideoCallBean
import com.xianghe.ivy.mvp.BaseVideoCallActivity
import com.xianghe.ivy.util.SystemUtil
import com.xianghe.ivy.utils.KLog


/**
 * author:  ycl
 * date:  2019/2/20 10:29
 * desc: 环信
 */
object EMManager {

    private var mHandler: Handler?=null


    fun init(context: Context) {
        val pid = android.os.Process.myPid()
        val processAppName = SystemUtil.getAppName(context, pid)
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null || !processAppName.equals(context.packageName, ignoreCase = true)) {
            // 则此application::onCreate 是被service 调用的，直接返回
            return
        }
        registerEMClient(context)
        registerMessageListener()
        registerConnectionListener(context)
    }

    private fun registerEMClient(context: Context) {

    }

    private fun registerMessageListener() {

    }

    /**
     * 附带个人信息的消息 1 请求
     */
    fun sendTxtAttrSendMessage(bean: VideoCallBean) {


    }

    /**
     *  5 录制视频 6 终止视频录制
     * 仅仅是通知对方消息
     */
    fun sendTxtAttrSendMessage(requestType: String, username: String, roomID: String, isMe: Boolean) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此

    }

    /**
     * 2 拒绝  3 接受 7 对方在忙
     * 仅仅是通知对方消息
     */
    fun sendTxtSendMessage(requestType: String, username: String, roomID: String) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此

    }


    /**
    注册模式分两种，开放注册和授权注册。只有开放注册时，才可以客户端注册。
    开放注册是为了测试使用，正式环境中不推荐使用该方式注册环信账号；
    授权注册的流程应该是您服务器通过环信提供的 REST API 注册，之后保存到您的服务器或返回给客户端。
    注册用户名会自动转为小写字母，所以建议用户名均以小写注册。（强烈建议开发者通过后台调用 REST 接口去注册环信 ID，客户端注册方法不提倡使用。）
     */
    fun register(username: String, password: String) {
        //注册失败会抛出HyphenateException
    }

    // 默认递归重连三次
    fun login(uid: String, password: String, errorNumber: Int = 0) {
        KLog.d("login uid $uid password $password")
        if (uid.isEmpty() || password.isEmpty()) {
            return
        }
    }

    private fun loginSuccessLoadAll() {

    }

    /**
     * 退出登录 同步
     */
    fun logout() {
        if (mHandler != null) {
            mHandler?.removeCallbacksAndMessages(null)
            mHandler = null
        }
        UserInfoManager.putHxIsLogin(false)

        KLog.d("logout")
    }

    /**
     * 退出登录 异步
     */
    fun asynLogout() {

    }


    fun registerConnectionListener(context: Context) {
        //注册一个监听连接状态的listener

    }
}
