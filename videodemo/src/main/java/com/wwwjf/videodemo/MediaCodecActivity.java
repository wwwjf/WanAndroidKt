package com.wwwjf.videodemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MediaCodecActivity extends AppCompatActivity {

    private String mFilePath = "";
    private DataInputStream mDataInputStream;
    private SurfaceView mSurfaceView;
    private LinearLayout videoLayout;
    private SurfaceHolder mSurfaceHolder;
    private MediaCodec mMediaCodec;
    private Thread mDecodeThread;
    private Handler hdl_fixHWForSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_codec);
        mSurfaceView = findViewById(R.id.sv_activity_media_codec);
        videoLayout = findViewById(R.id.videoLayout);
        getFileInputStream();
        mediaCodec();
    }

    private void getFileInputStream() {
        try {
            mFilePath = getApplication().getExternalMediaDirs()[0].getAbsolutePath()+"/hh264.h264";
            mDataInputStream = new DataInputStream(new FileInputStream(new File(mFilePath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                if (mDataInputStream != null) {
                    mDataInputStream.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    private void mediaCodec() {
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    // ???????????????
                    mMediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
                    // ??????MediaFormat????????????????????????????????????
                    MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC,
                            holder.getSurfaceFrame().width(),
                            holder.getSurfaceFrame().height());
                    // ????????????
                    mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE,40);
                    //???????????????
                    mMediaCodec.configure(mediaFormat,holder.getSurface(),null,0);

                    starDecodingThread();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                if (mMediaCodec != null) {
                    mMediaCodec.release();
                    mMediaCodec.stop();
                }
            }
        });

    }

    private void starDecodingThread() {
        mMediaCodec.start();
        mMediaCodec.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        fixHW();
        if (mDataInputStream != null) {
            mDecodeThread = new Thread(new DecodeThread(mMediaCodec, mDataInputStream));
            mDecodeThread.start();
        }
    }

    //??????????????????????????????????????????,????????????????????????releaseOutputBuffer????????????
    int width,height;
    int mediaW,mediaH;
    public void fixHW() {
        if (width == 0 || height == 0) {
            width = videoLayout.getWidth();
            height = videoLayout.getHeight();
        }
        Log.e("tempWH", width + ":" + height);
        try {
            //???????????????????????????????????????????????????releaseOutputBuffer????????????
            MediaFormat currentFormat = mMediaCodec.getOutputFormat();
            mediaW = currentFormat.getInteger("width");
            mediaH = currentFormat.getInteger("height");
            Log.e("mediaWH", mediaW + ":" + mediaH);
            if (mediaH < height && mediaW < width) {
                Log.e("fixType", "mediaRes<layoutRes");
                height = mediaH;
                width = mediaW;
                doFix();
            } else if (mediaH >= height) {
                Log.e("fixType", "mediaH<layoutH");
                int fixW = mediaW * (height / mediaH);
                if (width != fixW) {
                    width = fixW;
                    doFix();
                }
            } else if (mediaW >= width) {
                Log.e("fixType", "mediaW<layoutW");
                int fixH = mediaH * (width / mediaW);
                if (height != fixH) {
                    height = fixH;
                    doFix();
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void doFix() {
        Log.e("mediaWH", width + ":" + height);
        //???????????????????????????
        MediaFormat newFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
//        configureDecoder(newFormat, tempSurface);
        //??????videoSurfaceView???????????????????????????
        ViewGroup.LayoutParams surfaceLayoutParams = mSurfaceView.getLayoutParams();
        surfaceLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        surfaceLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        if (width > height) {
            int fixH = (int) (height * (videoLayout.getWidth() / (double) width));
            if (fixH < videoLayout.getHeight()) {
                surfaceLayoutParams.height = fixH;
            } else {
                surfaceLayoutParams.height = videoLayout.getHeight();
                surfaceLayoutParams.width = (int) (width * (surfaceLayoutParams.height / (double) height));
            }
            fixVideoLayout(surfaceLayoutParams);
        } else if (height > width) {
            int fixW = (int) (width * (videoLayout.getHeight() / (double) height));
            if (fixW < videoLayout.getWidth()) {
                surfaceLayoutParams.width = fixW;
            } else {
                surfaceLayoutParams.width = videoLayout.getWidth();
                surfaceLayoutParams.height = (int) (height * (surfaceLayoutParams.width / (double) width));
            }
            fixVideoLayout(surfaceLayoutParams);
        }
    }

    //??????videoSurfaceView??????????????????????????????????????????????????????????????????????????????
    private void fixVideoLayout(ViewGroup.LayoutParams surfaceLayoutParams) {
        if (hdl_fixHWForSurfaceView == null) {
            hdl_fixHWForSurfaceView = new Handler(mSurfaceView.getContext().getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    mSurfaceView.setLayoutParams((ViewGroup.LayoutParams) msg.obj);
                }
            };
        }
        Message msg = new Message();
        msg.obj = surfaceLayoutParams;
        hdl_fixHWForSurfaceView.sendMessage(msg);
        Log.e("layoutWH", surfaceLayoutParams.width + ":" + surfaceLayoutParams.height);
    }

}