@echo off
setlocal
set PATH=%~dp0data/node/;%PATH%
npm %*
@echo on
