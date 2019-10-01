#!/bin/bash
echo "Enter name of Automation project: "
read project

echo "Checking out $project"
sudo mkdir /Automation
sudo chmod -R ugo+rw /Automation
sudo chown -R preconditonator3 /Automation
sudo svn checkout https://dev1.texturallc.net/svn/qa/Automation/externals/projects/trunk/$project/ /Automation;
cp /Automation/Textura/Framework/src/main/scripts/selenium/hub/linuxHubStart.sh /home/preconditionator3/linuxHubStart.sh
cp /Automation/Textura/Framework/src/main/scripts/selenium/node/linuxNodeStart.sh /home/preconditionator3/linuxNodeStart.sh
cp /Automation/Textura/Framework/src/main/scripts/ubuntuVM/gridSetup.sh /home/preconditionator3/gridSetup.sh
cp /Automation/Textura/Framework/src/main/scripts/jenkins/linuxJenkinsClient.sh /home/preconditionator3/linuxJenkinsClient.sh
cp /Automation/Textura/Framework/src/main/scripts/ubuntuVM/convertWindowsPaths.sh /home/preconditionator3/convertWindowsPaths.sh

chmod +x linuxHubStart.sh
chmod +x linuxNodeStart.sh
chmod +x linuxJenkinsClient.sh
chmod +x convertWindowsPaths.sh

chmod +x gridSetup.sh

echo "Automation project setup complete"

hostn=$(cat /etc/hostname)
echo "Current hostname: $hostn"
echo "Enter a new hostname: "
read newhost

sudo sed -i "s/$hostn/$newhost/g" /etc/hosts
sudo sed -i "s/$hostn/$newhost/g" /etc/hostname
sudo sed -i "s/<YOURCOMPUTER>/$newhost/g" /home/preconditionator3/linuxNodeStart.sh

echo "new hostname is $newhost"
sudo reboot




