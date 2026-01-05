pipeline {
    agent any
    
    parameters {
        string(
            name: 'SERVER_IP',
            defaultValue: '18.207.158.50',
            description: 'Enter server IP address'
        )
    }
    
    environment {
        SERVER_IP   =   "${params.SERVER_IP}"
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
        stage('Deploy') {
            steps {
                sh '''
                ssh ec2-user@${SERVER_IP} -i mykey.pem -T \
                    'cd /usr/share/nginx/html && git pull origin main'
                '''
            }
        }
    }
}