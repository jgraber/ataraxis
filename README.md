# ![AtaraxiS](http://jgraber.ch/AtaraxiS/wiki/Info_Ataraxis.png) AtaraxiS

AtaraxiS dient zum sicheren Speichern von Passwörtern und dem Verschlüsseln von Dateien (mittels AES 256).
AtaraxiS entstand als Diplomarbeit von Johnny Graber und Andreas Müdespacher an der 
FH Biel und wird seit 2007 weiterentwickelt. 

## Download
Download kompilierter Version bieten wir als [Zip]() (für Windows) und als [Tar]() (für Linux/Mac) an. Der Source Code 
ist unter der EUPL v1.1 lizenziert und auf [GitHub](https://github.com/jgraber/ataraxis) zu finden.


## Update auf neuste Version
 Um die neue Version von AtaraxiS einzusetzen, können Sie so vorgehen:
 * Benennen Sie den Ordner um, in dem die AtaraxiS Software liegt (Als Beispiel: AtaraxiS nach AtaraxiS_alt)
 * Entpacken Sie die neue Version von AtaraxiS
 * Kopieren Sie den Inhalt des Ordners ''AtaraxiS_alt\user_data\'' in den Ordner ''AtaraxiS\user_data\''.



## Ältere Versionen
* AtaraxiS v1.3.1 (30. November 2011)
[*.zip](http://download.origo.ethz.ch/ataraxis/3336/AtaraxiS_131.zip) / 
[*.tar.gz](http://download.origo.ethz.ch/ataraxis/3336/AtaraxiS_131.tar.gz)

* AtaraxiS v1.3.0 (29. Oktober 2011)  [*.zip](http://download.origo.ethz.ch/ataraxis/3336/AtaraxiS_130.zip) / [*.tar.gz](http://download.origo.ethz.ch/ataraxis/3336/AtaraxiS_130.tar.gz)

* AtaraxiS v1.2.0 (16. Oktober 2010) [*.zip](http://jgraber.ch/AtaraxiS/AtaraxiS_120.zip) / [*.tar.gz](http://jgraber.ch/AtaraxiS/AtaraxiS_120.tar.gz) 

* AtaraxiS v1.1.1 (1. August 2008) [(http://jgraber.ch/AtaraxiS/AtaraxiS_111.zip *.zip]) / [(http://jgraber.ch/AtaraxiS/AtaraxiS_111.tar.gz *.tar.gz]) 

* AtaraxiS v1.1.0 (16. Juni 2008) [*.zip](http://jgraber.ch/AtaraxiS/AtaraxiS_110.zip) / [*.tar.gz](http://jgraber.ch/AtaraxiS/AtaraxiS_110.tar.gz) 

* AtaraxiS v1.0.2 (14. August 2007) [*.zip](http://jgraber.ch/AtaraxiS/AtaraxiS_102.zip)

* AtaraxiS v1.0.1 (30. März 2007) [*.zip](http://projects.hti.bfh.ch/ataraxis/AtaraxiS_101.zip)


## Informationen für Entwickler

### Voraussetzungen
Um AtaraxiS selber weiterzuentwickeln gibt es diese Voraussetzungen
* JDK >= 6
* Ant
* Eclipse (empfohlen)


### Wie bauen?
`ant distGeneric`

### Wie testen?
`ant testsuite`

### Verwendete Komponenten
* Bouncy Castle: Copyright © 2000-2006 The Legion Of The Bouncy Castle
* JDom: Copyright © 2000-2004 Jason Hunter & Brett McLaughlin. All rights reserved.
* Jaxen: Copyright 2003-2006 The Werken Company. All Rights Reserved.
* The JMockit Testing Toolkit: Copyright © 2006-2009 Rogério Liesenfeld
* Log4J: Copyright © 2007 Apache Software Foundation is licensed under Apache 2.0
* SWT: Copyright © The Eclipse Foundation licensed under Eclipse Public License - Version 1.0