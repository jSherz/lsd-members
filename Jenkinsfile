pipeline {
  agent any
  stages {
    stage('Test') {
      steps {
        sh '''cd backend
sbt test'''
      }
    }
  }
}