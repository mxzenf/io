package org.yx.service;

/**
 * Created by 杨欣 on 2018/6/23.
 * 将服务信息注册要zookeeper上
 */
public interface ServiceRegistry {
    void registry(String serviceName, String serviceAddress);
}
