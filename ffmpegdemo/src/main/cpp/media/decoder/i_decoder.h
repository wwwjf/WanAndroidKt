//
// Created by 翁建烽 on 2022/5/20.
//

#ifndef WANANDROIDKT_I_DECODER_H
#define WANANDROIDKT_I_DECODER_H

class IDecoder {
public:
    virtual void GoOn() = 0;
    virtual void Pause() = 0;
    virtual void Stop() = 0;
    virtual bool IsRunning() = 0;
    virtual long GetDuration() = 0;
    virtual long GetCurPos() = 0;
};

#endif //WANANDROIDKT_I_DECODER_H
