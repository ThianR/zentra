@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-21
set MAVEN_PATH=D:\inventiva\apache-maven-3.9.12\bin
set PORT=9090

echo --- Iniciando Zentra SIFEN Middleware ---

rem 1. Matar el proceso en el puerto 9090 si existe
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%PORT% ^| findstr LISTENING') do (
    echo Deteniendo proceso en puerto %PORT% ^(PID: %%a^)...
    taskkill /F /PID %%a 2>nul
)
timeout /t 2 >nul


rem 2. Configurar entorno
set PATH=%MAVEN_PATH%;%JAVA_HOME%\bin;%PATH%

rem 3. Ejecutar modulo-api
echo Lanzando modulo-api...
mvn spring-boot:run -pl modulo-api -am > zentra.log 2>&1
