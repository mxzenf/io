package org.yx.client;

import org.yx.bean.RpcRequest;
import org.yx.bean.RpcResponse;
import org.yx.serialize.SerializeObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * Created by 杨欣 on 2018/6/12.
 */
public class RpcClient {
    private String address;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    SerializeObject serializeObject;
    public RpcResponse send(RpcRequest request){
        RpcResponse response = null;
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.bind(new InetSocketAddress(address, port));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            SocketChannel sc;
            while (true){
                selector.select();
                Set<SelectionKey> keys = selector.keys();
                if (null == keys || 0 == keys.size()) {
                    continue;
                }
                for (SelectionKey k : keys){
                    if (k.isConnectable()){
                        sc = (SocketChannel)k.channel();
                        sc.finishConnect();
                        sc.register(selector, SelectionKey.OP_WRITE);
                    } else if (k.isWritable()) {
                        sc = (SocketChannel)k.channel();
                        RpcResponse r = (RpcResponse)k.attachment();
                        if (null == r) {
                            sc.write(ByteBuffer.wrap(serializeObject.serialize(request)));
                            sc.register(selector, SelectionKey.OP_READ);
                        } else {
                            sc.write(ByteBuffer.wrap(serializeObject.serialize(quitReq())));
                            break;
                        }
                    } else if (k.isReadable()){
                        sc = (SocketChannel)k.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(2048);
                        buffer.clear();
                        sc.read(buffer);
                        buffer.flip();
                        response = (RpcResponse)serializeObject.deserialize(buffer.array());
                        SelectionKey wKey = sc.register(selector, SelectionKey.OP_WRITE);
                        wKey.attach(response);
                    } else {}
                }
                keys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return response;
    }
    public RpcRequest quitReq(){
        RpcRequest request = new RpcRequest();
        request.setId("-1");
        return request;
    }
    public void close(){
        if (null != selector){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != socketChannel) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
