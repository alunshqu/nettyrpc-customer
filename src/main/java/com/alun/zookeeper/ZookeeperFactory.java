package com.alun.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperFactory {
    public static CuratorFramework client;
    public static CuratorFramework create() {
        if(client == null) {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory.newClient("10.118.32.156:2181", 120000, 5000, retryPolicy);
            client.start();
        }
        return client;
    }

    public static CuratorFramework recreate() {
        client = null;
        create();
        return client;
    }


}
