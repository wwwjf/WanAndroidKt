//
// Created by 翁建烽 on 2022/5/22.
//

#ifndef WANANDROIDKT_OPENGL_PIXEL_RECEIVER_H
#define WANANDROIDKT_OPENGL_PIXEL_RECEIVER_H
#include <stdint.h>

class OpenGLPixelReceiver {
public:
    virtual void ReceivePixel(uint8_t *rgba) = 0;
};

#endif //WANANDROIDKT_OPENGL_PIXEL_RECEIVER_H
