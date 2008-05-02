@echo off

rem %~dp0 is expanded pathname of the current script under NT
set DEFAULT_BTRACE_HOME=%~dp0..

if "%BTRACE_HOME%"=="" set BTRACE_HOME=%DEFAULT_BTRACE_HOME%
set DEFAULT_BTRACE_HOME=

if not exist "%BTRACE_HOME%\build\btrace-client.jar" goto noBTraceHome

if "%JAVA_HOME%" == "" goto noJavaHome
  %JAVA_HOME%/bin/java -Dcom.sun.btrace.probeDescPath=. -Dcom.sun.btrace.dumpClasses=true -Dcom.sun.btrace.debug=false -cp %BTRACE_HOME%/build/btrace-client.jar;%BTRACE_HOME%/build/asm-3.0.jar;%JAVA_HOME%/lib/tools.jar com.sun.btrace.client.Main %*
  goto end
:noJavaHome
  echo Please set JAVA_HOME before running this script
  goto end
:noBTraceHome
  echo Please set BTRACE_HOME before running this script
:end
