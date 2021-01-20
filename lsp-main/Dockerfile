FROM openjdk:8
VOLUME /temp
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
ADD ./target/lsp-main-1.0.jar ./
ENTRYPOINT ["java","-jar","/lsp-main-1.0.jar"]
