package org.yx;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.Date;

/**
 * Created by 杨欣 on 2018/4/30.
 */
public class TimerServer {

    public void bind(int port) throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG,1024)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                    socketChannel.pipeline().addLast(new StringDecoder());
                    socketChannel.pipeline().addLast(new ChannelHandlerAdapter() {
                        private int count;
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                            ByteBuf bf = (ByteBuf)msg;
//                            byte[] bs = new byte[bf.readableBytes()];
//                            bf.readBytes(bs);
                            String body = (String) msg;
                            System.out.println("服务器收到:" + body);
                            System.out.println("服务器收到"+ ++count + "次命令");
                            String res = "Q T O".equals(body)?(new Date(System.currentTimeMillis()).toString()):"无效命令";
                            ctx.write(Unpooled.copiedBuffer(res.getBytes()));
                            super.channelRead(ctx, msg);
                        }

                        @Override
                        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                            ctx.flush();
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            ctx.close();
                        }
                    });
                }
            })
            ;
            ChannelFuture cf = b.bind(port).sync();
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args){
        try {
            new TimerServer().bind(9991);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
