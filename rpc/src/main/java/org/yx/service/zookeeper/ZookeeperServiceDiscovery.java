package org.yx.service.zookeeper;

import org.yx.service.ServiceDiscovery;

/**
 * Created by 杨欣 on 2018/6/23.
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {
    private ZookeeperService zookeeperService;
    @Override
    public String discovery(String serviceName) {
        return zookeeperService.getData("/"+serviceName);
    }

    public ZookeeperService getZookeeperService() {
        return zookeeperService;
    }

    public void setZookeeperService(ZookeeperService zookeeperService) {
        this.zookeeperService = zookeeperService;
    }
}
