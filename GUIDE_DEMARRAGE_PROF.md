# 🎓 Guide de Démarrage - Pour le Professeur

## 📋 Prérequis

- **Java 21** (JDK)
- **Maven 3.8+**
- **Node.js 18+** et npm
- **Git**

## 📥 Cloner le Projet

```bash
git clone https://github.com/IlyasAnkri/recipe-microservices-app.git
cd recipe-microservices-app
```

## 🔑 Configuration de la Clé OpenAI

Le projet utilise Spring AI avec OpenAI. Créez un fichier `.env` à la racine:

```bash
# .env
OPENAI_API_KEY=votre-clé-api-openai
```

**OU** définissez la variable d'environnement:

```bash
# Windows PowerShell
$env:OPENAI_API_KEY="votre-clé-api-openai"

# Windows CMD
set OPENAI_API_KEY=votre-clé-api-openai

# Linux/Mac
export OPENAI_API_KEY=votre-clé-api-openai
```

## 🏗️ Build du Projet

### Backend (Microservices)

```bash
# Build tous les services
mvn clean install -DskipTests
```

### Frontend (Angular)

```bash
cd frontend
npm install
cd ..
```

## ▶️ Démarrage des Services

### 1. Démarrer Eureka Server (Service Discovery)

```bash
cd services/eureka-server
java -jar target/eureka-server-1.0.0-SNAPSHOT.jar
```

**Attendez** que Eureka démarre (environ 30 secondes).  
**URL:** http://localhost:8761

---

### 2. Démarrer Substitution Engine Service

**Dans un nouveau terminal:**

```bash
cd services/substitution-engine-service
java -jar target/substitution-engine-service-1.0.0-SNAPSHOT.jar
```

**Attendez** que le service s'enregistre dans Eureka (environ 20 secondes).  
**URL:** http://localhost:8083

---

### 3. Démarrer API Gateway

**Dans un nouveau terminal:**

```bash
cd services/api-gateway
java -jar target/api-gateway-1.0.0-SNAPSHOT.jar
```

**URL:** http://localhost:8080

---

### 4. Démarrer le Frontend Angular

**Dans un nouveau terminal:**

```bash
cd frontend
npm start
```

**URL:** http://localhost:4200

---

## 🌐 URLs Importantes

| Service | URL |
|---------|-----|
| **Frontend (Angular)** | http://localhost:4200 |
| **Eureka Dashboard** | http://localhost:8761 |
| **API Gateway** | http://localhost:8080 |
| **Substitution Service** | http://localhost:8083 |

## 🧪 Tester l'Application

### 1. Vérifier Eureka

Ouvrez http://localhost:8761 - Vous devriez voir:
- `SUBSTITUTION-ENGINE-SERVICE`
- `API-GATEWAY`

### 2. Tester l'API de Substitution

```bash
curl -X POST http://localhost:8080/api/v1/substitutions/suggest \
  -H "Content-Type: application/json" \
  -d '{
    "ingredient": "butter",
    "quantity": "100g",
    "dietaryRestrictions": ["vegan"]
  }'
```

### 3. Utiliser le Frontend

1. Ouvrez http://localhost:4200
2. Entrez un ingrédient (ex: "butter")
3. Cliquez sur "Get Substitutions"
4. Vous devriez voir des suggestions de substitution

## 🏗️ Architecture

```
┌─────────────────┐
│   Frontend      │
│   (Angular)     │
│   Port: 4200    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   API Gateway   │
│   Port: 8080    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐      ┌──────────────────┐
│ Substitution    │◄─────┤  Eureka Server   │
│ Engine Service  │      │  Port: 8761      │
│ Port: 8083      │      └──────────────────┘
└─────────────────┘
```

## 📚 Technologies Utilisées

### Backend
- **Spring Boot 3.2.1**
- **Spring Cloud** (Eureka, Gateway)
- **Spring AI** (OpenAI Integration)
- **Java 21**

### Frontend
- **Angular 20**
- **Angular Material**
- **RxJS**

## 🔧 Dépannage

### Problème: Service ne démarre pas

**Solution:** Vérifiez que le port n'est pas déjà utilisé:
```bash
# Windows
netstat -ano | findstr :8761
netstat -ano | findstr :8083
netstat -ano | findstr :8080
netstat -ano | findstr :4200
```

### Problème: Service ne s'enregistre pas dans Eureka

**Solution:** Attendez 30-60 secondes après le démarrage d'Eureka avant de lancer les autres services.

### Problème: Erreur OpenAI API

**Solution:** Vérifiez que votre clé API est correctement configurée dans `.env` ou comme variable d'environnement.

## 📝 Notes

- **MongoDB, Redis, Kafka** sont optionnels pour la démo de base
- Le service de substitution fonctionne avec OpenAI ET un fallback local
- Si OpenAI n'est pas disponible, le système utilise des substitutions pré-configurées

## ✅ Ordre de Démarrage Recommandé

1. ✅ Eureka Server (8761) - **ATTENDRE 30s**
2. ✅ Substitution Engine Service (8083) - **ATTENDRE 20s**
3. ✅ API Gateway (8080) - **ATTENDRE 10s**
4. ✅ Frontend Angular (4200)

**Temps total de démarrage:** ~2 minutes

---

## 🎯 Démonstration Rapide

Pour une démo rapide devant le professeur:

1. Démarrez Eureka → Montrez le dashboard
2. Démarrez Substitution Service → Vérifiez dans Eureka
3. Démarrez API Gateway
4. Démarrez Frontend
5. Testez une substitution (ex: "butter" → suggestions vegan)
6. Montrez les logs dans les terminaux

**Bonne présentation !** 🚀
