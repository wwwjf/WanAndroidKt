//
// Created by 翁建烽 on 2022/5/20.
//

#ifndef WANANDROIDKT_DRAWER_PROXY_H
#define WANANDROIDKT_DRAWER_PROXY_H


#include "../drawer.h"

class DrawerProxy {
public:
    virtual void AddDrawer(Drawer *drawer) = 0;
    virtual void Draw() = 0;
    virtual void Release() = 0;
    virtual ~DrawerProxy() {}
};


#endif //WANANDROIDKT_DRAWER_PROXY_H
