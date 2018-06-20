package org.yx.testio;

import org.yx.serialize.ProtostuffSerialize;
import org.yx.server.RpcServer;

import java.io.IOException;

/**
 * Created by 杨欣 on 2018/6/18.
 */
public class ServerTest {

    public static void main(String[] args){
        RpcServer server = new RpcServer(9999,new ProtostuffSerialize());
        try {
            server.init();
            server.listen();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.close();
        }

    }
}

