# =============================
# 🌱 STAGE 1 — Build da aplicação
# =============================
FROM maven:amazoncorretto AS builder
WORKDIR /app

# 1️⃣ Copia apenas pom.xml primeiro e baixa dependências
# Isso cria uma camada de cache que só é invalidada quando o pom.xml muda
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2️⃣ Agora copia o código fonte
COPY src ./src

# 3️⃣ Build com otimizações
RUN mvn clean package -DskipTests -T 1C -B

# =============================
# 🚀 STAGE 2 — Execução da aplicação
# =============================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 4️⃣ Cria usuário não-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 5️⃣ Copia o jar com nome fixo
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]