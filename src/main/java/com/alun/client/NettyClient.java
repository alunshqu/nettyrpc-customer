package com.alun.client;

import com.alibaba.fastjson2.JSONObject;
import com.alun.rpc.ChannelFutureManager;
import com.alun.rpc.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

public class NettyClient {

    public static EventLoopGroup group = null;
    public static Bootstrap bootstrap = null;

    public static AtomicLong requestId = new AtomicLong(0);

    static {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Promise<Response> promise = new DefaultPromise<>(group.next());
        final ClientHandler handler = new ClientHandler();
        processHandler(handler, bootstrap);
        ChannelFuture future = bootstrap.connect("localhost", 8080).sync();
        RequestFuture req = new RequestFuture();
        req.setId(1);
        req.setRequest("Hello World");
        req.setPath("com.alun.spi.UserService.getUserByName");
        String reqStr = JSONObject.toJSONString(req);
        future.channel().writeAndFlush(reqStr);
        Response res = promise.get();
        System.out.println(JSONObject.toJSONString(res));
    }

    static void processHandler(ClientHandler handler, Bootstrap bootstrap) {

    }

    public static Bootstrap getBootStrap() {
        return bootstrap;
    }

    public static Object sentRequest(RequestFuture future) throws Exception{
        String requestStr = JSONObject.toJSONString(future);
        ChannelFuture channelFuture = ChannelFutureManager.get();
        channelFuture.channel().writeAndFlush(requestStr);
        Object result = future.get();
        return result;
    }
}
