package com.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.channels.SocketChannel;

/**
 * Created by Administrator on 2017/2/21.
 */
public class ChatClientHandler extends SimpleChannelInboundHandler<String>{

    /**
     * 客户端接受到消息，打印到自己客户端这里
     * @param channelHandlerContext
     * @param s
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(s);
    }
}
