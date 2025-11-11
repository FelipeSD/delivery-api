package com.deliverytech.delivery_api.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableCaching
public class RedisConfig {

  @Value("${spring.redis.host:localhost}")
  private String redisHost;

  @Value("${spring.redis.port:6379}")
  private int redisPort;

  @Value("${spring.redis.timeout:10s}")
  private Duration timeout;

  @Value("${spring.cache.redis.time-to-live:1800000}")
  private long defaultTtl;

  /**
   * 🔧 Cria a fábrica de conexões Redis (Lettuce)
   */
  @Bean
  @Profile("!test")
  public LettuceConnectionFactory redisConnectionFactory() {
    log.info("🔧 Configurando Redis Connection Factory...");
    log.info("   Host: {}", redisHost);
    log.info("   Port: {}", redisPort);
    log.info("   Timeout: {}", timeout);

    RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(redisHost, redisPort);

    LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
        .commandTimeout(timeout)
        .shutdownTimeout(Duration.ZERO)
        .build();

    LettuceConnectionFactory factory = new LettuceConnectionFactory(serverConfig, clientConfig);
    log.info("✅ RedisConnectionFactory configurado com sucesso");
    return factory;
  }

  /**
   * 💾 Template genérico para interação direta com Redis
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    log.info("🔧 Configurando RedisTemplate...");

    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    StringRedisSerializer stringSerializer = new StringRedisSerializer();
    GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

    template.setKeySerializer(stringSerializer);
    template.setHashKeySerializer(stringSerializer);
    template.setValueSerializer(jsonSerializer);
    template.setHashValueSerializer(jsonSerializer);

    template.afterPropertiesSet();
    log.info("✅ RedisTemplate configurado");
    return template;
  }

  /**
   * 🧠 CacheManager com múltiplas configurações de cache (TTL e prefixos)
   */
  @Bean
  public CacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper baseMapper) {
    log.info("🔧 Configurando RedisCacheManager...");
    log.info("   TTL padrão: {} ms ({} min)", defaultTtl, defaultTtl / 60000);

    // Configuração do ObjectMapper para o cache
    ObjectMapper cacheMapper = baseMapper.copy();
    cacheMapper.registerModule(new JavaTimeModule());
    cacheMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    cacheMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    cacheMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    cacheMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, DefaultTyping.NON_FINAL);

    GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(cacheMapper);

    // Configuração padrão (usada para caches sem TTL customizado)
    RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMillis(defaultTtl))
        .disableCachingNullValues()
        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

    // Configurações específicas
    Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
    cacheConfigs.put("produtos",
        defaultConfig.entryTtl(Duration.ofHours(1)).prefixCacheNameWith("produtos::"));
    cacheConfigs.put("pedidos",
        defaultConfig.entryTtl(Duration.ofMinutes(15)).prefixCacheNameWith("pedidos::"));
    cacheConfigs.put("usuarios",
        defaultConfig.entryTtl(Duration.ofMinutes(30)).prefixCacheNameWith("usuarios::"));

    RedisCacheManager manager = RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(defaultConfig)
        .withInitialCacheConfigurations(cacheConfigs)
        .transactionAware()
        .build();

    log.info("✅ RedisCacheManager configurado com {} caches customizados", cacheConfigs.size());
    return manager;
  }
}
