FROM openjdk:8
VOLUME /temp
ADD ./target/lsp-sys-1.0.jar ./
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
ENTRYPOINT ["java","-jar","/lsp-sys-1.0.jar"]
