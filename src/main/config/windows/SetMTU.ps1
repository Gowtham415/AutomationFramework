# This script sets MTU on Windows 2012 R2 server
netsh interface ipv4 show interfaces
netsh interface ipv4 set subinterface "12" mtu=1500 store=persistent
netsh interface ipv4 show interfaces