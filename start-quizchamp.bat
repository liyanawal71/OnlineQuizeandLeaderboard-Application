@echo off
title QuizChamp
cd /d "%~dp0"

echo Starting QuizChamp...
call mvnw.cmd spring-boot:run

pause