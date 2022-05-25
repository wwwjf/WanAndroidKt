package com.xianghe.ivy.ui.module.videocall.backdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.em.sound.AudioInComingManager;
import com.xianghe.ivy.model.VideoCallBean;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.LanguageUtil;
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
 * @Author: ycl
 * @Date: 2019/2/25 14:11
 * @Desc:
 */
public class VideoCallResponseDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private CircleImageView mIvVcUserAvatar;
    private ImageView mIvVcUserAvatarLogo;
    private TextView mTvVcUserName;
    private TextView mTvVcUserAge;
    private TextView mTvVcUserIntro;
    private Button mBtnVcRefuse;
    private Button mBtnVcReceive;

    private Disposable timeTask;

    private VideoCallBean bean;
    private onClickListener mListener;

    private AudioInComingManager mAudioInComingManager;
    private boolean isZH;


    public VideoCallResponseDialog(@NonNull Context context, VideoCallBean bean, onClickListener mListener) {
        super(context, R.style.DialogStyleRightTranslut);
        this.mContext = context;
        this.bean = bean;
        this.mListener = mListener;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_vc_receiver_message);
        initView();
        initData();
        mAudioInComingManager = new AudioInComingManager(mContext);
        isZH = LanguageUtil.isSimplifiedChinese(getContext());
    }

    private void initData() {
        // 设置window偏右
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().getAttributes().gravity = Gravity.RIGHT;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        Glide.with(this.mContext)
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

        long endTime = 8 - (System.currentTimeMillis() - bean.getDialTime()) / 1000;
        endTime = endTime > 0 ? endTime : 0;
        startTime(endTime);
    }

    public boolean hasEnd() {
        long endTime = 8 - (System.currentTimeMillis() - bean.getDialTime()) / 1000;
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
                if (System.currentTimeMillis() - 8000 > bean.getDialTime()) {
                    ToastUtil.showToast(getContext(), mContext.getString(R.string.the_dialing_terminal_has_been_closed));
                    activeRejectDialog();
                } else {
                    acceptDialog();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
                        KLog.i("");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        KLog.i(""); // complete
                        rejectDialog(true);
                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        KLog.i("");
                        timeTask = disposable;
                    }
                });
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
        mBtnVcRefuse.setText(mContext.getString(R.string.refuse));
//        AudioSoundPoolManager.getInstance(getContext()).stopInComing();
        stopInComing(true);

        this.dismiss();
        if (mListener != null) {
            mListener.acceptDialog();
        }
    }

    private void setRefuseTest(long initialDelay) {
        initialDelay = initialDelay < 0 ? 0 : initialDelay;
        SpannableString sp = new SpannableString(mContext.getString(R.string.refuse) + "(" + initialDelay + "s)");
        sp.setSpan(new RelativeSizeSpan(0.6f), isZH?2:6, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBtnVcRefuse.setText(sp);
    }

    public void rejectDialog(boolean isTimeOut) {
        stopTimeTask();
        mBtnVcRefuse.setText(mContext.getString(R.string.refuse));
//        AudioSoundPoolManager.getInstance(getContext()).stopInComing();
        stopInComing(true);

        this.dismiss();
        if (mListener != null) {
            mListener.rejectDialog(isTimeOut);
        }
    }

    /**
     * 主动关闭弹框，不需要发送消息到环信,但是要发送消息到后台
     */
    public void activeRejectDialog() {
        stopTimeTask();
        mBtnVcRefuse.setText(mContext.getString(R.string.refuse));
        stopInComing(true);

        this.dismiss();
        if (mListener != null) {
            mListener.rejectTimeOutDialog();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();

//        stopTimeTask();
        if (mAudioInComingManager != null) {
            mAudioInComingManager = null;
        }
       /* if (mListener != null) {
            mListener = null;
        }*/
       /* if (bean != null) {
            bean = null;
        }*/
        if (mContext != null) {
            mContext = null;
        }
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

    public interface onClickListener {
        public void acceptDialog();

        public void rejectDialog(boolean isTimeOut);

        public void rejectTimeOutDialog();
    }
}
