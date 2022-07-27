package com.alun.client;

import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FutureMain {

    public static void main(String[] args) {
        List<RequestFuture> requestFutures = new ArrayList<>();
        for(int i = 1; i < 100; i++) {
            RequestFuture future = new RequestFuture();
            future.setId(i);
            future.setRequest("Hello World" + i);
            future.addFuture(future);

            requestFutures.add(future);

            sendMsg(future);

            SubThread subThread = new SubThread(future);
            subThread.start();
        }

        for(RequestFuture req : requestFutures) {
            Object result = req.get();
            System.out.println(result);
        }
    }

    private static void sendMsg(RequestFuture future) {
        System.out.println("客户端发送数据：" + JSONObject.toJSONString(future));
    }
}
