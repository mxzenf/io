package org.yx.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SystemPropertyUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

/**
 * Created by 杨欣 on 2018/5/27.
 */
public class HttpStaticFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        final String uri = request.uri();
        final String path = sanitizeUri(uri);
        if (null == path) {
            sendError(ctx, FORBIDDEN);
            return;
        }

        File file = new File(path);
        if(!file.exists() || file.isHidden()){
            sendError(ctx, NOT_FOUND);
            return;
        }
        if (file.isDirectory()){
            if(uri.endsWith("/")){
                sendListing(ctx,file,path);
            } else {
                sendRedirect(ctx,uri+"/");
            }
        }
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "r");
        } catch (Exception e){
            sendError(ctx, INTERNAL_SERVER_ERROR);
            return;
        }
        long fileLength = raf.length();
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength+"");
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        setContentTypeHeader(response, file);
        ctx.write(response);

        ChannelFuture sendFileFuture;
        ChannelFuture lastContentFuture;
        sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
        lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        sendFileFuture.addListener(new ChannelProgressiveFutureListener(){

            @Override
            public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                System.err.println(future.channel() + " 传输完成.");
            }

            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
                System.err.println(future.channel() + " 传输进度: " + progress + " / " + total);
            }
        });
        lastContentFuture.addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendRedirect(ChannelHandlerContext ctx, String uri){
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,FOUND);
        response.headers().set(HttpHeaderNames.LOCATION, uri);
        ctx.writeAndFlush(response);
    }
    private static void setContentTypeHeader(HttpResponse response, File file){
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeTypesMap.getContentType(file));
    }
    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer("失败: " + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[^-\\._]?[^<>&\\\"]*");
    private static void sendListing(ChannelHandlerContext ctx, File dir, String dirPath){
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

        StringBuffer sb = new StringBuffer()
                .append("<!DOCTYPE html>\r\n")
                .append("<html><head><meta charset='utf-8' /><title>")
                .append("文件列表: ")
                .append(dirPath)
                .append("</title></head><body>\r\n")
                .append("<h3>文件列表: ")
                .append(dirPath)
                .append("</h3>\r\n")
                .append("<ul>")
                .append("<li><a href=\"../\">..</a></li>\r\n");

        for(File f : dir.listFiles()){
            if (f.isHidden() || !f.canRead()){
                continue;
            }
            String name = f.getName();
            if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
                continue;
            }
            sb.append("<li><a href=\"")
                    .append(name)
                    .append("\">")
                    .append(name)
                    .append("</a></li>\r\n");
        }
        sb.append("</ul></body></html>\r\n");

        ByteBuf bb = Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8);
        response.content().writeBytes(bb);

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
    private static String sanitizeUri(String uri) {
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (Exception e){

        }

        if(uri.isEmpty() || !uri.startsWith("/")){
            return null;
        }

        if (uri.contains(File.separator + '.') ||
                uri.contains('.' + File.separator) ||
                uri.charAt(0) == '.' || uri.charAt(uri.length() - 1) == '.' ||
                INSECURE_URI.matcher(uri).matches()) {
            return null;
        }

        return SystemPropertyUtil.get("user.dir") + uri;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(ctx.channel().isActive()){
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }
}
