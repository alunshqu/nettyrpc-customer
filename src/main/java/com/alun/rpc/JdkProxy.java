package com.alun.rpc;

import com.alibaba.fastjson2.JSONObject;
import com.alun.annotation.RemoteInvoke;
import com.alun.client.RequestFuture;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class JdkProxy implements InvocationHandler, BeanPostProcessor {

    private Field target;

    public static AtomicLong requestId = new AtomicLong(0);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestFuture requestFuture = new RequestFuture();
        requestFuture.setPath(target.getType().getName() + "." + method.getName());
        requestFuture.setRequest(args[0]);
        requestFuture.setId(requestId.incrementAndGet());
        Object res = NettyClientRpc.sentRequest(requestFuture);
        Class returnType = method.getReturnType();
        if(res == null) {
            return null;
        }
        res = JSONObject.parseObject(JSONObject.toJSONString(res), returnType);
        return res;
    }

    private Object getJDKProxy(Field field) {
        this.target = field;
        return Proxy.newProxyInstance(field.getType().getClassLoader(), new Class[]{field.getType()}, this);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for(Field field : fields) {
            if(field.isAnnotationPresent(RemoteInvoke.class)) {
                field.setAccessible(true);
                try {
                    field.set(bean, getJDKProxy(field));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }


}
