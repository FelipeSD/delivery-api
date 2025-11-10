# Delivery API — Guia do Usuário, Guia do Desenvolvedor e Relatório de Testes

---

## 1. Guia do Usuário

### 1.1 Objetivo

Manual curto para usuários da API do Sistema de Delivery: autenticação, principais endpoints e exemplos de uso.

### 1.2 Requisitos

* Acesso à rede que alcança a API (ex.: [http://host:8080](http://host:8080))
* Token JWT válido para endpoints autenticados

### 1.3 Autenticação

* Endpoint de login: `POST /api/auth/login`
* Corpo (JSON):

  ```json
  { "username": "user@example.com", "password": "senha" }
  ```
* Resposta: `{ "token": "<JWT>", "expiresIn": 86400000 }`
* Use o header: `Authorization: Bearer <JWT>` para chamadas autenticadas.

### 1.4 Principais Endpoints

* **Produtos**

  * `GET /api/produtos` — lista paginada (parâmetros: `page`, `size`, filtros por nome/categoria)
  * `GET /api/produtos/{id}` — obtém produto (cacheado)
  * `POST /api/produtos` — cria produto (autenticado)
  * `PATCH /api/produtos/{id}` — atualiza parcialmente (autenticado)
  * `DELETE /api/produtos/{id}` — remove (autenticado)

* **Restaurantes**

  * `GET /api/restaurantes` — lista e filtros
  * `GET /api/restaurantes/{id}`
  * `POST /api/restaurantes` (autenticado)

* **Pedidos**

  * `POST /api/pedidos` — criar pedido (autenticado)
  * `GET /api/pedidos/{id}` — status e itens
  * `GET /api/pedidos` — busca com filtros (data, status)

* **Dashboard / Health**

  * `GET /api/dashboard` — métricas resumidas (autenticado/admin)
  * `GET /actuator/health` — healthcheck público (conforme config)

### 1.5 Exemplos rápidos (curl)

* Login:

  ```bash
  curl -s -X POST http://localhost:8080/api/auth/login \
    -H 'Content-Type: application/json' \
    -d '{"usuario":"admin","senha":"123456"}'
  ```
* Buscar produto:

  ```bash
  curl -s http://localhost:8080/api/produtos/123 -H "Authorization: Bearer <JWT>"
  ```

### 1.6 Erros comuns e soluções

* `401 Unauthorized` — token inválido/expirado: faça login novamente.
* `404 Not Found` — id inexistente.
* `500 Internal Server Error` — verificar logs do serviço e health endpoints.

---

## 2. Guia do Desenvolvedor

### 2.1 Visão geral do projeto

Projeto Spring Boot modularizado em: `controllers`, `services`, `repositories`, `entities`, `dtos`, `config`, `monitoring`, `security`, `validations`.

Estrutura principal:

* API REST (Spring Web)
* Persistência: JPA/Hibernate + PostgreSQL
* Cache: Redis (Lettuce)
* Segurança: JWT + Spring Security
* Observabilidade: Actuator + Micrometer

### 2.2 Como rodar localmente

Pré-requisitos: Java 21, Maven, Docker (opcional)

1. Configurar `.env` ou variáveis de ambiente (ex.: DB/REDIS).
2. Rodar bancos via Docker Compose (opcional):

   ```bash
   docker compose up -d db redis
   ```
3. Build & run:

   ```bash
   ./mvnw clean package
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=docker
   ```
4. Acessar: `http://localhost:8080` e Swagger: `/swagger-ui.html` (se habilitado)

### 2.3 Configurações importantes

* `application.yml` com perfis: `default`, `docker`.
* Variáveis via ambiente: `SPRING_DATASOURCE_URL`, `SPRING_REDIS_HOST`, `JWT_SECRET`, etc.
* Habilitar batch inserts no JPA:

  ```yaml
  spring:
    jpa:
      properties:
        hibernate.jdbc.batch_size: 1000
        hibernate.order_inserts: true
        hibernate.order_updates: true
  ```

### 2.4 Dependências e build

* Gerenciado via Maven. Executar testes: `./mvnw test`.
* Plugins: surefire, failsafe (integração), jacoco.

### 2.5 Padrões e boas práticas

* Seguir SOLID e princípios de Clean Code.
* DTOs para comunicação externa; Entities apenas na camada de persistência.
* Services devem ser transacionais (`@Transactional`) e pequenos.
* Não logar dados sensíveis (senhas, tokens).
* Usar `@Validated` para validações de entrada e validators personalizados.

### 2.6 Caching e Redis

* Objetos serializados com `GenericJackson2JsonRedisSerializer` e `ObjectMapper` compartilhado.
* CacheManager configura TTL por cache (`produtos`, `pedidos`).
* Healthchecks e `RedisConnectionVerifier` para validar status no startup.

### 2.7 Testes

* Tests unitários com JUnit + Mockito (`src/test/java`)
* Integração com Testcontainers em `BaseIntegrationTest`.
* Cobertura: JaCoCo, meta >= 80% nas linhas críticas.

### 2.8 Debug e observabilidade

* Endpoints Actuator: `/actuator/health`, `/actuator/metrics`.
* Logs configurados em `logback-spring.xml`.
* Métricas exportadas para Prometheus (Micrometer).

### 2.9 Deploy / Docker

* `Dockerfile` para construir imagem da API.
* `docker-compose.yml` já contém serviços: `app`, `db`, `redis` com healthchecks.
* Práticas: usar `restart: unless-stopped`, redes próprias e volumes para persistência.

### 2.10 Checklist para PRs

* Testes unitários passam
* Estilo (Checkstyle/linters) OK
* Documentação do endpoint atualizada (Swagger/OpenAPI)
* Mensagem de commit clara e link para issue

---

## 3. Relatório de Testes (Resumo)

### 3.1 Escopo do Teste

* Unitários: serviços, validadores, utilitários.
* Integração: controllers e fluxos com DB e Redis (Testcontainers).
* End-to-end básico com endpoints críticos (autenticação, pedidos, produtos).

### 3.2 Resultados principais

* **Total testes unitários:** X (todos verdes)
* **Integração (IT):** 3 tests (`PedidoControllerIT`, `ProdutoControllerIT`, `RestauranteControllerIT`) — passaram em ambiente CI com Testcontainers.
* **Cobertura JaCoCo:** média projetada >= 80% nas áreas críticas (serviços e validações).

> Observações: substituir `X` pelos números reais extraídos da pipeline CI (relatório JaCoCo & Surefire).

### 3.3 Problemas encontrados e correções

* *Caso A:* Race condition ao inserir massivamente em paralelo — mitigado isolando transações por thread e evitando `EntityManager` compartilhado.
* *Caso B:* Serialização de objetos com `LocalDateTime` gerava erro — resolvido registrando `JavaTimeModule` no `ObjectMapper`.
* *Caso C:* Redis falhando na inicialização antes da app — adicionado `depends_on` com healthchecks e `RedisConnectionVerifier`.

### 3.4 Testes de performance (inserção em massa)

* Cenário: inserir 500k produtos com paralelismo (8 threads), batch size 5k, JPA batching habilitado.
* Métrica esperada: throughput e tempo total dependem do hardware; medir em ambiente target.
* Recomendação: usar COPY do Postgres para cargas maiores ou pipelines ETL.

### 3.5 Riscos e recomendações

* **Risco:** Segredos expostos em variáveis de ambiente sem vault. *Recomendação:* migrar para Vault/Secrets Manager.
* **Risco:** Backup/restore das bases e persistência de volumes não testados. *Recomendação:* rotinas de backup e testes periódicos.
* **Recomendação final:** integrar testes de performance e chaos engineering (simular falhas) antes do go-live.

---

## 4. Anexos rápidos

* Swagger/OpenAPI disponível em `/api-docs` e `/swagger-ui.html`.
* Health: `/actuator/health`, Metrics: `/actuator/metrics`.
* Comandos úteis:

  * `docker compose up --build --force-recreate`
  * `./mvnw test`
  * `./mvnw -Pcoverage verify`

---