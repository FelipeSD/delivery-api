# 🚚 Delivery API

API desenvolvida com **Spring Boot** para gerenciar funcionalidades relacionadas a entregas.  
Este projeto serve como base para futuras implementações de um sistema de logística/delivery.
Implementado durante o curso de Arquitetura de Sistemas do [Qualifica SP](https://www.alunos.cettpro.sp.gov.br/novo-emprego) em parceria com a FAT.


## 📋 ENUNCIADO DA PRÁTICA
### 🎯 Contexto e Problematização
Você foi contratado como desenvolvedor júnior pela startup DeliveryTech, uma nova empresa que quer competir com iFood e Uber Eats. O CTO da empresa te deu a primeira missão:

"Precisamos começar do zero. Temos uma ideia revolucionária para delivery, mas precisamos de uma base sólida.
Sua missão é preparar o ambiente de desenvolvimento e criar a estrutura inicial do nosso sistema.
Lembre-se: grandes projetos começam com fundações bem construídas!"


## ⚙️ Tecnologias Utilizadas

- **Java 24**
- **Spring Boot 3.5.6**
- **Spring Web** → criação de APIs REST  
- **Spring Data JPA** → persistência e acesso a dados  
- **H2 Database** → banco de dados em memória para desenvolvimento  
- **Lombok** → redução de boilerplate (getters/setters/constructors)  
- **Spring Boot DevTools** → suporte a hot reload no desenvolvimento  
- **JUnit 5** → testes automatizados  
## 📋 Endpoints

### Gerais
- `GET /health` — Status da aplicação (inclui versão Java)
- `GET /info` — Informações da aplicação
- `GET /h2-console` — Console do banco H2

### 2.1 ClienteController
Endpoints RESTful para gerenciamento de clientes:
- `POST /api/clientes` — Cadastrar cliente
- `GET /api/clientes/{id}` — Buscar cliente por ID
- `GET /api/clientes` — Listar clientes ativos
- `PUT /api/clientes/{id}` — Atualizar cliente
- `PATCH /api/clientes/{id}/status` — Ativar/desativar cliente
- `GET /api/clientes/email/{email}` — Buscar cliente por email

### 2.2 RestauranteController
Endpoints RESTful para restaurantes, com filtros e paginação:
- `POST /api/restaurantes` — Cadastrar restaurante
- `GET /api/restaurantes/{id}` — Buscar restaurante por ID
- `GET /api/restaurantes` — Listar restaurantes disponíveis
- `GET /api/restaurantes/categoria/{categoria}` — Listar por categoria
- `PUT /api/restaurantes/{id}` — Atualizar restaurante
- `GET /api/restaurantes/{id}/taxa-entrega/{cep}` — Calcular taxa de entrega por CEP

### 2.3 ProdutoController
Endpoints RESTful para produtos, com filtros por restaurante:
- `POST /api/produtos` — Cadastrar produto
- `GET /api/produtos/{id}` — Buscar produto por ID
- `GET /api/restaurantes/{restauranteId}/produtos` — Listar produtos de um restaurante
- `PUT /api/produtos/{id}` — Atualizar produto
- `PATCH /api/produtos/{id}/disponibilidade` — Alterar disponibilidade do produto
- `GET /api/produtos/categoria/{categoria}` — Listar produtos por categoria

### 2.4 PedidoController
Endpoints RESTful para operações de pedidos:
- `POST /api/pedidos` — Criar pedido (transação complexa)
- `GET /api/pedidos/{id}` — Buscar pedido completo por ID
- `GET /api/clientes/{clienteId}/pedidos` — Histórico de pedidos do cliente
- `PATCH /api/pedidos/{id}/status` — Atualizar status do pedido
- `DELETE /api/pedidos/{id}` — Cancelar pedido
- `POST /api/pedidos/calcular` — Calcular total do pedido sem salvar

### 2.5 Documentação API (Swagger)
- `GET /scalar` — Interface Swagger da API

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- [Java 24+](https://jdk.java.net/)
- [Maven 3.9+](https://maven.apache.org/)

### Passos para rodar localmente
```bash
# Clonar repositório
git clone https://github.com/FelipeSD/delivery-api.git
cd delivery-api

# Executar aplicação
./mvnw spring-boot:run

A aplicação será iniciada em:
👉 http://localhost:8080
```
## 🧪 Testes
Para rodar os testes automatizados:

```bash
./mvnw test
```

## 📖 Próximos Passos
Criar entidades principais (ex: Pedido, Cliente, Entregador).

Implementar endpoints REST para CRUD básico.

Configurar banco de dados relacional (PostgreSQL ou MySQL).

Adicionar autenticação/autorização (Spring Security + JWT).

## 📜 Licença
Este projeto está sob a licença MIT. Sinta-se à vontade para usar e modificar.

## ✨ Desenvolvedor
Desenvolvido por Felipe Damasceno - TI 03362 ARQUITETURA DE SISTEMAS 08h30 às 11h50 NOVO EMPREGO 🚀