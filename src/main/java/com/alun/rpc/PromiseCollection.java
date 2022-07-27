package com.alun.rpc;

import com.alun.client.Response;
import io.netty.util.concurrent.Promise;

import java.util.HashMap;
import java.util.Map;

public class PromiseCollection {

    public static Map<Long, Promise<Response>> longPromiseMap = new HashMap<>();

    public static void putPromise(Long id, Promise<Response> promise) {
        longPromiseMap.put(id, promise);
    }

    public static void removePromise(Long id) {
        longPromiseMap.remove(id);
    }

    public static Promise<Response> getPromise(Long id) {
        return longPromiseMap.get(id);
    }

}
