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
Para visualizar os endpoints disponíveis, acesse a documentação do swagger:

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