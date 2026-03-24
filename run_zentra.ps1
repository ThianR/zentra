# Script para arrancar Zentra SIFEN Middleware
# Uso: .\run_zentra.ps1

$PORT = 9090
$JAVA_PATH = "C:\Program Files\Java\jdk-21"
$MAVEN_PATH = "D:\inventiva\apache-maven-3.9.12\bin"

Write-Host "--- Iniciando Zentra SIFEN Middleware ---" -ForegroundColor Cyan

# 1. Buscar y detener procesos en el puerto 9090
$processId = Get-NetTCPConnection -LocalPort $PORT -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -First 1

if ($processId) {
    Write-Host "Detectado proceso previo en el puerto $PORT (PID: $processId). Deteniendo..." -ForegroundColor Yellow
    Stop-Process -Id $processId -Force
    Start-Sleep -Seconds 2
} else {
    Write-Host "El puerto $PORT está libre." -ForegroundColor Green
}

# 2. Configurar el entorno
$env:JAVA_HOME = $JAVA_PATH
$env:PATH = "$($env:JAVA_HOME)\bin;$MAVEN_PATH;$($env:PATH)"

Write-Host "Java Version:" -ForegroundColor Green
& java -version

# 3. Lanzar la aplicación
Write-Host "Reconstruyendo TODO el proyecto (Root Clean Install)..." -ForegroundColor Cyan
& "$MAVEN_PATH\mvn.cmd" clean install -DskipTests

Write-Host "Lanzando modulo-api..." -ForegroundColor Magenta
& "$MAVEN_PATH\mvn.cmd" spring-boot:run -pl modulo-api
