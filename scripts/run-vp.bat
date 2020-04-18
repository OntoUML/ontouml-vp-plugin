@echo off
set appDir=%2
set appDir=%appDir:"=%
set lib=\lib\*
set ormlib=\ormlib\*
set vpLibDir=%appDir%%lib%
set vpOrmlibDir=%appDir%%ormlib%

set classpath="%vpLibDir%;%vpOrmlibDir%"

cd /d "%appDir%/bin"

java -classpath %classpath% RV debug