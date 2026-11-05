# Étape 1 : construction du jar (les tests tournent avant chaque push avec mvn verify, pas pendant le déploiement)
FROM maven:3.9-eclipse-temurin-21 AS construction
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src src
RUN mvn -q package -DskipTests

# Étape 2 : image d'exécution, sans Maven ni code source
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=construction /app/target/*.jar application.jar
# Les dates sont stockées en UTC ; ce fuseau sert uniquement à les afficher à l'heure de Paris
ENV TZ=Europe/Paris
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]
