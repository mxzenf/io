package org.yx.serialize;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yx.bean.RpcRequest;

import java.nio.ByteBuffer;

/**
 * Created by 杨欣 on 2018/6/10.
 */
public class TestSerialize {
    private SerializeObject so;
    @Before
    public void init(){
        so = new DefaultSerializeObject();
    }
    @Test
    public void test_serializeObject(){
        RpcRequest rr = new RpcRequest();
        rr.setId("1");
        byte[] bytes = so.serialize(rr);
        System.out.print(so.deserialize(bytes));
    }
    @Test
    public void test_buff(){
        RpcRequest rr = new RpcRequest();
        rr.setId("1");
        ByteBuffer bb = ByteBuffer.allocate(2048);
        bb.clear();
        byte[] bytes = so.serialize(rr);
        bb.put(bytes);
        bb.flip();
        Assert.assertEquals("1", ((RpcRequest)so.deserialize(bb.array())).getId());
    }
}
