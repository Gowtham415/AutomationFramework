#!/bin/bash
echo "Converting Windows Paths"

sudo sed -i "s/C:\\\\/\//g" /Automation/Textura/pom.xml /Automation/Textura/CPM/pom.xml /Automation/Textura/BO/pom.xml /Automation/Textura/Framework/pom.xml /Automation/Textura/Framework/src/main/config/jmx/management.properties /Automation/Textura/Framework/src/main/scripts/jenkins/archive_build_artifacts_excl.txt /Automation/Maven/conf/settings.xml /Automation/repository/maven/com/textura/textura-cpm/2.5.0/textura-cpm-2.5.0.pom /Automation/repository/maven/com/textura/textura-framework/2.5.0/textura-framework-2.5.0.pom
sudo sed -i "s/\\\\/\//g" /Automation/Textura/pom.xml /Automation/Textura/CPM/pom.xml /Automation/Textura/BO/pom.xml /Automation/Textura/Framework/pom.xml /Automation/Textura/Framework/src/main/config/jmx/management.properties /Automation/Textura/Framework/src/main/scripts/jenkins/archive_build_artifacts_excl.txt /Automation/Maven/conf/settings.xml /Automation/repository/maven/com/textura/textura-cpm/2.5.0/textura-cpm-2.5.0.pom /Automation/repository/maven/com/textura/textura-framework/2.5.0/textura-framework-2.5.0.pom


echo "Finished"