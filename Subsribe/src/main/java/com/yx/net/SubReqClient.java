package com.yx.net;

import com.yx.pojo.SubscribeReqProto;
import com.yx.pojo.SubscribeRespProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * Created by 杨欣 on 2018/5/13.
 */
public class SubReqClient {

    public void connect(String addr, int port) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bt = new Bootstrap();
            bt.group(group)
              .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
            .handler(new ChannelInitializer<SocketChannel>(){

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                    socketChannel.pipeline().addLast(new ProtobufDecoder(
                            SubscribeRespProto.SubscribeResp.getDefaultInstance()
                    ));
                    socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    socketChannel.pipeline().addLast(new ProtobufEncoder());
                    socketChannel.pipeline().addLast(new ChannelHandlerAdapter(){

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            ctx.close();
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            for (int i = 0; i < 100; i ++) {
                                SubscribeReqProto.SubscribeReq.Builder builder =
                                        SubscribeReqProto.SubscribeReq.newBuilder();
                                builder.setSubReq(i);
                                builder.setAddress("自贡");
                                builder.setProductName("java编程思想");
                                ctx.writeAndFlush(builder.build());
                            }

                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                          System.out.println("收到回应:"+String.valueOf(msg));
                        }

                        @Override
                        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                            ctx.flush();
                        }
                    });
                }
            });
            ChannelFuture f =bt.connect(addr, port).sync();
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        new SubReqClient().connect("127.0.0.1", 9991);
    }
}
