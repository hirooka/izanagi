# Izanagi

Transport Stream (MPEG2-TS) Server Application

## Prerequisites

- Linux PC
- MPEG2-TS Tuner and appropriate recording application
- Java 17

## Getting Started

```
git clone https://github.com/hirooka/izanagi
cd izanagi
```
You need create `src/main/resources/tuner.json` by referring to example tuner.json and edit `channel-configuration.json` as necessary. Then you can run application.
```
./gradlew build
java -Dspring.profiles.active=izanagi-hsqldb -jar build/libs/izanagi-1.0.0-SNAPSHOT.jar
```

You can get stream via 

```
curl http://localhost:8081/api/v1/streams/1
```

It also works with Docker. see [example Dockerfile](Dockerfile.example).
