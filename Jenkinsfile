pipeline {
agent any

```
triggers {
    pollSCM('H/5 * * * *')
}

stages {

    stage('Checkout') {
        steps {
            git branch: 'main',
                url: 'https://github.com/Korng12/final-devops-ex1.git'
        }
    }

    stage('Test') {
        steps {
            sh '''
            chmod +x gradlew
            ./gradlew clean test
            '''
        }
    }

    stage('Build') {
        steps {
            sh '''
            chmod +x gradlew
            ./gradlew clean build
            '''
        }
    }

    stage('Deploy') {
        steps {
            sh '''
            ansible-playbook \
              -i ansible/inventory.ini \
              ansible/playbook.yaml
            '''
        }
    }
}

post {

    success {
        echo 'Build, Test and Deployment completed successfully'
    }

    failure {
        emailext(
            subject: "Build Failed - ${env.JOB_NAME}",
            body: """
            Project: ${env.JOB_NAME}
            Build Number: ${env.BUILD_NUMBER}

            Build or Test failed.

            Check Jenkins console output:
            ${env.BUILD_URL}
            """,
            recipientProviders: [
                [$class: 'DevelopersRecipientProvider']
            ],
            to: 'srengty@gmail.com'
        )
    }
}
```

}
