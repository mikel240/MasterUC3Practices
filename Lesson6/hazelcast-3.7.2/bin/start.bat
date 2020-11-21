@echo off

SETLOCAL

REM Descomentad la linea de abajo y poned vuestro java si no os funciona
 set JAVA_HOME="C:\Program Files\Java\jdk1.8.0_271"
if NOT DEFINED JAVA_HOME goto error
set RUN_JAVA="%JAVA_HOME%\bin\java"


REM ******* you can enable following variables by uncommenting them

REM ******* minimum heap size
REM set MIN_HEAP_SIZE=1G

REM ******* maximum heap size
REM set MAX_HEAP_SIZE=1G


if NOT "%MIN_HEAP_SIZE%" == "" (
	set JAVA_OPTS=%JAVA_OPTS% -Xms%MIN_HEAP_SIZE%
)

if NOT "%MAX_HEAP_SIZE%" == "" (
	set JAVA_OPTS=%JAVA_OPTS% -Xmx%MAX_HEAP_SIZE%
)

set CLASSPATH=%~dp0..\lib\hazelcast-all-3.7.2.jar

ECHO ########################################
ECHO # RUN_JAVA=%RUN_JAVA%
ECHO # JAVA_OPTS=%JAVA_OPTS%
ECHO # starting now...."
ECHO ########################################

start "hazelcast" "%RUN_JAVA%" %JAVA_OPTS% -cp "%CLASSPATH%" "com.hazelcast.core.server.StartServer"
goto endofscript

:error
ECHO JAVA_HOME environment variable must be set!
pause


:endofscript

ENDLOCAL
