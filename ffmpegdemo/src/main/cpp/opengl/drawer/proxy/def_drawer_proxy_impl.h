//
// Created by 翁建烽 on 2022/5/20.
//

#ifndef WANANDROIDKT_DEF_DRAWER_PROXY_IMPL_H
#define WANANDROIDKT_DEF_DRAWER_PROXY_IMPL_H


#include "drawer_proxy.h"
#include "../../../utils/logger.h"

#include <vector>

class DefDrawerProxyImpl: public DrawerProxy {

private:
    std::vector<Drawer *> m_drawers;

public:
    void AddDrawer(Drawer *drawer);
    void Draw() override;
    void Release() override;
};



#endif //WANANDROIDKT_DEF_DRAWER_PROXY_IMPL_H
