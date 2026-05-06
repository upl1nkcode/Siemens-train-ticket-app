@echo off
REM ---------------------------------------------------------
REM Train Ticket App - SMTP Startup Script
REM ---------------------------------------------------------

echo ========================================================
echo Starting Train Ticket App with Email Notifications
echo ========================================================

REM 1. Set your Gmail Address here
set SMTP_USERNAME=your_real_email@gmail.com

REM 2. Set your 16-character Google App Password here
REM    Get one at: https://myaccount.google.com/apppasswords
set SMTP_PASSWORD=your_16_char_app_password

REM 3. Set JAVA_HOME if necessary
set JAVA_HOME=C:\Program Files\Microsoft\jdk-11.0.12.7-hotspot

echo.
echo Using SMTP Username: %SMTP_USERNAME%
echo To use a different account, edit run_with_mail.cmd
echo.

call .\mvnw.cmd spring-boot:run
