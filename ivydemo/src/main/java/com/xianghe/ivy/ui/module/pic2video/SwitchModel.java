package com.xianghe.ivy.ui.module.pic2video;


import com.xianghe.ivy.model.MusicBean2;

public class SwitchModel {
    public static interface Type {
        int NORMAL = 1;
        int QUICK = 2;
        int FADE = 3;
        int HORIZONTALSCROLLING = 4;
        int VERTICALSCROLLING = 5;
        int GRAY = 6;
    }

    private int mType;
    private MusicBean2 mMusic;

    public SwitchModel() {
    }

    public SwitchModel(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public MusicBean2 getMusic() {
        return mMusic;
    }

    public void setMusic(MusicBean2 music) {
        mMusic = music;
    }
}
