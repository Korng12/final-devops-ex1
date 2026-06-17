pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')
    }

    environment {
        SPRING_PROFILES_ACTIVE = 'test'
        SPRING_DATASOURCE_URL = 'jdbc:sqlite:test.db'
        SPRING_DATASOURCE_DRIVER_CLASS_NAME = 'org.sqlite.JDBC'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Korng12/final-devops-ex1.git'
            }
        }

        stage('Test with SQLite') {
            steps {
                sh '''
                    chmod +x gradlew
                    ./gradlew clean test --tests '*' -Dspring.profiles.active=test
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'build/test-results/test/*.xml'
                }
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean assemble'
            }
        }

        stage('Deploy to Web Server') {
            steps {
                sh 'ansible-playbook -i ansible/inventory.ini ansible/playbook.yaml'
            }
        }
    }

    post {
        success {
            echo 'Build, test, and deployment completed successfully.'
        }

        failure {
            emailext(
                subject: "Jenkins build failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
Project: ${env.JOB_NAME}
Build Number: ${env.BUILD_NUMBER}
Result: ${currentBuild.currentResult}

The build, test, or deploy stage failed.

Console output:
${env.BUILD_URL}console
""",
                recipientProviders: [
                    [$class: 'DevelopersRecipientProvider'],
                    [$class: 'CulpritsRecipientProvider']
                ],
                to: 'srengty@gmail.com'
            )
        }
    }
}
