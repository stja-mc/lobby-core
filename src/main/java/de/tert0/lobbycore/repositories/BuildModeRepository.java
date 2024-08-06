package de.tert0.lobbycore.repositories;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

public class BuildModeRepository {
    private final JedisPool redisPool;

    public BuildModeRepository(JedisPool redisPool) {
        this.redisPool = redisPool;
    }

    public boolean isInBuilderMode(UUID uuid) {
        try(Jedis redis = this.redisPool.getResource()) {
            return redis.sismember("lobbycore:builders", uuid.toString());
        }
    }

    public void setBuilderMode(UUID uuid, boolean value) {
        try(Jedis redis = this.redisPool.getResource()) {
            if(value) {
                redis.sadd("lobbycore:builders", uuid.toString());
            } else {
                redis.srem("lobbycore:builders", uuid.toString());
            }
        }
    }
}
