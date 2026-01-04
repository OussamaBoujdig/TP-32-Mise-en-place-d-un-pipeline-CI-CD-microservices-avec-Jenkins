pipeline {
    agent any
    
    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }
    
    environment {
        SONAR_HOST_URL = 'http://localhost:9999'
        SONAR_TOKEN = credentials('sonar-token')
    }
    
    stages {
        stage('Clonage du code') {
            steps {
                echo '=== Clonage du repository ==='
                checkout scm
            }
        }
        
        stage('Build Maven - Server Eureka') {
            steps {
                echo '=== Build Server Eureka ==='
                dir('server_eureka') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Build Maven - Gateway') {
            steps {
                echo '=== Build Gateway ==='
                dir('gateway') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Build Maven - Client (Proprietaire)') {
            steps {
                echo '=== Build Service Proprietaire ==='
                dir('client') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Build Maven - Car (Moto)') {
            steps {
                echo '=== Build Service Moto ==='
                dir('car') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Tests Unitaires') {
            steps {
                echo '=== Execution des tests ==='
                dir('client') {
                    bat 'mvn test'
                }
                dir('car') {
                    bat 'mvn test'
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Analyse SonarQube - Client') {
            steps {
                echo '=== Analyse SonarQube Service Proprietaire ==='
                dir('client') {
                    bat """
                        mvn sonar:sonar ^
                        -Dsonar.projectKey=service-proprietaire ^
                        -Dsonar.projectName="Service Proprietaire" ^
                        -Dsonar.host.url=%SONAR_HOST_URL% ^
                        -Dsonar.token=%SONAR_TOKEN%
                    """
                }
            }
        }
        
        stage('Analyse SonarQube - Car') {
            steps {
                echo '=== Analyse SonarQube Service Moto ==='
                dir('car') {
                    bat """
                        mvn sonar:sonar ^
                        -Dsonar.projectKey=service-moto ^
                        -Dsonar.projectName="Service Moto" ^
                        -Dsonar.host.url=%SONAR_HOST_URL% ^
                        -Dsonar.token=%SONAR_TOKEN%
                    """
                }
            }
        }
        
        stage('Build et Deploy Docker Compose') {
            steps {
                echo '=== Deploiement Docker Compose ==='
                dir('deploy') {
                    bat 'docker-compose down --remove-orphans || exit 0'
                    bat 'docker-compose up -d --build'
                }
            }
        }
        
        stage('Verification des conteneurs') {
            steps {
                echo '=== Verification des conteneurs Docker ==='
                bat 'docker ps'
                sleep(time: 30, unit: 'SECONDS')
                bat 'docker ps'
            }
        }
    }
    
    post {
        success {
            echo '=== Pipeline execute avec succes ! ==='
        }
        failure {
            echo '=== Echec du pipeline ==='
        }
        always {
            echo '=== Fin du pipeline ==='
        }
    }
}
