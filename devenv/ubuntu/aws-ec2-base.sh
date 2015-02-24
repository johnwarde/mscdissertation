#!/bin/bash         
# ~/mscdissertation/devenv/ubuntu/aws-ec2-base.sh
# Sets up software needed to run the reference application components on an Amazon Web Service EC2 instance 



# NOTE: the '-y' option allow us to run unattended

# Install AWS Command Line
sudo apt-get install awscli -y

# Install Java
sudo add-apt-repository ppa:webupd8team/java -y
sudo apt-get update -y
sudo apt-get install oracle-java7-installer -y       # We still need to press OK for this package
sudo apt-get install oracle-java7-set-default -y

# Install RabbitMQ Server
sudo apt-get install rabbitmq-server -y
sudo rabbitmq-plugins enable rabbitmq_management
# Need to stop and start RabbitMQ to enable the WebUI
sudo rabbitmqctl stop
sudo invoke-rc.d rabbitmq-server start

# Optionally
sudo apt-get install unzip -y

# Verify installed software
echo ---------------------------------------
java -version 
javac -version 
sudo rabbitmqctl list_exchanges


# Configuring the AWS Command Line Interface
# http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html
# Can set environment variables to AccessID, SecretKey but needs to be done through a startup .bashrc (save into an AMI)
# Configure AWS CLI (enter AccessID, SecretKey, region = us-east-1a, format = json)
aws configure 


# Copy JARs from S3
aws s3 ls s3://johnwarde.net/
aws s3 cp s3://johnwarde.net/processor-0.0.1-SNAPSHOT.jar .
aws s3 cp s3://johnwarde.net/webapp-1.2.2.BUILD-SNAPSHOT.jar .
aws s3 cp s3://johnwarde.net/webappresourcestest.zip .
unzip webappresourcestest.zip -d .

cd processor
java -jar processor-0.0.1-SNAPSHOT.jar

cd webserver1
java -jar webapp-1.2.2.BUILD-SNAPSHOT.jar

cd webserver2
java -jar webapp-1.2.2.BUILD-SNAPSHOT.jar


