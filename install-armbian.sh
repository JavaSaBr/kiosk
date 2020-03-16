#!/usr/bin/env sh

# install dependencies
wget https://download.bell-sw.com/java/13.0.2+9/bellsoft-jre13.0.2+9-linux-aarch64-full.deb
dpkg -i bellsoft-jre13.0.2+9-linux-aarch64-full.deb

apt install -y -f
apt install -y xorg
apt install -y xterm
apt install -y unclutter

# configuring X server
touch /root/.xinitrc
echo 'unclutter &' >> /root/.xinitrc
echo 'bash kiosk.sh' >> /root/.xinitrc

# autostartup
touch /etc/rc.local
echo 'sh -c "export HOME=/root; startx" &' >> /etc/rc.local

chmod +x /root/kiosk.sh

echo 'setenv video-mode sunxi:1920x1080-24@60,monitor=hdmi,hpd=5000,edid=0' >> /boot/armbianEnv.txt