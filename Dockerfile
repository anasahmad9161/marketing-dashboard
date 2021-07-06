FROM openjdk:11
MAINTAINER Deepanshu Rathi(deepanshu.rathi@gupshup.io), Preetham Umarani (preetham.umarani@gupshup.io)
COPY target/marketing-dashboard-0.0.1-SNAPSHOT.jar marketing-dashboard-v1.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "marketing-dashboard-v1.jar"]