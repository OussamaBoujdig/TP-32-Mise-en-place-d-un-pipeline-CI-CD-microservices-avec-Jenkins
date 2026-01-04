# Guide CI/CD - Jenkins, SonarQube, Docker

## Prérequis
- Docker Desktop installé et démarré
- Jenkins installé (port 8080)
- Java 17 et Maven installés
- Ngrok pour le webhook GitHub (optionnel)

---

## 1. Démarrer SonarQube

```bash
cd deploy
docker-compose -f sonarqube-compose.yml up -d
```

Accès: http://localhost:9999
- Login par défaut: `admin` / `admin`
- Changer le mot de passe à la première connexion

### Créer un token SonarQube:
1. Aller dans **My Account** → **Security**
2. Générer un token nommé `jenkins-token`
3. Copier le token

---

## 2. Configurer Jenkins

### 2.1 Installer les plugins nécessaires
Dans **Manage Jenkins** → **Plugins** → **Available plugins**:
- SonarQube Scanner
- Docker Pipeline
- Pipeline
- Git

### 2.2 Configurer les outils
Dans **Manage Jenkins** → **Tools**:

**JDK:**
- Nom: `JDK17`
- JAVA_HOME: chemin vers JDK 17

**Maven:**
- Nom: `Maven3`
- Installer automatiquement ou spécifier le chemin

### 2.3 Configurer les credentials
Dans **Manage Jenkins** → **Credentials**:
- Ajouter un **Secret text** avec ID `sonar-token` contenant le token SonarQube

### 2.4 Configurer SonarQube Server
Dans **Manage Jenkins** → **System**:
- **SonarQube servers** → Add
- Name: `SonarQube`
- Server URL: `http://localhost:9999`
- Server authentication token: sélectionner `sonar-token`

---

## 3. Créer le Job Jenkins

### 3.1 Nouveau Pipeline
1. **New Item** → Nom: `microservices-moto-pipeline` → **Pipeline**
2. Dans **Pipeline**:
   - Definition: **Pipeline script from SCM**
   - SCM: **Git**
   - Repository URL: votre URL GitHub
   - Branch: `*/main`
   - Script Path: `Jenkinsfile`

### 3.2 Configurer le Webhook GitHub (optionnel)
1. Cocher **GitHub hook trigger for GITScm polling**
2. Dans GitHub → Settings → Webhooks:
   - Payload URL: `http://<ngrok-url>/github-webhook/`
   - Content type: `application/json`
   - Events: Just the push event

---

## 4. Lancer un Build Manuel

1. Ouvrir le job dans Jenkins
2. Cliquer sur **Build Now**
3. Suivre l'exécution dans **Console Output**

### Stages du pipeline:
1. **Clonage du code** - checkout du repository
2. **Build Maven** - compilation des 4 services
3. **Tests Unitaires** - exécution des tests
4. **Analyse SonarQube** - analyse de code pour client et car
5. **Docker Compose** - déploiement des conteneurs
6. **Vérification** - liste des conteneurs

---

## 5. Vérifier les Résultats

### 5.1 Jenkins Console Output
Vérifier que chaque stage est vert:
- ✅ Clonage: `checkout main`
- ✅ Builds Maven: `BUILD SUCCESS`
- ✅ SonarQube: `ANALYSIS SUCCESSFUL`
- ✅ Docker: conteneurs démarrés

### 5.2 SonarQube Dashboard
http://localhost:9999
- Projet `service-proprietaire` - métriques visibles
- Projet `service-moto` - métriques visibles

### 5.3 Docker
```bash
docker ps
```
Conteneurs attendus:
- mysql-container1
- eureka-server
- gateway-service
- proprietaire-service
- moto-service
- consul-container
- phpmyadmin-container

---

## 6. Tester les Services

```bash
# Health check Gateway
curl http://localhost:8888/actuator/health

# Health check Eureka
curl http://localhost:8761/actuator/health

# API Proprietaire
curl http://localhost:8088/api/client

# API Moto via Gateway
curl http://localhost:8888/SERVICE-MOTO/api/car
```

---

## 7. Test Webhook GitHub

```bash
# Modifier un fichier
echo "# Test webhook" >> README.md
git add README.md
git commit -m "test: declenchement webhook"
git push
```

Vérifier qu'un nouveau build démarre automatiquement dans Jenkins.

---

## Ports Utilisés

| Service | Port |
|---------|------|
| Jenkins | 8080 |
| SonarQube | 9999 |
| Eureka | 8761 |
| Gateway | 8888 |
| Proprietaire | 8088 |
| Moto | 8089 |
| MySQL | 3307 |
| Consul | 8500 |
| phpMyAdmin | 8081 |

---

## Dépannage

### Erreur Maven
- Vérifier que Maven est dans le PATH
- Vérifier la version Java (17 requise)

### Erreur SonarQube
- Vérifier que SonarQube est démarré
- Vérifier le token dans Jenkins credentials

### Erreur Docker
- Vérifier que Docker Desktop est démarré
- Vérifier les ports disponibles
