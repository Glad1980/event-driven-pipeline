FROM apache/beam_java17_sdk
ADD  /target/LogsController-fat.jar LogsController-fat.jar
COPY /src/main/resources/Config.json Config.json

ENV Config.json Config.json
ENV TZ Asia/Baghdad
ENTRYPOINT ["java" , "-jar" , "LogsController-fat.jar"]