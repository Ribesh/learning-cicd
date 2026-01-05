pipeline {
    agent any
    
    parameters {
        string(
            name: 'SERVER_IP',
            defaultValue: '52.73.93.84',
            description: 'Enter server IP address'
        )
    }
    
    environment {
        SERVER_IP   =   "${params.SERVER_IP}"
        DOCKER_USERNAME = "ribeshshr"
        IMAGE_TAG   =   "${BUILD_NUMBER}"

    }
    
    stages {
        stage('Configure SSH') {
            steps {
                sh '''
                mkdir -p ~/.ssh
                chmod 700 ~/.ssh
                cat > ~/.ssh/config <<'EOF'
Host *
  StrictHostKeyChecking no
EOF
                cat ~/.ssh/config   #to verify
                touch ~/.ssh/known_hosts
                chmod 600 ~/.ssh/known_hosts
                '''
            }
        }
        stage('Populate SSH Key') {
            steps {
                withCredentials([string(credentialsId: 'SSH_KEY64', variable: 'SSH_KEY64')]) {
                    sh '''
                    echo "$SSH_KEY64" | base64 -d > mykey.pem
                    chmod 600 mykey.pem
                    ssh-keygen -R ${SERVER_IP}
                    '''
                }
            }
        }

        stage('Build & Push Docker Image'){
            steps{
                withCredentials([string(credentialsId: 'DOCKER_PASSWORD', variable: 'DOCKER_PASSWORD')]) {
                    sh '''
                        docker build -t ${DOCKER_USERNAME}/learning-cicd-1:${IMAGE_TAG} .
                        echo $DOCKER_PASSWORD | docker login --username ${DOCKER_USERNAME} --password-stdin
                        docker push ${DOCKER_USERNAME}/learning-cicd-1:${IMAGE_TAG}
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                ssh ec2-user@${SERVER_IP} -i mykey.pem -T \
                    'docker stop learning-cicd || docker rm learning-cicd || docker run -d --name learninig-cicd -p 80:80 ${DOCKER_USERNAME}/learning-cicd-1:${IMAGE_TAG}'
                '''
            }
        }
    }
}