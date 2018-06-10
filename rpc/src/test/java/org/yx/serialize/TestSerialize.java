package org.yx.serialize;

import org.junit.Before;
import org.junit.Test;
import org.yx.bean.RpcRequest;

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
}
