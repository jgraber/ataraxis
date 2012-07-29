@Echo Off
title AtaraxiS
REM used if we are on a USB-Stick and the startup script is in the root folder
cd AtaraxiS

start javaw.exe -classpath lib\swt_64.jar;AtaraxiS.jar ataraxis.gui.AtaraxisStarter
