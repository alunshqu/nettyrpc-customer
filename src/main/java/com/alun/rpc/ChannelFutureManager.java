package com.alun.rpc;

import io.netty.channel.ChannelFuture;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelFutureManager {
    public static CopyOnWriteArrayList<String> serverList = new CopyOnWriteArrayList<>();

    public static AtomicInteger position = new AtomicInteger(0);

    public static ChannelFuture future = null;

    public static CopyOnWriteArrayList<ChannelFuture> channelFutures = new CopyOnWriteArrayList<>();

    public static ChannelFuture get() throws Exception {
        ChannelFuture future = get(position);
        if(future == null) {
            ServerChangeWatcher.initChannelFuture();
        }
        return channelFutures.get(0);
    }

    public static ChannelFuture getV2() throws Exception {
        if(future == null) {
            future = NettyClientRpc.getBootStrap().connect("localhost", 8080).sync();
        }
        return future;
    }

    public static ChannelFuture get(AtomicInteger index) {
        int size = channelFutures.size();
        if(size == 0) {
            return null;
        } else {
            return channelFutures.get(0);
        }

       /* ChannelFuture channelFuture = null;

        synchronized (index) {
            if(index.get() >= size - 1) {
                channelFuture = channelFutures.get(0);
                index.set(0);
            } else {
                channelFuture = channelFutures.get(index.incrementAndGet());
            }
        }

        if(!channelFuture.channel().isActive()) {
            channelFutures.remove(channelFuture);
            return get(position);
        }

        return channelFuture;*/
    }

    public static void removeChannel(ChannelFuture channelFuture) {
        channelFutures.remove(channelFuture);
    }

    public static void addChannel(ChannelFuture channelFuture) {
        channelFutures.add(channelFuture);
    }

    public static void clear() {
        for(ChannelFuture future : channelFutures) {
            future.channel().close();
        }
        channelFutures.clear();
    }

    public static void addAll(List<ChannelFuture> futures) {
        channelFutures.addAll(futures);
    }

    public static void add(ChannelFuture future) {
        channelFutures.add(future);
    }
}
