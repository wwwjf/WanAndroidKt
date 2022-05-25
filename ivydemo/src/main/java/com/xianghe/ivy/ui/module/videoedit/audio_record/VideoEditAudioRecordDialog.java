package com.xianghe.ivy.ui.module.videoedit.audio_record;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioRecord;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.czt.mp3recorder.MP3Recorder;
import com.xianghe.ivy.R;
import com.xianghe.ivy.ui.media.base.AudioRecordStatus;
import com.xianghe.ivy.ui.media.record.audio.AudioRecordUtils;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.NoDoubleClickUtils;
import com.xianghe.ivy.weight.CircleProgressBar;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author: ycl
 * @Date: 2018/10/25 14:11
 * @Desc:
 */
public class VideoEditAudioRecordDialog extends Dialog implements View.OnClickListener {
    private TextView mVeDialogSure;
    private ImageView mVeDialogRecord;
    private TextView mVeDialogRecordTime;
    private TextView mVeDialogDesc;
    private ImageView mVeDialogDelete;
    private CircleProgressBar mVeCircleProgress;


    private Context mContext;
    private AudioRecordStatus mStatus;
    private double mTotalVideoTime;
    private recordStatusListener mRecordStatusListener;
    // 音频录制包装类
    private AudioRecordUtils mAudioRecordUtils;


    private Disposable timeTask;
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.0");

    public VideoEditAudioRecordDialog(@NonNull Context context, double totalVideoTime, recordStatusListener recordStatusListener) {
        super(context, R.style.DialogStyleRightTranslut);
        this.mContext = context;
        this.mTotalVideoTime = totalVideoTime;
        this.mStatus = AudioRecordStatus.RECORD_START;
        this.mRecordStatusListener = recordStatusListener;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_video_edit_record);
        initView();
        initData();
    }


    private void initData() {
        // 设置window偏右
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().getAttributes().gravity = Gravity.RIGHT;

        setCurrentUIByStatus(mStatus);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mRecordStatusListener != null) {
                    mRecordStatusListener.dismissDialogListener();
                }
                stopRecord();
                // 配音未成功,都需要删除文件
                if (mStatus != AudioRecordStatus.RECORD_SURE
                        && mStatus != AudioRecordStatus.RECORD_START
                        ) {
                    deleteAudioTemp();
                }
            }
        });

        KLog.i("mTotalVideoTime: " + mTotalVideoTime + "  x10:  " + (mTotalVideoTime * 10));
        mVeCircleProgress.setMax((int) (mTotalVideoTime * 10));
    }


    private void initView() {
        mVeDialogSure = (TextView) findViewById(R.id.ve_dialog_sure);
        mVeDialogRecord = (ImageView) findViewById(R.id.ve_dialog_record);
        mVeDialogRecordTime = (TextView) findViewById(R.id.ve_dialog_record_time);
        mVeDialogDesc = (TextView) findViewById(R.id.ve_dialog_desc);
        mVeDialogDelete = (ImageView) findViewById(R.id.ve_dialog_delete);
        mVeCircleProgress = (CircleProgressBar) findViewById(R.id.ve_circle_progress);

        mVeDialogSure.setOnClickListener(this);
        mVeDialogRecord.setOnClickListener(this);
        mVeDialogDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ve_dialog_sure:
                File file = getAudioTempFile();
                if (file == null) {
                    Toast.makeText(mContext, mContext.getString(R.string.common_recording_file_invalid), Toast.LENGTH_SHORT).show();
                    return;
                }
                // 排除未授予权限文件为0问题
                if (file.length() <= 104) {
                    Toast.makeText(mContext, mContext.getString(R.string.common_recording_no_permission), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mRecordStatusListener != null) {
                    mRecordStatusListener.sureRecordListener(file, file.getAbsolutePath());
//                    Toast.makeText(mContext, "配音成功", Toast.LENGTH_SHORT).show();
                    mStatus = AudioRecordStatus.RECORD_SURE;
                          /*
                        // 确认配音成功之后，隐藏按钮
                        mVeDialogSure.setVisibility(View.GONE);
                        mVeDialogDelete.setVisibility(View.GONE);
                        mVeCircleProgress.setProgress(0);
                        mVeDialogRecordTime.setText("");*/
                    if (this.isShowing()) {
                        this.dismiss();
                    }
                }
                break;
            // 录制按钮的点击事件
            case R.id.ve_dialog_record:
                NoDoubleClickUtils.isDoubleClick(1000, () -> { // 1s内点击无效
                    switch (mStatus) {
                        case RECORD_START: // 开始录音
                            if (mRecordStatusListener != null) {
                                mRecordStatusListener.startFirstRecordListener();
                            }
                            //记录点击录音的
                            mStatus = AudioRecordStatus.RECORD_END;
                            startRecord();
                            break;
                        case RECORD_END:// 结束录音
                            //判断如果少于一秒就不让点击
                            mStatus = AudioRecordStatus.RECORD_RESTART;
                            stopRecord();
                            break;
                        case RECORD_RESTART:// 重新录制
                            //重新赋值
                            //记录点击录音的
                            deleteAudioTemp();
                            if (mRecordStatusListener != null) {
                                mRecordStatusListener.startFirstRecordListener();
                            }
                            mStatus = AudioRecordStatus.RECORD_END;
                            startRecord();
                            break;
                        case RECORD_SURE:// 配音成功之后再次录制
                        case RECORD_DELETE:// 删除之后重新录制
                            mStatus = AudioRecordStatus.RECORD_END;
                            startRecord();
                            break;
                    }
                    setCurrentUIByStatus(mStatus);
                });
                break;
            case R.id.ve_dialog_delete:
                if (deleteAudioTemp()) {
                    Toast.makeText(mContext, mContext.getString(R.string.common_is_delete), Toast.LENGTH_SHORT).show();
                    mStatus = AudioRecordStatus.RECORD_DELETE;
                    // 删除之后，隐藏按钮
                    mVeDialogSure.setVisibility(View.GONE);
                    mVeDialogDelete.setVisibility(View.GONE);
                    mVeCircleProgress.setProgress(0);
                    mVeDialogRecordTime.setText("");
                }
                break;
        }
    }


    // 当前界面的状态变换
    private void setCurrentUIByStatus(AudioRecordStatus status) {
        switch (status) {
            case RECORD_START:
                mVeDialogRecord.setBackground(ContextCompat.getDrawable(mContext, R.drawable.selector_ic_ve_dialog_record_def_press));
                mVeDialogDesc.setText(mContext.getString(R.string.common_click_start_recording));
                mVeDialogSure.setVisibility(View.GONE);
                mVeDialogDelete.setVisibility(View.GONE);
                break;
            case RECORD_END:
                mVeDialogRecord.setBackground(ContextCompat.getDrawable(mContext, R.drawable.selector_ic_ve_dialog_pause_def_press));
                mVeDialogDesc.setText(mContext.getString(R.string.common_click_finish_recording));
                mVeDialogSure.setVisibility(View.GONE);
                mVeDialogDelete.setVisibility(View.GONE);
                break;
            case RECORD_RESTART:
                mVeDialogRecord.setBackground(ContextCompat.getDrawable(mContext, R.drawable.selector_ic_ve_dialog_record_def_press));
                mVeDialogDesc.setText(mContext.getString(R.string.common_click_restart_recording));
                mVeDialogSure.setVisibility(View.VISIBLE);
                mVeDialogDelete.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void startRecordUI() {
        stopTimeTask();
        timeTask = Observable.intervalRange(0, (long) (mTotalVideoTime * 10), 0, 1 * 100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
//                        KLog.d("accept" + aLong);
                        int progress = aLong.intValue() + 1;
                        mVeCircleProgress.setProgress(progress);
                        mVeDialogRecordTime.setText(String.format(getContext().getString(R.string.common_second_p10), mDecimalFormat.format(progress / 10.0)));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.i("");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        KLog.i("");
                        // 结束录音
                        mStatus = AudioRecordStatus.RECORD_RESTART;
                        stopRecord();
                        setCurrentUIByStatus(mStatus);
                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        KLog.i("");
                        timeTask = disposable;
                    }
                });
    }

    private void startRecord() {
        setCanceledOnTouchOutside(false);
        mAudioRecordUtils = new AudioRecordUtils();// 因为每次需要重新创建文件，所以重新初始化
        if (mAudioRecordUtils != null) {
            try {
                mAudioRecordUtils.startRecord();
                if (isRecordStart(mAudioRecordUtils.getMP3Recorder())) {
                    // 录音成功之后才开始倒计时，此处录音异常就不要倒计时了
                    startRecordUI();
                } else {
                    // 录音异常，状态改为第一次开始录音状态
                    KLog.e("录音未初始化成功，录音状态异常");
                    mStatus = AudioRecordStatus.RECORD_START;
                }
            } catch (Exception e) {
                // 录音异常，状态改为第一次开始录音状态
                mStatus = AudioRecordStatus.RECORD_START;
                KLog.e(e);
                e.printStackTrace();
            }
        }
    }

    private boolean isRecordStart(MP3Recorder mp3Recorder) {
        try {
            Field field = MP3Recorder.class.getDeclaredField("mAudioRecord");
            field.setAccessible(true);
            AudioRecord mAudioRecord = (AudioRecord) field.get(mp3Recorder);
            // 如果是未录制状态，就代表异常了
            if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
                return false;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return true;
        }
        return true;
    }

    private void stopRecord() {
        setCanceledOnTouchOutside(true);
        if (mAudioRecordUtils != null) {
            mAudioRecordUtils.stopRecord();
        }
        stopTimeTask();
    }

    private void stopTimeTask() {
        if (timeTask != null && !timeTask.isDisposed()) {
            timeTask.dispose();
        }
    }

    public void pauseRecord() {
        if (mStatus == AudioRecordStatus.RECORD_END) {
            mStatus = AudioRecordStatus.RECORD_RESTART;
            stopRecord();
            setCurrentUIByStatus(mStatus);
        }
    }

    private boolean deleteAudioTemp() {
        return mAudioRecordUtils != null ? mAudioRecordUtils.deleteAudioTemp() : null;
    }

    private File getAudioTempFile() {
        return mAudioRecordUtils != null ? mAudioRecordUtils.getAudioTempFile() : null;
    }


    public interface recordStatusListener {
        void sureRecordListener(File file, String filePath);

        void startFirstRecordListener();

        void dismissDialogListener();
    }
}
