# 🚚 Delivery API

API desenvolvida com **Spring Boot** para gerenciar funcionalidades relacionadas a entregas.  
Este projeto serve como base para futuras implementações de um sistema de logística/delivery.

---

## ⚙️ Tecnologias Utilizadas

- **Java 25**
- **Spring Boot 3.5.6**
- **Spring Web** → criação de APIs REST  
- **Spring Data JPA** → persistência e acesso a dados  
- **H2 Database** → banco de dados em memória para desenvolvimento  
- **Lombok** → redução de boilerplate (getters/setters/constructors)  
- **Spring Boot DevTools** → suporte a hot reload no desenvolvimento  
- **JUnit 5** → testes automatizados  

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- [Java 25+](https://jdk.java.net/)
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
# 🧪 Testes
Para rodar os testes automatizados:

```bash
./mvnw test
```

# 📖 Próximos Passos
Criar entidades principais (ex: Pedido, Cliente, Entregador).

Implementar endpoints REST para CRUD básico.

Configurar banco de dados relacional (PostgreSQL ou MySQL).

Adicionar autenticação/autorização (Spring Security + JWT).

# 📜 Licença
Este projeto está sob a licença MIT. Sinta-se à vontade para usar e modificar.

# ✨ Autor
Desenvolvido por DeliveryTech 🚀