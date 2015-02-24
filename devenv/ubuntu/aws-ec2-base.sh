#!/bin/bash         
# ~/mscdissertation/devenv/ubuntu/aws-ec2-base.sh
# Sets up software needed to run the reference application components on an Amazon Web Service EC2 instance 

# NOTE: cannot run this yet as some of the apt-get command ask to confirm that you want to install, are there options?

# Install AWS Command Line
sudo apt-get install awscli

# Install Java
sudo add-apt-repository ppa:webupd8team/java 
sudo apt-get update 
sudo apt-get install oracle-java7-installer 
sudo apt-get install oracle-java7-set-default

# Install RabbitMQ Server
sudo apt-get install rabbitmq-server


# Verify installed software
echo ---------------------------------------
java -version 
javac -version 
sudo rabbitmqctl list_exchanges

# Configure AWS CLI (enter AccessID, SecretKey, region = us-east-1a, format = json)
aws configure 


# Copy JARs from S3
aws s3 cp s3://johnwarde.net/processor-0.0.1-SNAPSHOT.jar .
aws s3 cp s3://johnwarde.net/webapp-1.2.2.BUILD-SNAPSHOT.jar .

