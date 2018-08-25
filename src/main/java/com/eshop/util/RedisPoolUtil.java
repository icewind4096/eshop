package com.eshop.util;

import com.eshop.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * Created by windvalley on 2018/8/25.
 */
@Slf4j
public class RedisPoolUtil {
    /**
     * 设置Key对应的value
     * @param key
     * @param value
     * @return
     */
    public static String set(String key, String value){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key, value);
            RedisPool.returnResource(jedis);
            return result;
        } catch (Exception e){
            log.error("set key:{} value:{} error", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return null;
        }
    }

    /**
     * 设置key对应的value，以及超时释放时间
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    public static String set(String key, String value, int expireTime){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key, expireTime, value);
            RedisPool.returnResource(jedis);
            return result;
        } catch (Exception e){
            log.error("setExpire key:{} value:{} error", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return null;
        }
    }

    /**
     * 得到key对应的value
     * @param key
     * @return
     */
    public static String get(String key){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
            RedisPool.returnResource(jedis);
            return result;
        } catch (Exception e){
            log.error("get key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return null;
        }
    }

    /**
     * 设置Key有效期,单位是秒
     * @param key
     * @param expireTime
     * @return
     */
    public static Long setExpire(String key, int expireTime){
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key, expireTime);
            RedisPool.returnResource(jedis);
            return result;
        } catch (Exception e){
            log.error("expire key:{} expireTime:{} error", key, expireTime, e);
            RedisPool.returnBrokenResource(jedis);
            return null;
        }
    }

    /**
     * 删除key
     * @param key
     * @return
     */
    public static Long del(String key){
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
            RedisPool.returnResource(jedis);
            return result;
        } catch (Exception e){
            log.error("del key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return null;
        }
    }
}
