package org.yx.zookeeper;

import junit.framework.Assert;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * Created by 杨欣 on 2018/6/17.
 */
public class ClientApi implements AsyncCallback.StringCallback {
    private ZooKeeper zk;
    LoggerFactory logger;
    @Before
    public void init(){
        try {
            zk = new ZooKeeper("127.0.0.1", 15000, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test_connect(){
        Assert.assertNotNull(zk);
    }
    @Test
    public void test_create() throws Exception {
        String add = "127.0.0.1:9999";
        String path = "/zookeeper";
        zk.create(path, add.getBytes(Charset.forName("utf8")), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, this,null);
    }

    @Test
    public void test_exist_path() throws KeeperException, InterruptedException {
        Stat stat = zk.exists("/zookeeper", false);
        Assert.assertNotNull(stat);
    }

    @After
    public void close(){
        if (null != zk) {
            try {
                zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        System.out.print(path);
    }
}
