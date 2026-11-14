pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code from Git repository...'
                checkout scm
            }
        }

        stage('Automated Tests') {
            steps {
                echo 'Running automated JUnit unit tests (CI Enhancement)...'
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build & Package') {
            steps {
                echo 'Compiling and packaging JAR artifact...'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Post-Build Archive') {
            steps {
                echo 'Archiving build artifacts...'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    triggers {
        pollSCM('* * * * *')
    }

    post {
        success {
            echo 'Pipeline completed successfully! Artifact is packaged and archived.'
        }
        failure {
            echo 'Pipeline failed! Check build or test logs for integration errors.'
        }
    }
}
