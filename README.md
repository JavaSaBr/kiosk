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

##  How to use
```shell script
java --enable-preview -jar build/distributions/Kiosk-shadow-0.0.1/lib/Kiosk-0.0.1-all.jar
```
## Installation
- wget https://download.bell-sw.com/java/13.0.2+9/bellsoft-jre13.0.2+9-linux-aarch64-full.deb
- dpkg -i bellsoft-jre13.0.2+9-linux-aarch64-full.deb
- apt install -f
- apt install xorg
- apt install xterm
- apt install unclutter

## --
- nano /root/.xinitrc

```
unclutter &
bash /root/kiosk.sh
```
- nano /root/kiosk.sh

```bash
#!/usr/bin/env sh

echo "The script you are running has basename $(basename "$0"), dirname $(dirna$
echo "The present working directory is $(pwd)"

FOLDER=$(dirname "$0")
CONFIG_PATH=""$FOLDER/application.yml""
APP_PATH="$FOLDER/kiosk.jar"

echo "FOLDER $FOLDER"
echo "CONFIG_PATH is $CONFIG_PATH"
echo "APP_PATH is $APP_PATH"

xrandr -s 1920x1080

java "-Dmicronaut.config.files=$CONFIG_PATH" --enable-preview -jar "$APP_PATH"
```

## Autorun
- nano /etc/rc.local
```
sh -c "export HOME=/root; startx" &
```
- chmod +x /root/kiosk.sh

## Set armbian default screen resolution
- nano /boot/armbianEnv.txt
```
setenv video-mode sunxi:1920x1080-24@60,monitor=hdmi,hpd=5000,edid=0
```

## Image repository API
You can configure any endpoint in properties file like:
```yaml
image-repository:
  method: GET
  url: https://public-crm-catalog.tipscrm.ru/tv?device_id=2
  reload-interval: 30

```
Which should return json content like:
```json
[
  {
    "name": "Image1.jpg",
    "url": "https://public-crm-catalog.tipscrm.ru/image/Image1.jpg",
    "lastModified": "2021-01-03T10:15:30+01:00"
  },
  {
    "name": "Image2.jpg",
    "url": "https://public-crm-catalog.tipscrm.ru/image/Image2.jpg",
    "lastModified": "2021-01-01T15:00:30+01:00"
  }
]
```