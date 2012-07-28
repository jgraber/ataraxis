@Echo Off
title AtaraxiS
REM used if we are on a USB-Stick and the startup script is in the root folder
cd AtaraxiS

REM check if java supports 64Bit flag -d64
java -d64 -version

REM if it had no error we use the 64Bit SWT library
IF %errorlevel% == 0 (
	start javaw.exe -classpath lib\swt_64.jar;AtaraxiS.jar ch.ethz.origo.ataraxis.gui.AtaraxisStarter
	goto :eof
)

REM otherwise we use the 32Bit SWT library
start javaw.exe -classpath lib\swt.jar;AtaraxiS.jar ch.ethz.origo.ataraxis.gui.AtaraxisStarter
