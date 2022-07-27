package com.alun.rpc;

import com.alibaba.fastjson2.JSONObject;
import com.alun.client.RequestFuture;
import com.alun.rpc.ChannelFutureManager;
import com.alun.rpc.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

public class NettyClientRpc {

    public static EventLoopGroup group = new NioEventLoopGroup();

    public static Bootstrap getBootStrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        final ClientHandler handler = new ClientHandler();
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0 , 4));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(handler);
                ch.pipeline().addLast(new LengthFieldPrepender(4, false));
                ch.pipeline().addLast(new StringEncoder(Charset.defaultCharset()));
            }
        });
        return bootstrap;
    }

    public static Object sentRequest(RequestFuture future) throws Exception{
        String requestStr = JSONObject.toJSONString(future);
        System.out.println("请求参数：" + requestStr);
        ChannelFuture channelFuture = ChannelFutureManager.getV2();
        channelFuture.channel().writeAndFlush(requestStr);
        return null;
    }
}
