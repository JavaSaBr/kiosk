# Kiosk

## License Apache 2.0

## Requires
#### 1. Java 13+

## How to build for Desktop
```shell script
./gradlew build -PtargetArch=x64
or
./gradlew build -PtargetArch=x86
```
## How to build for Arm
```shell script
./gradlew build -PtargetArch=arm
```

## How to use
```shell script
java --enable-preview -jar build/distributions/Kiosk-shadow-0.0.1/lib/Kiosk-0.0.1-all.jar
```
# Installation
- wget https://download.bell-sw.com/java/13.0.2+9/bellsoft-jre13.0.2+9-linux-aarch64-full.deb
- dpkg -i bellsoft-jre13.0.2+9-linux-aarch64-full.deb
- apt install -f
- apt install xorg
- apt install xterm
- apt install unclutter

# --
- nano /root/.xinitrc
- unclutter &
- bash /root/kiosk.sh

# Autorun
- nano /etc/rc.local
- sh -c "export HOME=/root; startx" &
- chmod +x /root/kiosk.sh

# Set armbian screen resolution
- nano /boot/armbianEnv.txt
- setenv video-mode sunxi:1920x1080-24@60,monitor=hdmi,hpd=5000,edid=0
