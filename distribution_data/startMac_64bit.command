#!/bin/sh
cd "$(dirname "$0")"
java -XstartOnFirstThread -classpath lib/swt_mac_64.jar:AtaraxiS.jar:ib/log4j-1.2-api-2.1.jar:lib/log4j-api-2.1.jar:lib/log4j-core-2.1.jar:lib/bcprov-jdk15on-151.jar:lib/jaxen-1.1.4.jar:lib/jdom-2.0.5.jar ataraxis.gui.AtaraxisStarter
