package com.chat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 服务端的事件处理器,指定泛型为String，表示处理的是String类型数据
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String>{

    //新建一个channels，用于存放连接的channel
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 每当从服务端收到新的客户端连接时，客户端的 Channel 存入ChannelGroup列表中，并通知列表中的其他客户端 Channel
     * channelGroup存放连接的channel
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        for(Channel channel : channels){
            //不是当前channel，通知其它channel
            if(channel != inComing){
                channel.writeAndFlush("[系统消息]: " + inComing.remoteAddress() + "上线了!\n");
            }
        }
        //把当前的channel加入到channels
        channels.add(inComing);
    }

    /**
     * 当有服务端收到客户端断开连接时，客户端的 Channel 存入ChannelGroup列表中，并通知列表中的其他客户端 Channel
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        for(Channel channel : channels){
            //不是当前channel，通知其它channel
            if(channel != inComing){
                channel.writeAndFlush("[系统消息]: " + inComing.remoteAddress() + "下线了!\n");
            }
        }
        //把当前的channel加入到channels
        channels.remove(inComing);
    }

    /**
     * 每当从服务端读到客户端写入信息时，将信息转发给其他客户端的 Channel
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            if (channel != incoming){
                channel.writeAndFlush("[用户 " + incoming.remoteAddress() + "]: " + msg + "\n");
            } else {
                channel.writeAndFlush("[我]: " + msg + "\n");
            }
        }
    }

    /**
     * 当服务端监听到客户端活动时
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println("[" +inComing.remoteAddress()+ "]: "+"在线中");
    }

    /**
     * 当服务端监听到客户端不活动时
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println("[" +inComing.remoteAddress()+ "]: "+"离线中");
    }

    /**
     * 当发生io异常时
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("SimpleChatClient:"+incoming.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        System.out.println("出现异常");
        ctx.close();
    }
}
