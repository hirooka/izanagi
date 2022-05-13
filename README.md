# Izanagi

Transport Stream (MPEG2-TS) Server Application

## Prerequisites

### Linux PC

- Ubuntu 22.04
- MPEG2-TS Tuner and appropriate driver, recording application
  - Tested: PT3, PX-W3U4, PX-S1UD V2.0, PX-BCUD
- Java 17

### Raspberry Pi 4 Model B 8GB

- Raspberry Pi OS (64bit)
- MPEG2-TS Tuner and appropriate driver, recording application
  - Tested: PX-W3U4, PX-S1UD V2.0, PX-BCUD
- Java 17

## Getting Started

Run application on Docker

### Linux PC

#### Install Docker

[Install Docker Engine on Ubuntu](https://docs.docker.com/engine/install/ubuntu/)

#### Build application

```
git clone https://github.com/hirooka/izanagi
cd izanagi
touch Dockerfile
# cp ~/IdeaProjects/izanagi/Dockerfile .
touch ./src/main/resources/tuner.json
# cp ~/IdeaProjects/izanagi/src/main/resources/tuner.json ./src/main/resources/
# vi src/main/resources/channel-configuration.json
docker build . -t $USER/izanagi:1.0.0-SNAPSHOT
```

#### Run application

```
docker network create nihon
docker pull postgres:14
docker run \
  --rm \
  --name izanagi-postgres \
  --net nihon \
  -e POSTGRES_USER=izanagi \
  -e POSTGRES_DB=izanagidb \
  -e POSTGRES_HOST_AUTH_METHOD=trust \
  -p 5432:5432 \
  -d \
  postgres
docker run \
  --rm \
  --name izanagi \
  --net nihon \
  --privileged \
  --volume /dev/:/dev/ \
  --volume /var/run/pcscd/pcscd.comm:/var/run/pcscd/pcscd.comm \
  --volume /etc/localtime:/etc/localtime:ro \
  -p 8081:8081 \
  -d \
  -it $USER/izanagi:1.0.0-SNAPSHOT
```

#### Clean Application

```
docker stop izanagi
docker stop izanagi-postgres
docker network rm nihon
```

### Raspberry Pi 4 Model B 8GB

#### Install Docker

[Install Docker Engine on Debian](https://docs.docker.com/engine/install/debian/)

```
git clone https://github.com/hirooka/izanagi
cd izanagi
touch Dockerfile
# scp dev:/home/$USER/IdeaProjects/izanagi/Dockerfile .
touch ./src/main/resources/tuner.json
# scp dev:/home/$USER/IdeaProjects/izanagi/src/main/resources/tuner-rpi-example.json ./src/main/resources/tuner.json
# vi ./src/main/resources/tuner.json
# vi src/main/resources/channel-configuration.json
docker build . -t $USER/izanagi:1.0.0-SNAPSHOT --build-arg ARCH=arm64
```

#### Run application

```
docker network create nihon
docker pull postgres:14
docker run \
  --rm \
  --name izanagi-postgres \
  --net nihon \
  -e POSTGRES_USER=izanagi \
  -e POSTGRES_DB=izanagidb \
  -e POSTGRES_HOST_AUTH_METHOD=trust \
  -p 5432:5432 \
  -d \
  postgres
docker run \
  --rm \
  --name izanagi \
  --net nihon \
  --privileged \
  --volume /dev/:/dev/ \
  --volume /var/run/pcscd/pcscd.comm:/var/run/pcscd/pcscd.comm \
  --volume /etc/localtime:/etc/localtime:ro \
  -p 8081:8081 \
  -d \
  -it $USER/izanagi:1.0.0-SNAPSHOT
```

#### Clean Application

```
docker stop izanagi
docker stop izanagi-postgres
docker network rm nihon
```

## Usage

### EPG
```
curl http://localhost:8081/api/v1/programs/1
```
### Stream
```
curl http://localhost:8081/api/v1/streams/1 > 1.ts
```
