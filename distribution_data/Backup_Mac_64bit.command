#!/bin/sh
cd "$(dirname "$0")"
java -XstartOnFirstThread -classpath lib/swt_mac_64.jar:AtaraxiS.jar:lib/log4j-1.2-api-2.13.3.jar:lib/log4j-api-2.13.3.jar:lib/log4j-core-2.13.3.jar:lib/bcprov-jdk15on-167.jar:lib/jaxen-1.2.0.jar:lib/jdom-2.0.6.jar -Dlog4j.configurationFile=application_data/config/log4j2.xml ataraxis.misc.AtaraxisBackup
