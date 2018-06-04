package org.yx;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.io.File;

/**
 * Created by 杨欣 on 2018/4/30.
 */
public class TimeClient {

    public void connect(String addr, int port) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bt = new Bootstrap();
            bt.group(group)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                    socketChannel.pipeline().addLast(new StringDecoder());
                    socketChannel.pipeline().addLast(new ChannelHandlerAdapter(){
                        String order = "Q T O" + System.getProperty("line.separator");
                        ByteBuf m = Unpooled.buffer(order.length());
                        private int count;

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            System.out.print("释放资源");
                            ctx.close();
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            for (int i = 0; i < 100; i ++) {
                                m = Unpooled.buffer(order.length());
                                m.writeBytes(order.getBytes());
                                ctx.writeAndFlush(m);
                            }

                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                            ByteBuf bb = (ByteBuf) msg;
//                            byte[] req = new byte[bb.readableBytes()];
//                            bb.readBytes(req);
                            String body = (String) msg;
                            System.out.println(body);
                            System.out.println("第"+ ++count + "次收到服务器回应");
                        }
                    });
                }
            })
            ;
            ChannelFuture f =bt.connect(addr, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        new TimeClient().connect("127.0.0.1", 9991);
    }
}
