#!/bin/bash
java -classpath lib/swt_linux.jar:AtaraxiS.jar:lib/log4j-1.2-api-2.4.1.jar:lib/log4j-api-2.4.1.jar:lib/log4j-core-2.4.1.jar -Dlog4j.configurationFile=application_data/config/log4j2.xml ataraxis.misc.AtaraxisPasswordExport
