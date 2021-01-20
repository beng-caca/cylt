FROM openjdk:8
VOLUME /temp
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
ADD ./target/cylt-main-1.0.jar ./
ENTRYPOINT ["java","-jar","/cylt-main-1.0.jar"]
