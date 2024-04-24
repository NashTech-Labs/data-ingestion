FROM gcr.io/dataflow-templates-base/java11-template-launcher-base:latest

ENV FLEX_TEMPLATE_JAVA_MAIN_CLASS=com.nashtech.Application
ENV FLEX_TEMPLATE_JAVA_CLASSPATH=/template/*
ENV JAVA_TOOL_OPTIONS="-Djava.util.logging.config.file=/resources/logback.xml"
WORKDIR .
COPY /target/data-ingestion-1.0-SNAPSHOT.jar /template/