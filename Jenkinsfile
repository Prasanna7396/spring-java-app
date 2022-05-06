pipeline {
  agent {
    label "QAEnv"
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
        checkout([$class: 'GitSCM', branches: [[name: '*/qa']], extensions: [], userRemoteConfigs: [[credentialsId: 'myGithub', url: 'https://github.com/Prasanna7396/spring-java-app.git']]])
      }
    }
    stage('Selenium test cases') {
      steps {
	script {		
	  try {
           sh 'mvn clean test -DMaven.test.failure.ignore=true -Dtest="TestSelenium" surefire-report:report-only'
           echo "Testing has been completed. !!"
	  }
          catch (Exception e) {
           echo 'Excpetion has occurred' + e.toString()
           echo 'Error has occured while running Selenium Test cases, please check the report'		
          }    
        }
      }	
    }
    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('sonarqube-8.9.2') {
          sh "mvn sonar:sonar -Dsonar.projectKey=SpringWebAppQA"
        }
      }
    }
    stage('Docker Build and Image Push') {
      steps {
        echo "Creating the docker image"
        sh 'docker build -t "$DOCKERHUB_USER"/"$REGISTRY_NAME":"QA"-"$BUILD_NUMBER" .'
      }
      post {
        success {
          echo "Pushing the docker image to docker hub repo"
          sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
          sh 'docker push "$DOCKERHUB_USER"/"$REGISTRY_NAME":"QA"-"$BUILD_NUMBER"'
        }
        always {
          sh 'docker logout'
        }
      }
    }
    stage('Docker Deployment - qa') {
      steps {
        emailext body: '${FILE, path="target/site/surefire-report.html"}',
         recipientProviders: [[$class: 'DevelopersRecipientProvider'],[$class: 'RequesterRecipientProvider']],
         mimeType: 'text/html',
         subject: "QA Test Report - Build No - ${env.BUILD_NUMBER} | Job: ${env.JOB_NAME}"

        timeout(time: 1, unit: 'DAYS') {
          input message: 'Approve QA Deployment?'
        }
        sh 'docker run -d -p 8092:8080 --name java-qa-"$BUILD_NUMBER" "$DOCKERHUB_USER"/"$REGISTRY_NAME":"QA"-"$BUILD_NUMBER"'
      }
    }
  }
  post {
    always {
      emailext body: "Deployment Status: ${currentBuild.currentResult}\n Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n More info at: ${env.BUILD_URL}",
       recipientProviders: [[$class: 'DevelopersRecipientProvider'],[$class: 'RequesterRecipientProvider']],
       subject: "QA Deployment -  Build ${currentBuild.currentResult}: Job ${env.JOB_NAME}"
    }
  }
}
