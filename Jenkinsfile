pipeline {
    agent any

    stages {
        /*stage('Stop & Clean Old Environment') {
            steps {
                // -v removes sql volume, starts fresh
                bat 'docker compose down -v || ver > nul'
            }
        }*/
        
        stage('Deploy Microservices') {
            // only restarts containers with different image IDs
            // from previous built
            steps {
                bat 'docker compose up -d'
            }
        }

        stage('Verify Containers') {
            steps {
                bat 'docker compose ps'
            }
        }
    }
}