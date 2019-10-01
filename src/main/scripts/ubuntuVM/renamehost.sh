#!/bin/bash
hostn=$(cat /etc/hostname)
echo "Current hostname: $hostn"
echo "Enter a new hostname"
read newhost

sudo sed -i "s/$hostn/$newhost/g" /etc/hosts
sudo sed -i "s/$hostn/$newhost/g" /etc/hostname

echo "new hostname is $newhost"
cat /etc/hostname
