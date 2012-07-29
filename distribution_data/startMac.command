#!/bin/sh
cd "$(dirname "$0")"
java -XstartOnFirstThread -classpath lib/swt_mac.jar:AtaraxiS.jar:lib/log4j-1.2.17.jar:lib/bcprov-jdk15on-147.jar:lib/jaxen-1.1.4.jar:lib/jdom.jar  ataraxis.gui.AtaraxisStarter
