//
// Created by 翁建烽 on 2022/5/20.
//

#ifndef WANANDROIDKT_PLAYER_H
#define WANANDROIDKT_PLAYER_H

#include <jni.h>
#include "../../render/video/video_render.h"
#include "../../decoder/video/v_decoder.h"
#include "../../decoder/audio/a_decoder.h"

#include "../../render/video/native_render/native_render.h"
#include "../../render/audio/opensl_render.h"

class Player {
private:
    VideoDecoder *m_v_decoder;
    VideoRender *m_v_render;

    // 新增音频解码和渲染器
    AudioDecoder *m_a_decoder;
    AudioRender *m_a_render;
public:
    Player(JNIEnv *jniEnv, jstring path, jobject surface);
    ~Player();

    void play();
    void pause();
    void stop();
};

#endif //WANANDROIDKT_PLAYER_H
