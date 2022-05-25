package com.xianghe.ivy.ui.media.base;


import androidx.annotation.Nullable;

/**
 * @Author: ycl
 * @Date: 2018/10/25 16:06
 * @Desc:
 */
public interface IMediaPlayer {

    public void play(String path);

    public void stop();

    public void pause();

    public void resume();

    public void destroy();




    public interface Status {
        public int STATUS_NULL = 0;
        public int STATUS_READY = 1;
        public int STATUS_PLAYING = 2;
        public int STATUS_PAUSE = 3;
        public int STATUS_COMPLETE = 4;
        public int STATUS_STOP = 5;
        public int STATUS_ERROR = 6;
        public int STATUS_STOP_START = 7;
    }


    public interface OnStatusChangedListener {
        public void onStatusChanged(IMediaPlayer playerUtils, int status, @Nullable Object other);
    }

    interface OnPlayNextListener{
        void onNextPlay(int position);
    }
}
