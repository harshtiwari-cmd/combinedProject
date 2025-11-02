FROM artifacts-ci-cd.dukhanbank.com:5050/base/openjdk-17:v1
WORKDIR /app
USER 1001
COPY target/*.jar common-service.jar
ENTRYPOINT ["java","-jar","/app/common-service.jar"]