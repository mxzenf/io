package org.yx.service;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yx.service.zookeeper.ZookeeperService;
import org.yx.service.zookeeper.ZookeeperServiceDiscovery;
import org.yx.service.zookeeper.ZookeeperServiceRegistry;

/**
 * 测试zookeeper注册和发现服务
 * Created by 杨欣 on 2018/6/23.
 */
public class ZkTest {
    ZookeeperServiceRegistry zsr;
    ZookeeperServiceDiscovery zsd;
    ZookeeperService zs = new ZookeeperService("127.0.0.1","/zookeeper");
    @Before
    public void init(){
        zsr = new ZookeeperServiceRegistry();
        zsd = new ZookeeperServiceDiscovery();
        zsr.setZookeeperService(zs);
        zsd.setZookeeperService(zs);
    }
    @Test
    public void test_registry(){
        zsr.registry("hello","192.168.1.101:9009");
    }
    @Test
    public void test_getService(){
        Assert.assertNotNull(zsd.discovery("hello"));
    }
    @After
    public void close(){
        zsr.getZookeeperService().close();
    }
}
