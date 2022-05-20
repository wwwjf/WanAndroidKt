//
// Created by 翁建烽 on 2022/5/20.
//

#ifndef WANANDROIDKT_I_DECODE_STATE_CB_H
#define WANANDROIDKT_I_DECODE_STATE_CB_H

#include "../one_frame.h"
//声明IDecoder，在cpp中include，编码重复引用
class IDecoder;

class IDecodeStateCb {
public:
    IDecodeStateCb();
    virtual void DecodePrepare(IDecoder *decoder) = 0;
    virtual void DecodeReady(IDecoder *decoder) = 0;
    virtual void DecodeRunning(IDecoder *decoder) = 0;
    virtual void DecodePause(IDecoder *decoder) = 0;
    virtual bool DecodeOneFrame(IDecoder *decoder, OneFrame *frame) = 0;
    virtual void DecodeFinish(IDecoder *decoder) = 0;
    virtual void DecodeStop(IDecoder *decoder) = 0;
};

#endif //WANANDROIDKT_I_DECODE_STATE_CB_H
