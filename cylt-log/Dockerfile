FROM openjdk:8
VOLUME /temp
ADD ./target/cylt-log-1.0.jar ./
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
ENTRYPOINT ["java","-jar","/cylt-log-1.0.jar"]
