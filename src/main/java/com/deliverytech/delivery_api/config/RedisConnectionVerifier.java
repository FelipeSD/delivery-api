package com.deliverytech.delivery_api.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisConnectionVerifier {

  private final RedisConnectionFactory redisConnectionFactory;

  @Value("${spring.redis.host:localhost}")
  private String redisHost;

  @Value("${spring.redis.port:6379}")
  private int redisPort;

  @Value("${spring.profiles.active:default}")
  private String activeProfile;

  public RedisConnectionVerifier(RedisConnectionFactory redisConnectionFactory) {
    this.redisConnectionFactory = redisConnectionFactory;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void verifyRedisConnection() {
    log.info("🔍 Iniciando verificação de conexão Redis...");
    log.info("📋 Perfil ativo: {}", activeProfile);
    log.info("📋 Redis Host configurado: {}", redisHost);
    log.info("📋 Redis Port configurado: {}", redisPort);
    log.info("📋 Connection Factory: {}", redisConnectionFactory.getClass().getName());

    try {
      log.info("🔌 Tentando conectar ao Redis em {}:{}...", redisHost, redisPort);

      RedisConnection connection = redisConnectionFactory.getConnection();
      log.info("✅ Conexão obtida com sucesso!");

      String pong = connection.ping();
      log.info("📡 Resposta do PING: {}", pong);

      if ("PONG".equalsIgnoreCase(pong)) {
        log.info("✅ Redis conectado e respondendo corretamente!");
        log.info("🎉 Sistema pronto para usar cache Redis");
      } else {
        log.error("⚠️ Redis respondeu inesperadamente: {}", pong);
      }

      connection.close();
      log.info("🔒 Conexão fechada");

    } catch (Exception e) {
      log.error("❌ FALHA AO CONECTAR AO REDIS");
      log.error("❌ Host tentado: {}:{}", redisHost, redisPort);
      log.error("❌ Tipo de erro: {}", e.getClass().getName());
      log.error("❌ Mensagem: {}", e.getMessage());
      log.error("❌ Stack trace:", e);

      // Dicas de troubleshooting
      log.error("🔧 TROUBLESHOOTING:");
      log.error("   1. Verifique se o container Redis está rodando: docker ps");
      log.error("   2. Verifique os logs do Redis: docker logs delivery-redis");
      log.error("   3. Teste a conexão manualmente: docker exec delivery-api ping redis -c 3");
      log.error("   4. Verifique a rede: docker network inspect delivery_delivery-network");
      log.error("   5. Verifique as variáveis de ambiente: docker exec delivery-api env | grep REDIS");
    }
  }
}