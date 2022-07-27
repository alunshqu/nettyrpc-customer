package com.alun.rpc;

import com.alun.zookeeper.ZookeeperFactory;
import io.netty.channel.ChannelFuture;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.ArrayList;
import java.util.List;

public class ServerChangeWatcher implements CuratorWatcher {

    public static final int SERVER_COUNT = 100;

    public static ServerChangeWatcher serverChangeWatcher = null;

    public static ServerChangeWatcher getInstance() {
        if (serverChangeWatcher == null) {
            serverChangeWatcher = new ServerChangeWatcher();
        }
        return serverChangeWatcher;
    }

    public static void initChannelFuture() throws Exception{
        CuratorFramework client = ZookeeperFactory.create();
        List<String> servers = client.getChildren().forPath("/netty");
        System.out.println("初始化服务器连接" + servers);
        for(String realServer : servers) {
            String[] arr = realServer.split("#");
            ChannelFuture channelFuture = NettyClientRpc.getBootStrap().connect(arr[0], Integer.valueOf(arr[1])).sync();
            ChannelFutureManager.add(channelFuture);
        }

        client.getChildren().usingWatcher(getInstance()).forPath("/netty");
    }

    @Override
    public void process(WatchedEvent watchedEvent) throws Exception {
        if (watchedEvent.getState().equals(Watcher.Event.KeeperState.Disconnected) ||
                watchedEvent.getState().equals(Watcher.Event.KeeperState.Expired)) {
            CuratorFramework client = ZookeeperFactory.recreate();
            client.getChildren().usingWatcher(this).forPath("/netty");
            return;
        } else if (watchedEvent.getState().equals(Watcher.Event.KeeperState.SyncConnected)
                && !watchedEvent.equals(Watcher.Event.EventType.NodeChildrenChanged)) {
            CuratorFramework client = ZookeeperFactory.create();
            client.getChildren().usingWatcher(this).forPath("/netty");
            return;
        }

        System.out.println("重新初始化服务器连接====");
        CuratorFramework client = ZookeeperFactory.create();
        client.getChildren().usingWatcher(this).forPath("/netty");
        List<String> serverPaths = client.getChildren().forPath("/netty");
        List<String> servers = new ArrayList<>();
        for(String serverPath : serverPaths) {
            String[] str = serverPath.split("#");
            servers.add(str[0] + "#" + str[1]);
        }

        ChannelFutureManager.serverList.clear();
        ChannelFutureManager.serverList.addAll(servers);
        List<ChannelFuture> futures = new ArrayList<>();
        for(String realServer : ChannelFutureManager.serverList) {
            String[] str = realServer.split("#");
            ChannelFuture channelFuture = NettyClientRpc.getBootStrap().connect(str[0], Integer.valueOf(str[1])).sync();
            futures.add(channelFuture);
        }

        synchronized (ChannelFutureManager.position) {
            ChannelFutureManager.clear();
            ChannelFutureManager.addAll(futures);
        }

    }
}
