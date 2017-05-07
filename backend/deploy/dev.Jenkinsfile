#!/usr/bin/env groovy

pipeline {
    agent any

    stages {
        stage('Copy release jar') {
            steps {
                step([$class     : 'CopyArtifact',
                      projectName: 'lsd-members-backend',
                      filter     : 'target/scala-2.11/luskydive-assembly-*'])

                sh 'mv target/scala-2.11/*.jar deploy.jar'
                sh 'rm -rf target'
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([
                        file(credentialsId: 'lsd-dev-deploy', variable: 'DEV_KEY')
                ]) {
                    sh 'chmod 0600 $DEV_KEY'
                    sh 'scp -i $DEV_KEY deploy.jar deploy@api.dev.leedsskydivers.com:/home/api/jars/$BUILD_TAG.jar'
                    sh 'ssh -i $DEV_KEY deploy@api.dev.leedsskydivers.com /opt/deploy.sh /home/api/jars/$BUILD_TAG.jar'
                }
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts 'deploy.jar'
            }
        }
    }
}
