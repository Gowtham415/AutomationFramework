#!/bin/bash
hostname=$(cat /etc/hostname)
java -jar /Automation/repository/resources/jenkins/slave.jar -jnlpUrl http://dfjenkinspro1.texturallc.net:8080/computer/$hostname/slave-agent.jnlp