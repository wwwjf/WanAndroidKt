//
// Created by 翁建烽 on 2022/5/20.
//

#ifndef WANANDROIDKT_AUDIO_RENDER_H
#define WANANDROIDKT_AUDIO_RENDER_H

class AudioRender {
public:
    virtual void InitRender() = 0;
    virtual void Render(uint8_t *pcm, int size) = 0;
    virtual void ReleaseRender() = 0;
    virtual ~AudioRender() {}
};

#endif //WANANDROIDKT_AUDIO_RENDER_H
