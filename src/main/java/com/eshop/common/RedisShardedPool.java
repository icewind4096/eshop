package com.eshop.common;

import com.eshop.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by windvalley on 2018/8/29.
 */
public class RedisShardedPool {
    private static ShardedJedisPool pool; //sharded jedis 连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));            //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10"));              //最大空闲实例
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "20"));              //最小空闲实例
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));//从pool中取得redis连接时，是否需要验证
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));//归还redis连接到到pool时，是否需要验证

    private static String redisIP_1 = PropertiesUtil.getProperty("redis_1.ip");                                         //redisIP_1地址
    private static Integer redisPort_1 = Integer.parseInt(PropertiesUtil.getProperty("redis_1.port"));                  //redisIP_1服务端口

    private static String redisIP_2 = PropertiesUtil.getProperty("redis_2.ip");                                         //redisIP_2地址
    private static Integer redisPort_2 = Integer.parseInt(PropertiesUtil.getProperty("redis_2.port"));                  //redisIP_2服务端口

    private static void initPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);

        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);
        jedisPoolConfig.setBlockWhenExhausted(true);            //连接耗尽时，是否阻塞, true 阻塞直到超时， false会抛出异常， 默认为true;

        JedisShardInfo jedisShardInfo_1 = new JedisShardInfo(redisIP_1, redisPort_1);

        JedisShardInfo jedisShardInfo_2 = new JedisShardInfo(redisIP_2, redisPort_2);

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>();
        jedisShardInfoList.add(jedisShardInfo_1);
        jedisShardInfoList.add(jedisShardInfo_2);

        pool = new ShardedJedisPool(jedisPoolConfig, jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static {
        initPool();
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args){
        ShardedJedis shardedJedis = pool.getResource();

        for (int i = 0; i < 10; i ++){
            shardedJedis.set("key" + i, "value" + i);
        }

        returnResource(shardedJedis);
    }
}
