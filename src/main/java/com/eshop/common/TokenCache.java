package com.eshop.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by windvalley on 2018/7/15.
 */
public class TokenCache {
    public static final String TOKENPREFIX = "token_";

    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder()
            //初始缓存大小, 缓存不会变大的，如果超过容量，会释放将要过期的数据
            .initialCapacity(1000)
            //缓存中最多能存放的缓存个数
            .maximumSize(1000)
            //缓存有效期为3600秒, 3600秒内没有存取，就回收
            .expireAfterAccess(3600, TimeUnit.SECONDS)
            .build(new CacheLoader<String, String>() {
                //当本地缓存命没有中，调用load方法获取结果并将结果缓存, 如果命中，不会调用，可以在这里取数据库什么的，看业务了
                //也可以在getkey的地方使用callback,实现一个匿名类，效果一样
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key, String value){
        localCache.put(key, value);
    }

    public static String getKey(String key){
        try {
            String value = localCache.get(key);
            if ("null".equals(value) == false){
                return value;
            }
        } catch (ExecutionException e) {
            logger.error("localCache get error", e);
        }
        return null;
    }
}
