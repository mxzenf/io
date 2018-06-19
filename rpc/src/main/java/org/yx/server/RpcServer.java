package org.yx.server;

import org.yx.bean.RpcRequest;
import org.yx.bean.RpcResponse;
import org.yx.serialize.DefaultSerializeObject;
import org.yx.serialize.ProtostuffSerialize;
import org.yx.serialize.SerializeObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Set;

/**
 * Created by 杨欣 on 2018/6/11.
 */
public class RpcServer {
    private static final int CAPACITY = 2048;
    private Selector selector;
    private ServerSocketChannel ssc;
    private int port;
    private List<RpcHandler> handlers;
    SerializeObject serializeObject;

    public RpcServer(){}

    public RpcServer(int port){
        this.port = port;
        serializeObject = new DefaultSerializeObject();
    }

    public RpcServer(int port, SerializeObject serializeObject){
        this.port = port;
        this.serializeObject = serializeObject;
    }

    public void init() throws IOException {
        selector = Selector.open();
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(port));
    }
    public void listen() throws IOException {
        while(true){
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            if (null == keys || 0 == keys.size()) {
                continue;
            }
            SocketChannel sc = null;
            for (SelectionKey k : keys) {
                if (k.isAcceptable()){
                    ServerSocketChannel ss = (ServerSocketChannel)k.channel();
                    sc = ss.accept();
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                } else if (k.isReadable()) {
                    sc = (SocketChannel)k.channel();
//                    k.cancel();
//                    sc.configureBlocking(true);
//                    ObjectInputStream ois = new ObjectInputStream(sc.socket().getInputStream());
//                    RpcRequest request = null;
//                    try {
//                        request = (RpcRequest)ois.readObject();
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }
                    ByteBuffer bb = ByteBuffer.allocate(CAPACITY);
                    bb.clear();
                    sc.read(bb);
                    bb.flip();
                    byte[] data = new byte[bb.position()];
                    bb.get(data);
                    RpcRequest request = serializeObject.deserialize(data, RpcRequest.class);
                    System.out.println("收到请求:" + request);
                    if (null == request || null == request.getId() ||
                            request.getId().length() == 0 || "-1".equals(request.getId())){
                        sc.close();
                        continue;
                    }
                    SelectionKey write_key = sc.register(selector, SelectionKey.OP_WRITE);
                    RpcResponse response = new RpcResponse();
                    for (RpcHandler rh : handlers) {
                        rh.handle(request, response);
                    }
                    write_key.attach(response);
                } else if (k.isWritable()) {
                    sc = (SocketChannel)k.channel();
                    RpcResponse response = (RpcResponse)k.attachment();
                    sc.write(ByteBuffer.wrap(serializeObject.serialize(response)));
                } else {

                }
            }
            keys.clear();
        }
    }
    public void close(){
        try {
          if (null != selector){
              selector.close();
          }
          if (null != ssc) {
              ssc.close();
          }
        } catch (Exception e){

        }
    }
}

