package org.yx.service;

/**
 * Created by 杨欣 on 2018/6/23.
 * 根据服务名发现在zookeeper上面注册的服务
 *
 */
public interface ServiceDiscovery {
    String discovery(String serviceName);
}
