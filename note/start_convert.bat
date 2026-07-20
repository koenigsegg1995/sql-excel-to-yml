@echo off
chcp 65001 >nul
cd /d %~dp0
java -jar sql-excel-to-yml.jar
pause