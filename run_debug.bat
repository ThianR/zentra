@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-21
set MAVEN="D:\inventiva\apache-maven-3.9.12\bin\mvn.cmd"
cd /d d:\Personales\SISTEMAS\SIFEN\zentra
%MAVEN% clean install -DskipTests
%MAVEN% spring-boot:run -pl modulo-api
