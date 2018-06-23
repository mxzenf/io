package org.yx.service.zookeeper;

import org.yx.service.ServiceRegistry;

/**
 * Created by 杨欣 on 2018/6/23.
 */
public class ZookeeperServiceRegistry implements ServiceRegistry {
    private ZookeeperService zookeeperService = new ZookeeperService("127.0.0.1","/zookeeper");
    @Override
    public void registry(String serviceName, String serviceAddress) {
       zookeeperService.createNode("/"+serviceName,serviceAddress);
    }

    public ZookeeperService getZookeeperService() {
        return zookeeperService;
    }

    public void setZookeeperService(ZookeeperService zookeeperService) {
        this.zookeeperService = zookeeperService;
    }
}
