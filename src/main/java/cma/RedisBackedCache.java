package cma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public class RedisBackedCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(RedisBackedCache.class);

  private final Jedis jedis;

  public RedisBackedCache(String host, Integer port) {
    // Connecting to Redis server on localhost
    jedis = new Jedis(host, port);
    LOGGER.info("Connection to server successfully");
    // Check whether server is running or not
    LOGGER.info("Server is running: {} ", jedis.ping());
  }

  public void put(String key, String value) {
    LOGGER.debug("Got request to put key {} with value {}", key, value);
    jedis.set(key, value);
  }

  public String get(String key) {
    LOGGER.debug("Got request to get key - {}", key);
    return jedis.get(key);
  }
}
