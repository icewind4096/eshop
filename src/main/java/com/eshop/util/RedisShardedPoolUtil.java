package com.eshop.util;

import lombok.extern.slf4j.Slf4j;
import com.eshop.common.RedisShardedPool;
import redis.clients.jedis.ShardedJedis;

/**
 * Created by windvalley on 2018/8/25.
 */
@Slf4j
public class RedisShardedPoolUtil {
    /**
     * 设置Key对应的value
     * @param key
     * @param value
     * @return
     */
    public static String set(String key, String value){
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.set(key, value);
            RedisShardedPool.returnResource(jedis);
            return result;
        } catch (Exception e){
            log.error("set key:{} value:{} error", key, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
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
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setex(key, expireTime, value);
            RedisShardedPool.returnResource(jedis);
            return result;
        } catch (Exception e){
            log.error("setExpire key:{} value:{} error", key, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return null;
        }
    }

    /**
     * 得到key对应的value
     * @param key
     * @return
     */
    public static String get(String key){
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.get(key);
            RedisShardedPool.returnResource(jedis);
            return result;
        } catch (Exception e){
            log.error("get key:{} error", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return null;
        }
    }

    /**
     * 设置Key有效期,单位是秒
     * @param key
     * @param expireTime
     * @return
     */
    public static Long expire(String key, int expireTime){
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.expire(key, expireTime);
            RedisShardedPool.returnResource(jedis);
            return result;
        } catch (Exception e){
            log.error("expire key:{} expireTime:{} error", key, expireTime, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return null;
        }
    }

    /**
     * 删除key
     * @param key
     * @return
     */
    public static Long del(String key){
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.del(key);
            RedisShardedPool.returnResource(jedis);
            return result;
        } catch (Exception e){
            log.error("del key:{} error", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return null;
        }
    }
}
