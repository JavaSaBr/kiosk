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
