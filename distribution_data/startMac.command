#!/bin/sh
cd "$(dirname "$0")"
java -XstartOnFirstThread -classpath lib/swt_mac.jar:AtaraxiS.jar:lib/log4j-1.2.16.jar:lib/bcprov-jdk16-145.jar:lib/jaxen-1.1.3.jar:lib/jdom.jar  ch.ethz.origo.ataraxis.gui.AtaraxisStarter
