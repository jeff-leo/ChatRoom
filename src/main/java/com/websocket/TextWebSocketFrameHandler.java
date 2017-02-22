package com.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Created by Administrator on 2017/2/21.
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        Channel inComing = ctx.channel();
        for (Channel channel : channels){
            if(channel != inComing){
                channel.writeAndFlush(new TextWebSocketFrame("[" +
                        inComing.remoteAddress() + "]: " + textWebSocketFrame.text()));
            }else{
                channel.writeAndFlush(new TextWebSocketFrame("[you]: " + textWebSocketFrame.text() ));
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.writeAndFlush(new TextWebSocketFrame("[系统消息]: " + incoming.remoteAddress() + " 上线了！"));
        }
        channels.add(incoming);
        System.out.println("[Client]: "+incoming.remoteAddress() +"加入");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.writeAndFlush(new TextWebSocketFrame("[系统消息]: " + incoming.remoteAddress() + " 下线了！"));
        }
        System.out.println("[Client]: "+incoming.remoteAddress() +"离开");
        channels.remove(incoming);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("[Client]: "+incoming.remoteAddress()+" 在线中");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("[Client]: "+incoming.remoteAddress()+" 离线中");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("[Client]: "+incoming.remoteAddress()+" 异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
