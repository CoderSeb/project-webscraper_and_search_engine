# Preparation steps
FROM amazoncorretto:17-alpine-full AS BUILD_IMAGE
ENV APP_HOME=/app
RUN mkdir -p $APP_HOME/src/main/java
WORKDIR $APP_HOME

# Copy all the files
COPY ./build.gradle ./gradlew ./gradlew.bat $APP_HOME
COPY gradle $APP_HOME/gradle
COPY ./src $APP_HOME/src/

# Build desirable JAR
RUN ./gradlew clean build -x test

# Create running image from build image
FROM amazoncorretto:17-alpine-full
WORKDIR /app
COPY --from=BUILD_IMAGE '/app/src' '/app/src'
COPY --from=BUILD_IMAGE '/app/build/libs/app-0.0.1-SNAPSHOT.jar' '/app/application.jar'
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/application.jar"]