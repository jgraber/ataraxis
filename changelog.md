# Changelog

## AtaraxiS next (x. x. x)
Aktualisierungen:
* Passwort-Manager: Fokus bleibt auf neu erstellten Passwörtern
* Passwort-Manager: Generierte Passwörter sind 16 Zeichen lang
 
Aktualisierte Hilfsbibliotheken:
* (SWT auf 4.5)
* Bouncycastle auf 1.53
* (Log4J auf 2.4.1)
* JUnit auf 4.12
* JMockit auf 1.19
* (JDom auf 2.0.6)

## AtaraxiS 1.6.0 (22. 2. 2015)
Aktualisierungen:
* Kleinere Anpassungen am GUI (Pfad zur Log-Konfiguration)
* Darstellung unter Ubuntu optimiert
 
Aktualisierte Hilfsbibliotheken:
* SWT auf 4.4
* Bouncycastle auf 1.51
* Log4J auf 2.1
* JUnit auf 4.12
* JMockit auf 1.14
* JDom auf 2.0.5



## AtaraxiS 1.5.0 (21. 04. 2014)
Aktualisierungen:
* Unterstützung von Java 8
* Minimalanforderung von AtaraxiS auf Java 7 hochgesetzt
* Unbehandelte Exception auf Entschlüsseln-Dialog behoben
* Diverse interne Umbauten (Unit-Tests verbessert)

Aktualisierte Hilfsbibliotheken:
* SWT auf 4.3
* Bouncycastle auf 1.50



## AtaraxiS 1.4.0 (05. 09. 2012)
Aktualisierungen:
* Passwort-Manager: Umbenennen eines Eintrages auf einen bestehenden Namen führte zu Absturz
* Kleinere Anpassungen am GUI
* Kleinere Übersetzungskorrekturen
* Diverse interne Umbauten (vereinfachte Namensgebung für package-Struktur)

Aktualisierte Hilfsbibliotheken:
* SWT auf 4.2 
* Bouncycastle auf 1.47
* Log4J auf 1.2.17
* Jaxen auf 1.1.4
* JUnit auf 4.10



## AtaraxiS 1.3.1 (30. 11. 2011)
Korrigierte Fehler
* Beim Verschlüsseln konnten nur noch Dateien, aber keine Ordner mehr ausgewählt werden.



## AtaraxiS 1.3.0 (29. 10. 2011)
Aktualisierungen:
* JCE Policy Dateien für Java 7.
* Überprüfen der Verschlüsselungsstärke während Start (Splash Screen)
* Fehlerhafte Darstellung (abgeschnittenen Knöpfen im Passwort-Manager) unter Linux korrigiert.
* Unterstützung für 64bit Mac OS X.
* Diverse interne Umbauten für eine modularere Oberfläche.
* Lange dauernde Aktionen (ver- und entschlüsseln, löschen) laufen in eigenen Hintergrund-Threads.

Aktualisierte Hilfsbibliotheken:
* SWT auf 3.7.1 
* Bouncycastle auf 1.46



## AtaraxiS 1.2.0 (16. 10. 2010)
Aktualisierungen:
* AtaraxiS benötigt nun Java 6.
* AtaraxiS MainGUI: GUI wird in der Mitte des 1. Monitors angezeigt. Bei 2 Monitoren nicht mehr zwischen den beiden Monitoren.
* AtaraxiS Crypter: Properties-File nicht mehr nötig.
* Passwort-Manager: Einträge können umbenannt werden.
* Passwort-Manager: Keine Abhängigkeiten mehr zwischen dem Manager und JDOM.

Aktualisierte Hilfsbibliotheken:
* SWT auf 3.6.1
* Bouncycastle auf 1.45
* JDom auf 1.1.1
* JUnit auf 4.8.2
* Log4J auf 1.2.16



## AtaraxiS 1.1.1 (1. August 2008)
Aktualisierungen:
* Sprachauswahl zeigt nun immer die gleiche, lokalisierte Liste an (Deutsch, Français, English).
* Warnung fals AtaraxiS unter dem gleichen Benutzer ein zweites mal gestartet wird.
* Kleinere Übersetzungskorrekturen

Aktualisierte Hilfsbibliotheken:
* SWT auf 3.3.2



## AtaraxiS 1.1.0 (16. Juni 2008) 
Aktualisierungen:
* Neues Verschlüsselungsverfahren mit Cipher Block Chaining Mode (CBC) und einem zufälligen Initialvektor
* AtaraxiS startet neu mit dem Passwort-Manager
* Funktion zum prüfen ob Update verfügbar ist
* Minimierung auf Tasklisten-Icon

Aktualisierte Hilfsbibliotheken:
* SWT auf 3.3.2
* Bouncycastle auf 1.39
* JDom auf 1.1
* JUnit auf 4.4
* Log4J auf 1.2.15
* Jaxen auf 1.1.1



## AtaraxiS 1.0.2 (14. August 2007) 

Aktualisierungen:
* Unterstützung von Windows Vista
* (experimentelle) Unterstützung von Mac OS X 
* drop-down Auswahl für Benutzernamen
* Mauszeiger wechselt bei längeren Operationen
* kleinere Anpassungen am GUI
* geänderte Handhabung von Bearbeitungsabbrüchen im Passwort Manager
* farbliche Integration des PW-Generators
* Strukturbereinigung der Verzeichnisse 

Aktualisierte Hilfsbibliotheken:
* SWT auf 3.3
* Bereitstellung von Java 6u2 JRE



## AtaraxiS 1.0.1 (30. März 2007) 

Aktualisierungen:
* Passwort-Verwaltung: Die Passwörter werden mit * versteckt
* Verschlüsselung: Das Änderungsdatum einer Datei bleibt erhalten
* Windows Startscripte: Beim Start wird im Hintergrund kein DOS-Fenster mehr geöffnet.
* Policy-Dateien: Neu kann auch ein Java 6 JRE gepatcht werden, bisher ging nur ein JRE in Version 5.

Aktualisierte Hilfsbibliotheken:
* Bouncycastle auf 1.36



## AtaraxiS 1.0.0 (15. Dezember 2006) 

1. Version von AtaraxiS nach Abschluss der Diplomarbeit an der HTI Biel.
