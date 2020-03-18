# build client
FROM node:current-alpine as client-builder

RUN apk add --no-cache git \
    && mkdir /work \
    && chown node:node /work

USER node

COPY --chown=node:node ["src", "/work/src"]
COPY --chown=node:node ["README.md", "/work/"]
WORKDIR /work/src/main/client
RUN ls -l

RUN npm install
RUN npx ng version
RUN npx ng build --prod=true --outputPath=/work/static --optimization=true

# build application
FROM openjdk:8u201-jdk-alpine3.9 as java-builder

RUN apk add imagemagick git \
    && mkdir -p /work/src \
    && mkdir -p /work/gradle \
    && mkdir -p /work/.git \
    && mkdir -p /work/jars

COPY .git /work/.git
COPY src /work/src
COPY gradle /work/gradle
COPY jars /work/jars
COPY build.gradle settings.gradle gradlew /work/

RUN rm -rf /work/src/main/resources/static
COPY --from=client-builder /work/static /work/src/main/resources/static

WORKDIR /work
RUN ./gradlew -i build --no-daemon

# build final image
FROM openjdk:8u201-jre-alpine3.9

ENV SHEET_BLEED_MM=0
ENV HIDE_LABELS=false
ENV BOX_MARK_TO_FINAL_TRIM_THRESHOLD=2000

RUN apk add imagemagick
RUN mkdir /data
COPY --from=java-builder /work/build/libs/*.jar /opt/ImpositionService.jar

HEALTHCHECK  --interval=10s --timeout=3s CMD wget --quiet --tries=1 --spider http://localhost:4200/status || exit 1

ENTRYPOINT ["java", "-Xmx6g", "-jar","/opt/ImpositionService.jar"]

