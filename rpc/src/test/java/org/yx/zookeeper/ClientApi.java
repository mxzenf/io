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
import java.util.List;

import org.apache.zookeeper.ZooDefs.Ids;

/**
 * Created by 杨欣 on 2018/6/17.
 */
public class ClientApi implements AsyncCallback.StringCallback {
    private ZooKeeper zk;
    private String path = "/zookeeper";
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
        zk.create(path, add.getBytes(Charset.forName("utf8")), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, this,null);
    }

    @Test
    public void test_exist_path() throws KeeperException, InterruptedException {
        Stat stat = zk.exists(path, false);
        Assert.assertNotNull(stat);
    }

    @Test
    public void test_setData() throws KeeperException, InterruptedException {
        String data = "杨欣";
        Stat stat = zk.setData(path,data.getBytes(Charset.forName("utf8")),-1);
        Assert.assertNotNull(stat);
    }

    @Test
    public void test_getData() throws KeeperException, InterruptedException {
        byte[] data = zk.getData(path, true, null);
        Assert.assertEquals("杨欣", new String(data));
    }

    @Test
    public void test_createChild(){
        zk.create(path+"/node1", "127.0.0.1:9999".getBytes(Charset.forName("utf8")), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, this,null);
        zk.create(path+"/node2", "127.0.0.1:9998".getBytes(Charset.forName("utf8")), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, this,null);

    }

    @Test
    public void test_getChildred() throws KeeperException, InterruptedException {
        List<String> children = zk.getChildren(path, null);
        Assert.assertEquals(2, children.size());
    }

    @Test
    public void test_delNode() throws KeeperException, InterruptedException {
        zk.create(path+"/node3", "127.0.0.1:9998".getBytes(Charset.forName("utf8")), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, this,null);
        Assert.assertEquals(3, zk.getChildren(path, null).size());
        zk.delete(path+"/node3", -1);
        Assert.assertEquals(2, zk.getChildren(path, null).size());
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
