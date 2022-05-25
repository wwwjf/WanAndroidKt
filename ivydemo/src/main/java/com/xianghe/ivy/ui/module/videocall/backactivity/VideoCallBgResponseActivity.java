package com.xianghe.ivy.ui.module.videocall.backactivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.app.em.EMManager;
import com.xianghe.ivy.app.em.EMMessageHelper;
import com.xianghe.ivy.app.em.sound.AudioInComingManager;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.model.VideoCallBean;
import com.xianghe.ivy.mvp.BaseVideoCallActivity;
import com.xianghe.ivy.ui.module.videocall.VideoCallActivity;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.LanguageUtil;
import com.xianghe.ivy.utils.NotificationUtils;
import com.xianghe.ivy.utils.TimeFormatUtils;
import com.xianghe.ivy.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author: cwy
 * @Date: 2019/4/3 14:11
 * @Desc:
 */
public class VideoCallBgResponseActivity extends BaseVideoCallActivity implements View.OnClickListener {

    private CircleImageView mIvVcUserAvatar;
    private ImageView mIvVcUserAvatarLogo;
    private TextView mTvVcUserName;
    private TextView mTvVcUserAge;
    private TextView mTvVcUserIntro;
    private Button mBtnVcRefuse;
    private Button mBtnVcReceive;

    private Disposable timeTask;

    private VideoCallBean bean;

    private AudioInComingManager mAudioInComingManager;
    private boolean isZH;
    private long dialTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_bg_receiver_message);
        this.dialTime = getIntent().getLongExtra("dialTime",0);
        this.bean=getIntent().getParcelableExtra("videoCallBean");
        mAudioInComingManager = new AudioInComingManager(this);
        isZH = LanguageUtil.isSimplifiedChinese(this);
        initView();
        initData();
    }

    private void initData() {
        Glide.with(this)
                .load(bean.getAvatar())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_my_head_default)   //加载成功之前占位图
                        .error(R.mipmap.ic_my_head_default))
                .into(mIvVcUserAvatar);
        mTvVcUserName.setText(bean.getUsername());
        mTvVcUserIntro.setText(bean.getSignature());

        mIvVcUserAvatarLogo.setBackgroundResource(bean.getSex() == 1 ? R.drawable.icon_boy_vc : R.drawable.ic_famle_vc);
        if (bean.getBirthday() != null) {
            int age = TimeFormatUtils.getAge(bean.getBirthday());
            if (age != -1) {
                mTvVcUserAge.setVisibility(View.VISIBLE);
                mTvVcUserAge.setText("" + age);
            } else {
                mTvVcUserAge.setVisibility(View.GONE);
            }
        } else {
            mTvVcUserAge.setVisibility(View.GONE);
        }

        long endTime = 8 - (System.currentTimeMillis() - dialTime) / 1000;
        endTime = endTime > 0 ? endTime : 0;
        startTime(endTime);
        playInComing();
    }

    public boolean hasEnd() {
        long endTime = 8 - (System.currentTimeMillis() - dialTime) / 1000;
        return endTime > 0 ? true : false;
    }

    public String getAccept_userName() {
        return bean.getAccept_username();
    }

    public String getRoomID() {
        return bean.getRoomID();
    }

    public VideoCallBean getBean() {
        return bean;
    }

    private void initView() {
        mIvVcUserAvatar = (CircleImageView) findViewById(R.id.iv_vc_user_avatar);
        mIvVcUserAvatarLogo = (ImageView) findViewById(R.id.iv_vc_user_avatar_logo);
        mTvVcUserName = (TextView) findViewById(R.id.tv_vc_user_name);
        mTvVcUserAge = (TextView) findViewById(R.id.tv_vc_user_age);
        mTvVcUserIntro = (TextView) findViewById(R.id.tv_vc_user_intro);
        mBtnVcRefuse = (Button) findViewById(R.id.btn_vc_refuse);
        mBtnVcReceive = (Button) findViewById(R.id.btn_vc_receive);

        mBtnVcRefuse.setOnClickListener(this);
        mBtnVcReceive.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_vc_refuse:
                rejectDialog(false);
                break;
            case R.id.btn_vc_receive:
                // 超过8s,拨打端已经关闭了
                if (System.currentTimeMillis() - 8000 > dialTime) {
                    ToastUtil.showToast(this, getString(R.string.the_dialing_terminal_has_been_closed));
                    activeRejectDialog();
                } else {
                    acceptDialog();
                }
                break;
        }
    }


    private void startTime(long initialDelay) {
        stopTimeTask();
        KLog.d("initialDelay: " + initialDelay);
        timeTask = Observable.intervalRange(0, initialDelay, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        KLog.d("accept" + aLong);
                        setRefuseTest(initialDelay - 1 - aLong);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.d("accept" + -1);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        rejectDialog(true);
                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        KLog.d("accept" + -3);
                        timeTask = disposable;
                    }
                });
    }

    private void save(){
        EMMessageHelper.getInstance().addAtMeGroup(bean.toJson());
        NotificationUtils.showNotification(IvyApp.getInstance().getApplicationContext(),
                bean.getUsername(),
                IvyApp.getInstance().getApplicationContext().getString(R.string.started_a_video_chat_invitation_to_you),
                1, null);
        EMMessageHelper.isShowVideoCallHistory=true;
    }

    private void stopTimeTask() {
        if (timeTask != null && !timeTask.isDisposed()) {
            timeTask.dispose();
        }
    }

    public boolean isStopTimeTask() {
        return timeTask != null && !timeTask.isDisposed();
    }

    // 接收
    public void acceptDialog() {
        stopTimeTask();
        mBtnVcRefuse.setText(getString(R.string.refuse));
        stopInComing(true);
        requestVCmodify_video_chat_status(1, bean.getRoomID(), null);
        EMManager.INSTANCE.sendTxtSendMessage(EMMessageHelper.ACCEPT, bean.getAccept_username(), bean.getRoomID());
        Intent intent=new Intent(this, VideoCallActivity.class);
        intent.putExtra(Global.PARAMS,bean);
        intent.putExtra(Global.PARAMS1,true);
        startActivity(intent);
        finish();
    }


    private void setRefuseTest(long initialDelay) {
        initialDelay = initialDelay < 0 ? 0 : initialDelay;
        SpannableString sp = new SpannableString(getString(R.string.refuse) + "(" + initialDelay + "s)");
        sp.setSpan(new RelativeSizeSpan(0.6f), isZH?2:6, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBtnVcRefuse.setText(sp);
    }

    public void rejectDialog(boolean isTimeOut) {
        stopTimeTask();
        mBtnVcRefuse.setText(getString(R.string.refuse));
        stopInComing(true);
        if (isTimeOut) {
            requestVCmodify_video_chat_status(3, bean.getRoomID(), null);
            EMManager.INSTANCE.sendTxtSendMessage(EMMessageHelper.REFUSE_BUSY, bean.getAccept_username(), bean.getRoomID());
        } else {
            requestVCmodify_video_chat_status(2, bean.getRoomID(), null);
            EMManager.INSTANCE.sendTxtSendMessage(EMMessageHelper.REFUSE, bean.getAccept_username(), bean.getRoomID());
        }
        // 拒绝之后，检测有无缓存，有就弹出别人拨打的历史
        showHistoryVideoCallResponseDialog();
        finish();
    }

    @Override
    public void finish() {
        stopInComing(true);
        isStopTimeTask();
        super.finish();
    }

    /**
     * 主动关闭弹框，不需要发送消息到环信,但是要发送消息到后台
     */
    public void activeRejectDialog() {
        stopTimeTask();
        mBtnVcRefuse.setText(getString(R.string.refuse));
        stopInComing(true);
        // 拨打端超时了，需要发送后台超时信息，防止拨打端挂了
        requestVCmodify_video_chat_status(3, bean.getRoomID(), null);
        finish();
    }


    public synchronized void stopInComing(boolean isDestroy) {
        if (mAudioInComingManager != null) {
            mAudioInComingManager.stopInComing();
            if (isDestroy) {
                mAudioInComingManager.destroy();
                mAudioInComingManager = null;
            }
        }
    }

    public synchronized void playInComing() {
        if (mAudioInComingManager != null) mAudioInComingManager.playInComing();
    }


}
