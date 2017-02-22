package com.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 服务端
 */
public class ChatServer {

    private int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public void run() {
        //‘boss’，用来接收进来的连接。第二个经常被叫做‘worker’，用来处理已经被接收的连接，
        // 一旦‘boss’接收到连接，就会把连接信息注册到‘worker’上
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //服务端的启动时利用ServerBootstrap，而客户端的启动时利用Bootstrap
        try{
            ServerBootstrap strap = new ServerBootstrap();
            strap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatServerInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //到这里服务器就启动了
            System.out.println("[系统消息]: 服务器启动完毕！");

            //开始接受客户端连接
            ChannelFuture sync = strap.bind(port).sync();

            //等待服务器关闭
            sync.channel().closeFuture().sync();
        }catch (Exception e){

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.println("[系统消息]: 服务器关闭！");
        }
    }

    public static void main(String[] args) throws Exception{
        int port = 8080;
        new ChatServer(port).run();
    }
}
