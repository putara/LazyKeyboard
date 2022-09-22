@echo off
cd "%~dp0"
copy app\debug\app-debug.apk .\
copy app\release\app-release.apk .\
advzip -a2 teretere.zip app-debug.apk app-release.apk teretere.yml
del app-debug.apk
del app-release.apk
