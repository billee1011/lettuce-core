package com.lambdaworks.redis.support;

import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

import com.lambdaworks.redis.RedisConnectionPool;

/**
 * Pooling proxy factory to create transparent pooling proxies. These proxies will allocate internally connections and use
 * always valid connections. You don't need to allocate/free the connections anymore.
 * 
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 15.05.14 21:12
 */
public class PoolingProxyFactory {

    /**
     * Creates a transparent connection pooling proxy. Will re-check the connection every 5 secs.
     * 
     * @param connectionPool The Redis connection pool
     * @param <T> Type of the connection.
     * @return Transparent pooling proxy.
     */
    public static <T, C extends T> C create(RedisConnectionPool<T> connectionPool) {
        return create(connectionPool, 5, TimeUnit.SECONDS);
    }

    /**
     * Creates a transparent connection pooling proxy.
     * 
     * @param connectionPool The Redis connection pool
     * @param recheckInterval interval for connection-checking
     * @param unit unit of the interval.
     * @param <T> Type of the connection.
     * @return Transparent pooling proxy
     */
    public static <T, C extends T> C create(RedisConnectionPool<T> connectionPool, long recheckInterval, TimeUnit unit) {
        Class<T> componentType = connectionPool.getComponentType();

        TransparentPoolingInvocationHandler h = new TransparentPoolingInvocationHandler(connectionPool, recheckInterval, unit);

        Object o = Proxy.newProxyInstance(PoolingProxyFactory.class.getClassLoader(), new Class[] { componentType }, h);

        return (C) o;
    }

}