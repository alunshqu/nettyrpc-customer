package com.alun.client;

public class SubThread extends Thread{
    RequestFuture future;

    public SubThread(RequestFuture future) {
        this.future = future;
    }

    @Override
    public void run() {
        Response res = new Response();
        res.setId(future.getId());
        res.setResult("server response" + Thread.currentThread().getId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        future.received(res);
    }
}
