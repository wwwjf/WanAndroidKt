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

    // ????????????
    public var mIsPause = false

    // loading
    private var mLoadingDialog: LoadingDialog? = null

    private var mHistoryCallProgress: CustomProgress? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ????????????????????????????????????????????????
        if (umPageName() == XWLauncherActivity::class.java.simpleName) {
            closeAllStatus()
        }
    }


    override fun onResume() {
        super.onResume()
        mIsPause = false


        // ???????????????????????????????????????,???????????????
        mVideoCallResponseDialog?.run {
            if (hasEnd()) {
                if (!isShowVideoCallResponseDialog()) show()
                playInComing()
            }
        }

        // ????????????????????????????????????????????????
        if (EMMessageHelper.isShowVideoCallHistory) {
            EMMessageHelper.isShowVideoCallHistory = false
            if (isShowVideoCallResponseDialog()) {
                // ??????????????????????????????????????????????????????????????????????????????
                EMMessageHelper.getInstance().clearAtMeGroup()
            } else if (umPageName() == MainActivity::class.java.simpleName
                    || umPageName() == PlayerActivity::class.java.simpleName
                    || umPageName() == UserActivity::class.java.simpleName) {
                showHistoryVideoCallResponseDialog()
            }
        }

//        // ????????????????????????????????????
//        if (isShowVideoCallResponseDialog()) {
//            mVideoCallResponseDialog?.playInComing()
//        }
    }

    override fun onPause() {
        super.onPause()
        mIsPause = true

        // ????????????????????????????????????????????????
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
        // ???????????????????????????????????????
        if (bean == null) {
            return
        }
        if (bean.requestType == EMMessageHelper.REQUEST) {
            // ????????????????????????????????????????????????
            if (isShowVideoCallResponseDialog()) {
//                mVideoCallResponseDialog?.dismiss()
                return
            }
            mVideoCallResponseDialog = VideoCallResponseDialog(this, bean, object : VideoCallResponseDialog.onClickListener {
                override fun rejectTimeOutDialog() {
                    // ???????????????????????????????????????????????????????????????????????????
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
                    // ?????????????????????????????????????????????????????????????????????
                    showHistoryVideoCallResponseDialog()
                    mVideoCallResponseDialog = null
                }
            })
            // ????????????????????????
            if (!mIsPause) {
                mVideoCallResponseDialog?.show()
                mVideoCallResponseDialog?.playInComing()
            }
        }
    }

    // ?????????????????????????????????????????????????????????????????????????????????????????????
    open fun acceptDialogCallBack() {

    }

    fun isShowVideoCallResponseDialog(): Boolean = mVideoCallResponseDialog != null && mVideoCallResponseDialog?.isShowing == true

    fun isLastAcceptUNVideoCallResponseDialog(): String = mVideoCallResponseDialog?.accept_userName
            ?: ""

    fun isLastRoomIDVideoCallResponseDialog(): String = mVideoCallResponseDialog?.roomID ?: ""

    fun dismissVideoCallResponseDialog() {
        mVideoCallResponseDialog?.activeRejectDialog()
        // ?????????????????????????????????????????????????????????????????????
        showHistoryVideoCallResponseDialog()
    }

    open fun showHistoryVideoCallResponseDialog() {
        if (mIsPause) {
            return
        }
        if (mHistoryCallProgress?.isShowing == true) {
            mHistoryCallProgress?.cancel()
        }
        if (!EMMessageHelper.getInstance().hasAtMeGroups()) return // ?????????????????????????????????

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

                    if (!bean.isOnline) {// ???????????????  ??????
                        requestVCUserInfo(bean.uid, bean.username, IvyConstants.REQUEST_CODE_VIDEO_CALL)
                    } else  // ??????????????? ??????8s ?????? ?????????8s  ?????? ???????????? ???????????????
                        if (bean.isOnline) {
                            val be = mVideoCallResponseDialog?.bean
                            if (be != null && be.roomID == bean.roomID && System.currentTimeMillis() - 8000 < be.dialTime) {
                                requestVCmodify_video_chat_status(1, bean.roomID, null)
                                EMManager.sendTxtSendMessage(EMMessageHelper.ACCEPT, bean.accept_username, bean.roomID)
                                startActivity<VideoCallActivity>(Global.PARAMS to bean, Global.PARAMS1 to true)
                            } else {
                                // ??????  ?????????
                                requestVCUserInfo(bean.uid, bean.username, IvyConstants.REQUEST_CODE_VIDEO_CALL)
                            }
                        } else { // ??????  ?????????
                            requestVCUserInfo(bean.uid, bean.username, IvyConstants.REQUEST_CODE_VIDEO_CALL)
                        }
                }

                override fun updateDrawState(ds: TextPaint) {
//                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(this@BaseVideoCallActivity, R.color.blue_color_007AFF) //????????????
                    ds.isUnderlineText = false;      //?????????????????????
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
                    // ???????????????
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

    private var hasRequestVCUserInfo = false // ???????????????????????????????????????
    // ??????????????????????????????????????????????????????????????????
    private var lastUserName: String? = null
    private var lastUid: Long = 0L


    // 1. ??????????????????????????????
    fun requestVCUserInfo(uid: Long, recipient_username: String?, requestCode: Int) {
        KLog.d("requestVCUserInfo")
        // ???????????????
        if (hasRequestVCUserInfo) {
            KLog.d("requestVCUserInfo ?????????????????????????????????")
            return
        }
        hasRequestVCUserInfo = true
        KLog.d("requestVCUserInfo ?????????????????? uid???$uid recipient_username:$recipient_username requestCode:$requestCode")
        showLoadingDialog()
        requestVideoCall(uid, recipient_username, requestCode) {
            dismissLoadingDialog()
        }
    }

    private fun requestVideoCall(uid: Long, recipient_username: String?, requestCode: Int, errorCallBack: (() -> Unit)?) {

        closeAllStatus()

        //?????????????????????
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
            //??????????????????????????????????????????????????????
            toast(getString(R.string.personal_information_first))

//            val intentProfile = Intent(this, UserProfileActivity::class.java)
//            intentProfile.putExtra(UserProfileActivity.INTENT_KEY, UserProfileActivity.FLAG_VIDEOCHAT)
//            if (requestCode == -1) {
//                startActivity(intentProfile)
//            } else {
//                startActivityForResult(intentProfile, requestCode)
//            }
            // ??????????????????????????????
            lastUid = uid
            lastUserName = recipient_username

            // ????????????
            hasRequestVCUserInfo = false
            errorCallBack?.invoke()
        } else {
            // 2.  ??????????????????
            requestVCVideo_chat(uid, recipient_username, userBean, errorCallBack)
        }
    }

    // 2. ????????????
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
                    chatBean.room_id, // ????????????????????????
                    EMMessageHelper.REQUEST,
                    // ???????????????
                    userBean.id,
                    userBean.name,
                    userBean.avatar,
                    userBean.birthday,
                    userBean.signature,
                    userBean.sex)
            if (chatBean.is_online == 1) {
                val intent = Intent(this, VideoCallActivity::class.java)
                intent.putExtra(Global.PARAMS, bean)
                // ???????????????
                intent.putExtra(Global.PARAMS2, chatBean.avatar) // ??????
                intent.putExtra(Global.PARAMS3, chatBean.sex) // ??????
                if (recipient_username != null) intent.putExtra(Global.PARAMS4, recipient_username) // ??????
                startActivity(intent)
            } else {
                toast(getString(R.string.the_other_party_is_not_online))

                // ???????????????????????????????????????????????????????????????????????????
                EMManager.sendTxtAttrSendMessage(bean)
            }
        } else {
            toast(VideoChatBean.errorMessage(this, response.status,response.info))
        }
    }

    // 3. ??????????????????
    fun requestVCmodify_video_chat_status(status: Int, room_id: String, duration_time: String?) {
        // status  ????????????(0:??????;1:??????;2:??????;3:????????????;4:??????))
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
                    if (status == 4) { // ???????????????????????????
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
                KLog.d("???????????? ?????????????????? ???????????? closeAllStatus")
                it.forEach { item ->
                    requestVCmodify_video_chat_status(4, item, null)
                }
            }
        }
    }
    // --------------- request end ---------------------

}
