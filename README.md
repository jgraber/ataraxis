# ![AtaraxiS](https://jgraber.ch/AtaraxiS/wiki/Info_Ataraxis.png) AtaraxiS [![Build Status](https://travis-ci.org/jgraber/ataraxis.svg?branch=master)](https://travis-ci.org/jgraber/ataraxis)

AtaraxiS dient zum sicheren Speichern von Passwörtern und dem Verschlüsseln von Dateien (mittels AES 256).
AtaraxiS entstand als Diplomarbeit von Johnny Graber und Andreas Müdespacher an der 
FH Biel und wird seit 2007 [weiterentwickelt](https://github.com/jgraber/ataraxis/blob/master/changelog.md). 

![Create new password](https://jgraber.ch/AtaraxiS/wiki/Ataraxis_PasswortErzeugen.png)

## Download
Download kompilierter Version bieten wir als [Zip](https://jgraber.ch/AtaraxiS/AtaraxiS_190.zip) (für Windows) 
und als [Tar](https://jgraber.ch/AtaraxiS/AtaraxiS_190.tar.gz) (für Linux/Mac) an. Der Source Code 
ist unter der EUPL v1.1 lizenziert und auf [GitHub](https://github.com/jgraber/ataraxis) zu finden.

* AtaraxiS v1.9.0 (15. Dezember 2020) [*.zip](https://jgraber.ch/AtaraxiS/AtaraxiS_190.zip) / [*.tar.gz](https://jgraber.ch/AtaraxiS/AtaraxiS_190.tar.gz)

## Update auf neuste Version
Um die neue Version von AtaraxiS einzusetzen, können Sie so vorgehen:
 * Benennen Sie den Ordner um, in dem die AtaraxiS Software liegt (als Beispiel: AtaraxiS nach AtaraxiS_alt)
 * Entpacken Sie die neue Version von AtaraxiS
 * Kopieren Sie den Inhalt des Ordners ''AtaraxiS_alt\user_data\'' in den Ordner ''AtaraxiS\user_data\''.

## Exportieren von Passwörtern
Sie können ihre Passwörter mit den PasswordExport_* Programmen in eine CSV Datei exportieren, um diese in einem anderen Passwortmanager zu verwenden. Eine kurze Anleitung finden Sie [hier](https://graberj.wordpress.com/2019/02/15/ataraxis-1-8-veroffentlicht/).


## Ältere Versionen
* AtaraxiS v1.8.0 (15. Februar 2019) [*.zip](https://jgraber.ch/AtaraxiS/AtaraxiS_180.zip) / [*.tar.gz](https://jgraber.ch/AtaraxiS/AtaraxiS_180.tar.gz)

* AtaraxiS v1.7.0 (27. Februar 2016) [*.zip](https://jgraber.ch/AtaraxiS/AtaraxiS_170.zip) / [*.tar.gz](https://jgraber.ch/AtaraxiS/AtaraxiS_170.tar.gz)

* AtaraxiS v1.6.0 (22. Februar 2015) [*.zip](https://jgraber.ch/AtaraxiS/AtaraxiS_160.zip) / [*.tar.gz](https://jgraber.ch/AtaraxiS/AtaraxiS_160.tar.gz)

* AtaraxiS v1.5.0 (21. April 2014) [*.zip](https://jgraber.ch/AtaraxiS/AtaraxiS_150.zip) / [*.tar.gz](https://jgraber.ch/AtaraxiS/AtaraxiS_150.tar.gz)

* AtaraxiS v1.4.0 (5. September 2012) [*.zip](https://jgraber.ch/AtaraxiS/AtaraxiS_140.zip) / [*.tar.gz](https://jgraber.ch/AtaraxiS/AtaraxiS_140.tar.gz)

* AtaraxiS v1.3.1 (30. November 2011) [*.zip](https://jgraber.ch/AtaraxiS/AtaraxiS_131.zip) / [*.tar.gz](https://jgraber.ch/AtaraxiS/AtaraxiS_131.tar.gz)

* AtaraxiS v1.3.0 (29. Oktober 2011)  [*.zip](https://jgraber.ch/AtaraxiS/AtaraxiS_130.zip) / [*.tar.gz](https://jgraber.ch/AtaraxiS/AtaraxiS_130.tar.gz)

* AtaraxiS v1.2.0 (16. Oktober 2010) [*.zip](https://jgraber.ch/AtaraxiS/AtaraxiS_120.zip) / [*.tar.gz](https://jgraber.ch/AtaraxiS/AtaraxiS_120.tar.gz) 

* AtaraxiS v1.1.1 (1. August 2008) [*.zip](https://jgraber.ch/AtaraxiS/AtaraxiS_111.zip) / [*.tar.gz](https://jgraber.ch/AtaraxiS/AtaraxiS_111.tar.gz) 

* AtaraxiS v1.1.0 (16. Juni 2008) [*.zip](https://jgraber.ch/AtaraxiS/AtaraxiS_110.zip) / [*.tar.gz](https://jgraber.ch/AtaraxiS/AtaraxiS_110.tar.gz) 

* AtaraxiS v1.0.2 (14. August 2007) [*.zip](https://jgraber.ch/AtaraxiS/AtaraxiS_102.zip)

* AtaraxiS v1.0.1 (30. März 2007) [*.zip](https://projects.hti.bfh.ch/ataraxis/AtaraxiS_101.zip)


## Informationen für Entwickler

### Voraussetzungen
Um AtaraxiS selber weiterzuentwickeln gibt es diese Voraussetzungen
* JDK >= 7
* Ant
* Eclipse (empfohlen)


### Wie bauen?
`ant distGeneric`

### Wie testen?
`ant testsuite`

**Achtung:** In Eclipse muss dem JUnit Test Runner als VM Argument `-javaagent:./lib/jmockit.jar` mitgegeben werden.

### Verwendete Komponenten
* [Bouncy Castle](http://www.bouncycastle.org/): Copyright (c) The Legion Of The Bouncy Castle
* [JDom](http://www.jdom.org/): Copyright (c) Jason Hunter & Brett McLaughlin. All rights reserved.
* [Jaxen](http://www.cafeconleche.org/jaxen/): Copyright (c) The Werken Company. All Rights Reserved.
* [The JMockit Testing Toolkit](http://jmockit.github.io/): Copyright (c) Rogério Liesenfeld
* [Log4J](http://logging.apache.org/log4j/): Copyright (c) Apache Software Foundation is licensed under Apache 2.0
* [SWT](http://eclipse.org/swt/): Copyright (c) The Eclipse Foundation licensed under Eclipse Public License - Version 1.0
