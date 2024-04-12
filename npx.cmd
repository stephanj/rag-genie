@echo off
setlocal
set PATH=%~dp0data/node/;%PATH%
npx %*
@echo on
