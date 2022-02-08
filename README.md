# Izanagi

Transport Stream (MPEG2-TS) Server Application

## Prerequisites

- Linux PC (Ubuntu 20.04)
- MPEG2-TS Tuner and appropriate driver, recording application
  - Tested: PT3, PX-W3U4, PX-S1UD V2.0, PX-BCUD
- Java 17

## Getting Started

The easiest way to run application is to use Docker. In this case you need install Docker on Ubuntu 20.04 ([Install Docker Engine on Ubuntu](https://docs.docker.com/engine/install/ubuntu/)) and create Dockerfile by referring to [example Dockerfile](Dockerfile.example).

Additionally, You need create `src/main/resources/tuner.json` by referring to example tuner.json and edit `channel-configuration.json` as necessary and then build application and build Docker image. You also need PostgreSQL image.

### Build application Docker image

```
git clone https://github.com/hirooka/izanagi
cd izanagi
vi Dockerfile
vi src/main/resources/tuner.json
vi src/main/resources/channel-configuration.json
./gradlew build
docker build . -t $USER/izanagi:1.0.0-SNAPSHOT
```

### Run Application (with PostgreSQL and Docker network)
```
docker network create nihon
docker pull postgres:14
docker run \
  --name izanagi-postgres \
  --net nihon \
  -e POSTGRES_USER=izanagi \
  -e POSTGRES_DB=izanagidb \
  -e POSTGRES_HOST_AUTH_METHOD=trust \
  -p 5432:5432 \
  -d \
  postgres
docker run \
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

You can get stream via 

```
curl http://localhost:8081/api/v1/streams/1 > 1.ts
```
