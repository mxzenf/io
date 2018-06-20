package org.yx.testio;

import org.yx.bean.RpcRequest;
import org.yx.bean.RpcResponse;
import org.yx.client.RpcClient;
import org.yx.serialize.ProtostuffSerialize;

/**
 * Created by 杨欣 on 2018/6/18.
 */
public class ClientTest {

    public static void main(String[] args){
        RpcClient client = new RpcClient("127.0.0.1", 9999,new ProtostuffSerialize());
        RpcRequest request = new RpcRequest();
        request.setId("9999");
        request.setArgs("111");
        request.setParameterTypes(new Class[]{ClientTest.class});
        request.setServiceName("say hello");
        RpcResponse response = client.send(request);
        System.out.print("收到回应:" + response.getId());
        request = new RpcRequest();
        request.setId("8888");
        request.setArgs("111");
        request.setParameterTypes(new Class[]{ClientTest.class});
        request.setServiceName("say hello");
        response = client.send(request);
        System.out.print("收到回应:" + response.getId());
    }

}
