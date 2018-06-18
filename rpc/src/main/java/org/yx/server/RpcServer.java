package org.yx.server;

import org.yx.bean.RpcRequest;
import org.yx.bean.RpcResponse;
import org.yx.serialize.DefaultSerializeObject;
import org.yx.serialize.SerializeObject;

import java.io.IOException;
import java.net.InetSocketAddress;
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
                    sc.register(selector, SelectionKey.OP_READ);
                    sc.configureBlocking(false);
                } else if (k.isReadable()) {
                    sc = (SocketChannel)k.channel();
                    ByteBuffer bb = ByteBuffer.allocate(CAPACITY);
                    bb.clear();
                    sc.read(bb);
                    bb.flip();
                    RpcRequest request = (RpcRequest)serializeObject.deserialize(bb.array());
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
                    byte[] bytes = serializeObject.serialize(response);
                    sc.write(ByteBuffer.wrap(bytes));
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

