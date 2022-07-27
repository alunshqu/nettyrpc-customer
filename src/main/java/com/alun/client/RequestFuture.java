package com.alun.client;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class RequestFuture {

    public static Map<Long, RequestFuture> futures = new ConcurrentHashMap<>();

    long id;
    Object request;
    Object result;

    String path;

    long timeout = 5000;

    public static void addFuture(RequestFuture future) {
        futures.put(future.getId(), future);
    }

    public Object get() {
        synchronized (this) {
            while (this.result == null) {
                try {
                    this.wait(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return this.result;
    }

    public static void received(Response res) {
        RequestFuture future = futures.get(res.getId());
        if(future != null) {
            future.setResult(res.getResult());
        }

        synchronized (future) {
            future.notify();
        }
    }

}
