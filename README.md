# Recipe Adjuster - Microservices Application

Application de suggestion de recettes avec substitution d'ingrédients utilisant Spring Boot, Angular et Spring AI.

## 🏗️ Architecture

### Microservices Backend
- **Eureka Server** (Port 8761) - Service Discovery
- **API Gateway** (Port 8080) - Point d'entrée unique
- **Substitution Engine Service** (Port 8083) - Suggestions de substitutions avec Spring AI
- **Recipe Search Service** (Port 8082) - Recherche de recettes
- **Recipe Database Service** (Port 8084) - Gestion de la base de données de recettes
- **Ingredient Matching Service** (Port 8081) - Correspondance d'ingrédients

### Frontend
- **Angular Application** (Port 4200) - Interface utilisateur moderne

## 🚀 Technologies

### Backend
- Java 21
- Spring Boot 3.2.1
- Spring Cloud (Eureka, Gateway)
- Spring AI (OpenAI Integration)
- Spring Kafka
- MongoDB
- Redis
- PostgreSQL

### Frontend
- Angular 20
- Angular Material
- NgRx (State Management)
- RxJS

## 📋 Prérequis

- Java 21
- Node.js 18+
- Maven 3.8+
- MongoDB (optionnel)
- Redis (optionnel)
- Kafka (optionnel)

## 🔧 Installation

### 1. Cloner le projet
```bash
git clone <votre-repo>
cd suggestionrecette
```

### 2. Compiler les services backend
```bash
# Compiler tous les services
mvn clean install -DskipTests

# Ou compiler individuellement
cd services/eureka-server
mvn clean package -DskipTests
```

### 3. Installer le frontend
```bash
cd frontend
npm install --legacy-peer-deps
```

## ▶️ Démarrage

### Backend

#### Démarrer Eureka Server
```bash
cd services/eureka-server
java -jar target/eureka-server-1.0.0-SNAPSHOT.jar
```

#### Démarrer Substitution Engine Service
```bash
cd services/substitution-engine-service
java -jar target/substitution-engine-service-1.0.0-SNAPSHOT.jar
```

#### Démarrer API Gateway
```bash
cd services/api-gateway
java -jar target/api-gateway-1.0.0-SNAPSHOT.jar
```

### Frontend
```bash
cd frontend
npm start
```

## 🌐 URLs

- **Frontend:** http://localhost:4200
- **Eureka Dashboard:** http://localhost:8761
- **API Gateway:** http://localhost:8080
- **Substitution Service:** http://localhost:8083

## 🔑 Configuration

### OpenAI API Key
Pour utiliser Spring AI avec OpenAI, configurez votre clé API dans:
```
services/substitution-engine-service/src/main/resources/application.properties
```

## 📚 Documentation

Consultez le dossier `docs/` pour la documentation complète:
- Architecture des microservices
- Guide d'intégration Eureka
- Documentation API
- Stories et user stories

## 🧪 Tests

```bash
# Tests backend
mvn test

# Tests frontend
cd frontend
npm test
```

## 📦 Build de Production

### Backend
```bash
mvn clean package
```

### Frontend
```bash
cd frontend
npm run build
```

## 👥 Auteurs

Projet réalisé dans le cadre du cours de microservices.

## 📄 Licence

Ce projet est à usage éducatif.
