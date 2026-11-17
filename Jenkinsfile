pipeline {
    agent any

    environment {
        VENV_DIR = '.venv'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code from GitHub...'
                checkout scm
            }
        }

        stage('Setup Python Environment') {
            steps {
                echo 'Creating virtual environment and installing dependencies...'
                sh '''
                    python3 -m venv $VENV_DIR
                    . $VENV_DIR/bin/activate
                    pip install --upgrade pip --quiet
                    pip install -r requirements.txt
                '''
            }
        }

        stage('Lint') {
            steps {
                echo 'Running linter (flake8)...'
                sh '''
                    . $VENV_DIR/bin/activate
                    pip install flake8 --quiet
                    flake8 app/ --max-line-length=100 --ignore=E501,W503
                '''
            }
        }

        stage('Test') {
            steps {
                echo 'Running pytest with coverage...'
                sh '''
                    . $VENV_DIR/bin/activate
                    pytest tests/ -v --tb=short --cov=app --cov-report=xml --cov-report=term
                '''
            }
            post {
                always {
                    echo 'Tests completed.'
                }
            }
        }

        stage('Build Verification') {
            steps {
                echo 'Verifying app can be imported...'
                sh '''
                    . $VENV_DIR/bin/activate
                    python3 -c "from app.main import app; print('App import: OK')"
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline PASSED — All stages completed successfully!'
        }
        failure {
            echo 'Pipeline FAILED — Check logs above for errors.'
        }
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
    }
}
