pipeline {
  agent {
    label "DevEnv"
  }
  environment {
    DOCKERHUB_CREDENTIALS = credentials('dockerHub')
    REGISTRY_NAME = "simple-java-app"
    DOCKERHUB_USER = "prasanna7396"
  }
  stages {
    stage('Git Checkout') {
      steps {
        script {
          properties([pipelineTriggers([pollSCM('H */1 * * *')])])
        }
        checkout([$class: 'GitSCM', branches: [[name: '*/dev']], extensions: [], userRemoteConfigs: [[credentialsId: 'myGithub', url: 'https://github.com/Prasanna7396/spring-java-app.git']
        ]])
      }
    }
    stage('Maven build and Artifact archival') {
      steps {
        sh 'mvn clean package'
      }
      post {
        success {
          echo "Now Archiving the Artifacts...."
          archiveArtifacts artifacts: '**/*.war'
        }
      }
    }
    stage('Junit test report generation') {
      steps {
        sh 'mvn surefire-report:report-only'
      }
    }
    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('sonarqube-8.9.2') {
          sh "mvn sonar:sonar -Dsonar.projectKey=SpringWebAppDev"
        }
      }
    }
    stage('Docker Build and Image Push') {
      steps {
        echo "Creating the docker image"
        sh 'docker build -t "$DOCKERHUB_USER"/"$REGISTRY_NAME":"Dev"-"$BUILD_NUMBER" .'
      }
      post {
        success {
          echo "Pushing the docker image to docker hub repo"
          sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
          sh 'docker push "$DOCKERHUB_USER"/"$REGISTRY_NAME":"Dev"-"$BUILD_NUMBER"'
        }
        always {
          sh 'docker logout'
        }
      }
    }
    stage('Docker Deployment - dev') {
      steps {
        sh 'docker run -d -p 8091:8080 --name java-dev-"$BUILD_NUMBER" "$DOCKERHUB_USER"/"$REGISTRY_NAME":"Dev"-"$BUILD_NUMBER"'
      }
    }
  }
  post {
    always {
        emailext body: "Deployment Status: ${currentBuild.currentResult}: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n More info at: ${env.BUILD_URL}",
        recipientProviders: [[$class: 'DevelopersRecipientProvider'],[$class: 'RequesterRecipientProvider']],
        subject: "Dev Deployment -  Build ${currentBuild.currentResult}: Job ${env.JOB_NAME}"
    }
  }
}

