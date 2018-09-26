pipeline {
  agent any
  stages {
    stage('Backend Test') {
      steps {
        sh '''cd backend
/opt/sbt/bin/sbt test'''
      }
    }
  }
}