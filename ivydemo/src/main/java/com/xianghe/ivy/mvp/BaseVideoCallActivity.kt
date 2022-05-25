package com.xianghe.ivy.mvp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.birdsport.cphome.extension.toast
import com.google.gson.reflect.TypeToken
import com.luckongo.tthd.net.mapper.RespMapper
import com.xianghe.ivy.R
import com.xianghe.ivy.app.IvyApp
import com.xianghe.ivy.app.IvyConstants
import com.xianghe.ivy.app.em.EMManager
import com.xianghe.ivy.app.em.EMMessageHelper
import com.xianghe.ivy.app.em.OnlinePreferenceManager
import com.xianghe.ivy.constant.Api
import com.xianghe.ivy.constant.Global
import com.xianghe.ivy.http.request.NetworkRequest
import com.xianghe.ivy.http.request.RequestError
import com.xianghe.ivy.http.scheduler.SchedulerProvider
import com.xianghe.ivy.manager.UserInfoManager
import com.xianghe.ivy.model.UserBean
import com.xianghe.ivy.model.VideoCallBean
import com.xianghe.ivy.model.VideoChatBean
import com.xianghe.ivy.model.response.BaseResponse
import com.xianghe.ivy.network.GsonHelper
import com.xianghe.ivy.ui.module.main.mvp.view.activity.MainActivity
import com.xianghe.ivy.ui.module.player.PlayerActivity
import com.xianghe.ivy.ui.module.user.UserActivity
import com.xianghe.ivy.ui.module.videocall.VideoCallActivity
import com.xianghe.ivy.ui.module.videocall.backdialog.VideoCallResponseDialog
import com.xianghe.ivy.ui.module.videoedit.VideoEditActivity
import com.xianghe.ivy.ui.module.welcom.XWLauncherActivity
import com.xianghe.ivy.utils.IvyUtils
import com.xianghe.ivy.utils.KLog
import com.xianghe.ivy.utils.ScreenListener
import com.xianghe.ivy.weight.CustomProgress
import com.xianghe.ivy.weight.dialog.LoadingDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.startActivity
import java.util.*

/**
 * author:  ycl
 * date:  2019/2/27 9:46
 * desc:
 */
abstract class BaseVideoCallActivity : BaseActivity() {

    private var mVideoCallResponseDialog: VideoCallResponseDialog? = null

    // 稀屏监听
    public var mIsPause = false

    // loading
    private var mLoadingDialog: LoadingDialog? = null

    private var mHistoryCallProgress: CustomProgress? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 在启动的时候，关闭以前的视频通话
        if (umPageName() == XWLauncherActivity::class.java.simpleName) {
            closeAllStatus()
        }
    }


    override fun onResume() {
        super.onResume()
        mIsPause = false


        // 如果还在时间内，就弹出弹框,并弹出声音
        mVideoCallResponseDialog?.run {
            if (hasEnd()) {
                if (!isShowVideoCallResponseDialog()) show()
                playInComing()
            }
        }

        // 如果接受弹框还在显示，就移除消息
        if (EMMessageHelper.isShowVideoCallHistory) {
            EMMessageHelper.isShowVideoCallHistory = false
            if (isShowVideoCallResponseDialog()) {
                // 弹框还在显示，则后台存储的上一个接受的消息，需要移除
                EMMessageHelper.getInstance().clearAtMeGroup()
            } else if (umPageName() == MainActivity::class.java.simpleName
                    || umPageName() == PlayerActivity::class.java.simpleName
                    || umPageName() == UserActivity::class.java.simpleName) {
                showHistoryVideoCallResponseDialog()
            }
        }

//        // 弹框还在显示，就弹出声音
//        if (isShowVideoCallResponseDialog()) {
//            mVideoCallResponseDialog?.playInComing()
//        }
    }

    override fun onPause() {
        super.onPause()
        mIsPause = true

        // 如果接受弹框弹出状态，就关闭声音
        if (isShowVideoCallResponseDialog()) {
            mVideoCallResponseDialog?.stopInComing(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mVideoCallResponseDialog != null) {
            if (mVideoCallResponseDialog?.isShowing == true) mVideoCallResponseDialog?.dismiss()
            mVideoCallResponseDialog = null
        }
        if (mLoadingDialog != null) {
            if (mLoadingDialog?.isShowing == true) mLoadingDialog?.dismiss()
            mLoadingDialog = null
        }
        if (mHistoryCallProgress != null) {
            if (mHistoryCallProgress?.isShowing == true) mHistoryCallProgress?.dismiss()
            mHistoryCallProgress = null
        }
    }


    //  ------------  videoCallDialog start ----------------
    fun showVideoCallResponseDialog(bean: VideoCallBean) {
        // 需要有锁屏权限才能弹出提示
        if (bean == null) {
            return
        }
        if (bean.requestType == EMMessageHelper.REQUEST) {
            // 正在显示接收界面就不弹出新的拨打
            if (isShowVideoCallResponseDialog()) {
//                mVideoCallResponseDialog?.dismiss()
                return
            }
            mVideoCallResponseDialog = VideoCallResponseDialog(this, bean, object : VideoCallResponseDialog.onClickListener {
                override fun rejectTimeOutDialog() {
                    // 拨打端超时了，需要发送后台超时信息，防止拨打端挂了
                    requestVCmodify_video_chat_status(3, bean.roomID, null)
                    mVideoCallResponseDialog = null
                }

                override fun acceptDialog() {
                    acceptDialogCallBack()
                    requestVCmodify_video_chat_status(1, bean.roomID, null)
                    EMManager.sendTxtSendMessage(EMMessageHelper.ACCEPT, bean.accept_username, bean.roomID)
                    startActivity<VideoCallActivity>(Global.PARAMS to bean, Global.PARAMS1 to true)
                    mVideoCallResponseDialog = null
                }

                override fun rejectDialog(isTimeOut: Boolean) {
                    if (isTimeOut) {
                        requestVCmodify_video_chat_status(3, bean.roomID, null)
                        EMManager.sendTxtSendMessage(EMMessageHelper.REFUSE_BUSY, bean.accept_username, bean.roomID)
                    } else {
                        requestVCmodify_video_chat_status(2, bean.roomID, null)
                        EMManager.sendTxtSendMessage(EMMessageHelper.REFUSE, bean.accept_username, bean.roomID)
                    }
                    // 拒绝之后，检测有无缓存，有就弹出别人拨打的历史
                    showHistoryVideoCallResponseDialog()
                    mVideoCallResponseDialog = null
                }
            })
            // 稀屏不弹出接收框
            if (!mIsPause) {
                mVideoCallResponseDialog?.show()
                mVideoCallResponseDialog?.playInComing()
            }
        }
    }

    // 提供给子类调用，如果接受了视频通话，子类释放当前界面的一些逻辑
    open fun acceptDialogCallBack() {

    }

    fun isShowVideoCallResponseDialog(): Boolean = mVideoCallResponseDialog != null && mVideoCallResponseDialog?.isShowing == true

    fun isLastAcceptUNVideoCallResponseDialog(): String = mVideoCallResponseDialog?.accept_userName
            ?: ""

    fun isLastRoomIDVideoCallResponseDialog(): String = mVideoCallResponseDialog?.roomID ?: ""

    fun dismissVideoCallResponseDialog() {
        mVideoCallResponseDialog?.activeRejectDialog()
        // 拒绝之后，检测有无缓存，有就弹出别人拨打的历史
        showHistoryVideoCallResponseDialog()
    }

    open fun showHistoryVideoCallResponseDialog() {
        if (mIsPause) {
            return
        }
        if (mHistoryCallProgress?.isShowing == true) {
            mHistoryCallProgress?.cancel()
        }
        if (!EMMessageHelper.getInstance().hasAtMeGroups()) return // 没有拨打历史就不弹弹框

        val str = SpannableStringBuilder("")
        EMMessageHelper.getInstance().atMeGroups.last().let {
            val bean = GsonHelper.getsGson().fromJson<VideoCallBean>(it, VideoCallBean::class.java)
            str.append("#")
            str.append(bean.username)
            str.append("#  ")
            str.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    mHistoryCallProgress?.cancel()
                    EMMessageHelper.getInstance().clearAtMeGroup()

                    if (!bean.isOnline) {// 离线的消息  重播
                        requestVCUserInfo(bean.uid, bean.username, IvyConstants.REQUEST_CODE_VIDEO_CALL)
                    } else  // 在线区分， 大于8s 重拨 ，小于8s  接收 ，不在线 一定是重拨
                        if (bean.isOnline) {
                            val be = mVideoCallResponseDialog?.bean
                            if (be != null && be.roomID == bean.roomID && System.currentTimeMillis() - 8000 < be.dialTime) {
                                requestVCmodify_video_chat_status(1, bean.roomID, null)
                                EMManager.sendTxtSendMessage(EMMessageHelper.ACCEPT, bean.accept_username, bean.roomID)
                                startActivity<VideoCallActivity>(Global.PARAMS to bean, Global.PARAMS1 to true)
                            } else {
                                // 重播  待完成
                                requestVCUserInfo(bean.uid, bean.username, IvyConstants.REQUEST_CODE_VIDEO_CALL)
                            }
                        } else { // 重播  待完成
                            requestVCUserInfo(bean.uid, bean.username, IvyConstants.REQUEST_CODE_VIDEO_CALL)
                        }
                }

                override fun updateDrawState(ds: TextPaint) {
//                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(this@BaseVideoCallActivity, R.color.blue_color_007AFF) //设置颜色
                    ds.isUnderlineText = false;      //设置去除下划线
                }
            }, 0, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        str.append("\n")
        str.append(getString(R.string.start_chat_to_you_call_back))

        if (mHistoryCallProgress == null) {
            mHistoryCallProgress = CustomProgress(this)
        }
        mHistoryCallProgress?.show(str,
                getString(R.string.common_tips_title),
                getString(R.string.ignore),
                getString(R.string.see_details),
                {
                    mHistoryCallProgress?.cancel()
                    EMMessageHelper.getInstance().clearAtMeGroup()
                },
                {
                    // 去动态页面
                    mHistoryCallProgress?.cancel()
                    EMMessageHelper.getInstance().clearAtMeGroup()
                    val intent = Intent(this, UserActivity::class.java)
                    intent.data = Uri.parse("ivy://UserActivity?uid=0&" + UserActivity.ACTION_KEY + "=" + UserActivity.DYNAMIC_VALUE)
                    startActivity(intent)
                },
                false, null)
    }

    //  ------------  videoCallDialog end ----------------


    //  ------------  loadingDialog start ----------------

    private fun showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog(this)
        }
        if (mLoadingDialog?.isShowing != true) {
            mLoadingDialog?.show()
        }
    }

    private fun dismissLoadingDialog() {
        if (mLoadingDialog?.isShowing == true) {
            mLoadingDialog?.dismiss()
        }
    }
    //  ------------  loadingDialog end ----------------


    // --------------- request start ---------------------

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IvyConstants.REQUEST_CODE_VIDEO_CALL && lastUid != 0L) {
            requestVCUserInfo(lastUid, lastUserName, IvyConstants.REQUEST_CODE_VIDEO_CALL)
        }
    }

    private var hasRequestVCUserInfo = false // 控制拨打一次，防止重复拨打
    // 存储上一个，跳到个人信息完善页面的信息的数据
    private var lastUserName: String? = null
    private var lastUid: Long = 0L


    // 1. 判断是否填写个人信息
    fun requestVCUserInfo(uid: Long, recipient_username: String?, requestCode: Int) {
        KLog.d("requestVCUserInfo")
        // 防重复拦截
        if (hasRequestVCUserInfo) {
            KLog.d("requestVCUserInfo 拦截重复拨打的视频通话")
            return
        }
        hasRequestVCUserInfo = true
        KLog.d("requestVCUserInfo 开始连接通信 uid：$uid recipient_username:$recipient_username requestCode:$requestCode")
        showLoadingDialog()
        requestVideoCall(uid, recipient_username, requestCode) {
            dismissLoadingDialog()
        }
    }

    private fun requestVideoCall(uid: Long, recipient_username: String?, requestCode: Int, errorCallBack: (() -> Unit)?) {

        closeAllStatus()

        //判断是否有缓存
        val userBeanBaseResponse = IvyApp.getInstance().aCache.getAsObject(Global.cache_key_userProfile + UserInfoManager.getUid()) as? BaseResponse<UserBean>
        if (userBeanBaseResponse != null) {
            if (!this.isDestroyed) {
                onRequestVCUserInfoSuccess(userBeanBaseResponse.data, uid, recipient_username, requestCode, errorCallBack)
            } else {
                hasRequestVCUserInfo = false
                errorCallBack?.invoke()
            }
            return
        }
        NetworkRequest.postMapByJAVA<BaseResponse<UserBean>>(Api.Route.User.USERINFO,
                HashMap(),
                RespMapper(),
                object : TypeToken<BaseResponse<UserBean>>() {}.type)
                .observeOn(SchedulerProvider.ui())
                .subscribe({
                    if (!this.isDestroyed) {
                        IvyApp.getInstance().getACache().put(Global.cache_key_userProfile + UserInfoManager.getUid(), it)
                        onRequestVCUserInfoSuccess(it.getData(), uid, recipient_username, requestCode, errorCallBack)
                    } else {
                        hasRequestVCUserInfo = false
                        errorCallBack?.invoke()
                    }
                }, {
                    if (!this.isDestroyed) {
                        toast(getString(R.string.personal_information_first))
                    }
                    hasRequestVCUserInfo = false
                    errorCallBack?.invoke()
                })

    }

    private fun onRequestVCUserInfoSuccess(userBean: UserBean, uid: Long, recipient_username: String?, requestCode: Int, errorCallBack: (() -> Unit)?) {
        if (!IvyUtils.checkFinishUserInfo(userBean, uid)) {
            //暂时每次都检测是否完善资料，方便测试
            toast(getString(R.string.personal_information_first))

//            val intentProfile = Intent(this, UserProfileActivity::class.java)
//            intentProfile.putExtra(UserProfileActivity.INTENT_KEY, UserProfileActivity.FLAG_VIDEOCHAT)
//            if (requestCode == -1) {
//                startActivity(intentProfile)
//            } else {
//                startActivityForResult(intentProfile, requestCode)
//            }
            // 存储上一个请求的数据
            lastUid = uid
            lastUserName = recipient_username

            // 释放按钮
            hasRequestVCUserInfo = false
            errorCallBack?.invoke()
        } else {
            // 2.  创建通话房间
            requestVCVideo_chat(uid, recipient_username, userBean, errorCallBack)
        }
    }

    // 2. 创建房间
    private fun requestVCVideo_chat(recipient_uid: Long, recipient_username: String?, userBean: UserBean, errorCallBack: (() -> Unit)?) {
        NetworkRequest.postMapByJAVA<BaseResponse<VideoChatBean>>(Api.VideoCall.video_chat,
                mapOf(Api.Key.recipient_uid to recipient_uid),
                RespMapper(),
                object : TypeToken<BaseResponse<VideoChatBean>>() {}.type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (!this.isDestroyed) {
                        onRequestVCVideoChatSuccess(it, userBean, recipient_username)
                    }
                    hasRequestVCUserInfo = false
                    errorCallBack?.invoke()
                }, {
                    if (!this.isDestroyed) {
                        if (it is RequestError) {
                            toast(VideoChatBean.errorMessage(this, it.info_code,it.message))
                        } else {
                            toast(getString(R.string.call_failed))
                        }
                    }
                    hasRequestVCUserInfo = false
                    errorCallBack?.invoke()
                })
    }

    private fun onRequestVCVideoChatSuccess(response: BaseResponse<VideoChatBean>, userBean: UserBean, recipient_username: String?) {
        val chatBean = response.data
        if (chatBean != null) {
            val bean = VideoCallBean(chatBean.recipient_username,
                    chatBean.is_online == 1,
                    chatBean.room_id, // 接收方创建的房间
                    EMMessageHelper.REQUEST,
                    // 发送方信息
                    userBean.id,
                    userBean.name,
                    userBean.avatar,
                    userBean.birthday,
                    userBean.signature,
                    userBean.sex)
            if (chatBean.is_online == 1) {
                val intent = Intent(this, VideoCallActivity::class.java)
                intent.putExtra(Global.PARAMS, bean)
                // 接受方信息
                intent.putExtra(Global.PARAMS2, chatBean.avatar) // 头像
                intent.putExtra(Global.PARAMS3, chatBean.sex) // 性别
                if (recipient_username != null) intent.putExtra(Global.PARAMS4, recipient_username) // 姓名
                startActivity(intent)
            } else {
                toast(getString(R.string.the_other_party_is_not_online))

                // 不在线也要发送一条环信消息，告知对方有人给你拨打过
                EMManager.sendTxtAttrSendMessage(bean)
            }
        } else {
            toast(VideoChatBean.errorMessage(this, response.status,response.info))
        }
    }

    // 3. 修改状态信息
    fun requestVCmodify_video_chat_status(status: Int, room_id: String, duration_time: String?) {
        // status  通讯状态(0:等待;1:同意;2:拒绝;3:等待超时;4:结束))
        KLog.d("status: $status room_id: $room_id duration_time: $duration_time")
        NetworkRequest.postMapByJAVA<BaseResponse<*>>(Api.VideoCall.modify_video_chat_status,
                if (duration_time != null) {
                    mapOf(Api.Key.status to status,
                            Api.Key.room_id to room_id,
                            Api.Key.duration_time to duration_time)
                } else {
                    mapOf(Api.Key.status to status,
                            Api.Key.room_id to room_id)
                },
                RespMapper(),
                object : TypeToken<BaseResponse<*>>() {}.type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    KLog.d(" success  status: $status room_id: $room_id duration_time: $duration_time")
                    if (status == 4) OnlinePreferenceManager.getInstance().remove(room_id)
                    if (!this.isDestroyed) {
//                    onRequestModifyVideoChatStatusSuccess(it )
                    }
                }, {
                    KLog.d(" error  status: $status room_id: $room_id duration_time: $duration_time")
                    if (status == 4) { // 存储关闭失败的请求
                        OnlinePreferenceManager.getInstance().putString(room_id)
                    }
                    if (!this.isDestroyed) {
//                    toast(getString(R.string.call_failed))
                    }
                })
    }

    private fun closeAllStatus() {
        OnlinePreferenceManager.getInstance().onLineSet?.let {
            if (it != null && it.isNotEmpty()) {
                KLog.d("开始遍历 执行未关闭的 房间请求 closeAllStatus")
                it.forEach { item ->
                    requestVCmodify_video_chat_status(4, item, null)
                }
            }
        }
    }
    // --------------- request end ---------------------

}
