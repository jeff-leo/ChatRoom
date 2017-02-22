package com.websocket;

import com.sun.corba.se.spi.activation.Server;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by Administrator on 2017/2/21.
 */
public class WebsocketChatServer {

    private int port;

    public WebsocketChatServer(int port) {
        this.port = port;
    }

    public void run(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap strap = new ServerBootstrap();
        try {
            strap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new WebsocketChatServerInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            System.out.println("[系统消息]: Server启动完毕");

            //开始接受新的链接
            ChannelFuture sync = strap.bind(port).sync();

            sync.channel().closeFuture().sync();
        } catch (Exception e){

        } finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            System.out.println("[系统消息]: Server已关闭");
        }
    }

    public static void main(String[] args){
        int port = 8080;
        new WebsocketChatServer(port).run();
    }
}
