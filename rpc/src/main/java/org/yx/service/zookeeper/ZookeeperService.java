package org.yx.service.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by 杨欣 on 2018/6/23.
 */
public class ZookeeperService implements AsyncCallback.StringCallback {
    private ZooKeeper zk;
    private final static int DEFAULT_TIMEOUT = 15000;
    private String zookeeperAdd;
    private String root;
    private static final Charset DEFAULT_CHARSET = Charset.forName("utf8");

    public ZookeeperService(){}
    public ZookeeperService(String address, String root){
        this.zookeeperAdd = address;
        try {
            zk = new ZooKeeper(this.zookeeperAdd, DEFAULT_TIMEOUT, null);
            setRoot(root);
        } catch (IOException e) {
            zookeeperRunException(e);
        }
    }

    /**
     * 创建zookeeper节点
     * @param nodeName
     * @param data
     */
    public void createNode(String nodeName, String data){
        String nodePath = root + nodeName;
        if (exists(nodePath)){
            setNodeData(nodePath, data);//如果节点存在直接设置/覆盖数据
            return;
        }
        zk.create(nodePath, encodeData(data), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, this,null);
    }

    /**
     * 删除节点
     * @param path
     */
    public void delNode(String path){
        try {
            zk.delete(root + path, -1);
        } catch (InterruptedException e) {
            zookeeperRunException(e);
        } catch (KeeperException e) {
            zookeeperRunException(e);
        }
    }
    /**
     * 设置节点数据
     * @param path 节点完整路径
     * @param data 节点数据
     */
    public void setNodeData(String path, String data){
        if (!exists(root + path)){
            return;
        }
        try {
            zk.setData(root + path, encodeData(data),-1);
        } catch (KeeperException e) {
            zookeeperRunException(e);
        } catch (InterruptedException e) {
            zookeeperRunException(e);
        }
    }
    /**
     * 判断节点是否已存在
     * @param nodeName 节点名称
     * @return
     */
    public boolean exists(String nodeName) {
        Stat stat = null;
        try {
            stat = zk.exists(root + nodeName, false);
        } catch (KeeperException e) {
            zookeeperRunException(e);
        } catch (InterruptedException e) {
            zookeeperRunException(e);
        }
        return null != stat;
    }

    /**
     * 默认utf8编码
     * @param data
     * @return
     */
    public byte[] encodeData(String data){
        return data.getBytes(DEFAULT_CHARSET);
    }

    /**
     * 获取节点数据
     * @param path
     * @return
     */
    public String getData(String path){
        try {
            return new String(zk.getData(root+path, true, null));
        } catch (KeeperException e) {
            zookeeperRunException(e);
        } catch (InterruptedException e) {
            zookeeperRunException(e);
        }
        return null;
    }
    public void setRoot(String root) {
        try {
            Stat stat = zk.exists(root, false);
            if (null == stat){
                zk.create(root, encodeData(""), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, this,null);
            }
        } catch (KeeperException e) {
            zookeeperRunException(e);
        } catch (InterruptedException e) {
            zookeeperRunException(e);
        }
        this.root = root;
    }

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {

    }

    /**
     * 将zookeeper异常转换为运行时异常
     * @param e
     */
    public void zookeeperRunException(Exception e){
        throw new RuntimeException("zookeeper异常"+e.getMessage());
    }

    /**
     * 关闭连接
     */
    public void close(){
        if (null != zk){
            try {
                zk.close();
            } catch (InterruptedException e) {
                zookeeperRunException(e);
            }
        }
    }
}
