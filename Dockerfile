# build image
FROM openjdk:8u201-jdk-alpine3.9 as builder

RUN apk add imagemagick git \
    && mkdir -p /work/src \
    && mkdir -p /work/gradle \
    && mkdir -p /work/.git

COPY .git /work/.git
COPY src /work/src
COPY gradle /work/gradle
COPY build.gradle settings.gradle gradlew /work/

WORKDIR /work
RUN ./gradlew -i build

# productive image
FROM openjdk:8u201-jre-alpine3.9

ENV SHEET_BLEED_MM=0
ENV MARK_BS_INFO=FALSE

RUN apk add imagemagick
RUN mkdir /data
COPY --from=builder /work/build/libs/*.jar /opt/ImpositionService.jar

HEALTHCHECK  --interval=10s --timeout=3s CMD wget --quiet --tries=1 --spider http://localhost:4200/status || exit 1

ENTRYPOINT ["java", "-Xmx6g", "-jar","/opt/ImpositionService.jar"]

