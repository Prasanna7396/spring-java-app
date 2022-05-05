pipeline {
  agent any
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
        checkout([$class: 'GitSCM', branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'myGithub', url: 'https://github.com/Prasanna7396/spring-java-app.git']]])
      }
    }
    stage('Maven Build and Artifact archival') {
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
          sh "mvn sonar:sonar -Dsonar.projectKey=SpringWebAppMain"
        }
      }
    }
    stage('Build Docker Image and Push Image to DockerHub') {
      steps {
        echo "Creating the docker image"
        sh 'docker build -t "$DOCKERHUB_USER"/"$REGISTRY_NAME":"main"-"$BUILD_NUMBER" .'
      }
      post {
        success {
          echo "Pushing the docker image to docker hub"
          sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
          sh 'docker push "$DOCKERHUB_USER"/"$REGISTRY_NAME":"main"-"$BUILD_NUMBER"'
        }
        always {
          sh 'docker logout'
        }
      }
    }
    stage('Docker Deployment - main') {
      steps {
          emailext body: "Please approve the below url build for Deployment \n ${env.BUILD_URL} \n NOTE: If not approved, build will be aborted by default",
          recipientProviders: [[$class: 'DevelopersRecipientProvider'],[$class: 'RequesterRecipientProvider']],
          mimeType: 'text/html',
          subject: "Main Deployment Approval - Build : ${env.BUILD_NUMBER} | Job: ${env.JOB_NAME}"

          timeout(time: 5, unit: 'DAYS') {
          input message: 'Approve Main Deployment?'
        }
        sh 'docker run -d -p 8090:8080 --name java-main-"$BUILD_NUMBER" "$DOCKERHUB_USER"/"$REGISTRY_NAME":"main"-"$BUILD_NUMBER"'
      }
    }
  }
  post {
    always {
        emailext body: "Deployment Status: ${currentBuild.currentResult}\n Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n More info at: ${env.BUILD_URL}",
        recipientProviders: [[$class: 'DevelopersRecipientProvider'],[$class: 'RequesterRecipientProvider']],
        subject: "Main Deployment - Build ${currentBuild.currentResult}: Job ${env.JOB_NAME}"
    }
  }
}
