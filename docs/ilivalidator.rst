======================
ilivalidator-Anleitung
======================

Überblick
=========

Ilivalidator ist ein in Java erstelltes Programm, das eine
Interlis-Transferdatei (itf oder xtf) gemäss einem Interlis-Modell entsprechend
(ili) überprüft.

Es bestehen u.a. folgende Konfigurationsmöglichkeiten:
- einzelne Prüfungen ein oder auszuschalten
- eigene Fehlermeldungen inkl. Attributwerte zu definieren
- zusätzliche Bedingung zu definieren
- zusätzliche INTERLIS-Funktionen zu implementieren

Laufzeitanforderungen
---------------------

Das Programm setzt Java 1.6 voraus.

Lizenz
------

GNU Lesser General Public License

Funktionsweise
==============

In den folgenden Abschnitten wird die Funktionsweise anhand einzelner
Anwendungsfälle beispielhaft beschrieben. Die detaillierte Beschreibung
einzelner Funktionen ist im Kapitel „Referenz“ zu finden.

Beispiele
---------

Fall 1
~~~~~~

Es wird eine INTERLIS 1-Datei validiert/geprüft.

``java -jar ilivalidator.jar path/to/data.itf``

Fall 2
~~~~~~

Es wird eine INTERLIS 2-Datei mit einer spezifischen 
Konfiguration validiert/geprüft.

``java -jar ilivalidator.jar --config config.toml path/to/data.xtf``

In der Datei config.toml wird definiert, welche Prüfungen gar nicht durchzuführen oder 
bei Nichterfüllen nur als Warnung zu melden sind.

Fall 3
~~~~~~

Es wird eine INTERLIS 2-Datei validiert/geprüft, wobei die Fehlermeldungen 
in eine Text-Datei geschrieben werden.

``java -jar ilivalidator.jar --log result.log path/to/data.xtf``

Die Fehlermeldungen inkl. Warnungen werden in die Datei result.log geschrieben.

Fall 4
~~~~~~

Es wird eine INTERLIS 2-Datei validiert/geprüft, wobei die Fehlermeldungen 
als Daten in eine Xtf-Datei geschrieben werden.

``java -jar ilivalidator.jar --xtflog result.xtf path/to/data.xtf``

Die Fehlermeldungen inkl. Warnungen werden in die Datei result.xtf geschrieben.
Die Datei result.xtf entspricht dem Modell IliVErrors und kann als normale 
INTERLIS 2-Transferdatei importiert werden. Dadurch können die 
Fehler visualisiert werden.

Fall 5
~~~~~~

Es erscheint eine Bildschirmmaske, mit deren Hilfe die zu validierende Datei 
ausgewählt und die Validierung gestartet werden kann.

``java -jar ilivalidator.jar``


Referenz
========

In den folgenden Abschnitten werden einzelne Aspekte detailliert, aber
isoliert, beschrieben. Die Funktionsweise als Ganzes wird anhand
einzelner Anwendungsfälle beispielhaft im Kapitel „Funktionsweise“
(weiter oben) beschrieben.

Aufruf-Syntax
-------------

``java -jar ilivalidator.jar [Options] [file]``

Ohne Kommandozeilenargumente erscheint die Bildschirmmaske, mit deren Hilfe die zu validierende Datei 
ausgewählt und die Validierung gestartet werden kann.

Optionen:

+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Option                        | Beschreibung                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
+===============================+============================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================+
| ``--config  filename``        | Konfiguriert die Datenprüfung mit Hilfe einer TOML-Datei.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --modeldir path               | Dateipfade, die Modell-Dateien (ili-Dateien) enthalten. Mehrere Pfade können durch Semikolon ‚;‘ getrennt werden. Es sind auch URLs von Modell-Repositories möglich. Default ist                                                                                                                                                                                                                                                                                                                                                           |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | %XTF\_DIR;http://models.interlis.ch/;%JAR\_DIR                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | %XTF\_DIR ist ein Platzhalter für das Verzeichnis mit der Transferdatei.                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | %JAR\_DIR ist ein Platzhalter für das Verzeichnis des ilivalidator Programms (ilivalidator.jar Datei).                                                                                                                                                                                                                                                                                                                                                                                                                                     |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Der erste Modellname (Hauptmodell), zu dem ili2db die ili-Datei sucht, ist nicht von der INTERLIS-Sprachversion abhängig. Es wird in folgender Reihenfolge nach einer ili-Datei gesucht: zuerst INTERLIS 2.3, dann 1.0 und zuletzt 2.2.                                                                                                                                                                                                                                                                                                    |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Beim Auflösen eines IMPORTs wird die INTERLIS Sprachversion des Hauptmodells berücksichtigt, so dass also z.B. das Modell Units für ili2.2 oder ili2.3 unterschieden wird.                                                                                                                                                                                                                                                                                                                                                                 |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --log filename                | Schreibt die log-Meldungen in eine Text-Datei.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --xtflog filename             | Schreibt die log-Meldungen in eine INTERLIS 2-Datei.  Die Datei result.xtf entspricht dem Modell IliVErrors.                                                                                                                                                                                                                                                                                                                                                                                                                               |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--plugins folder``          | Verzeichnis mit JAR-Dateien, die Zusatzfunktionen enthalten. Die Zusatzfunktionen müssen das Java-Interface ``ch.interlis.iox_j.validator.InterlisFunction`` implementieren, und der Name der Java-Klasse muss mit ``IoxPlugin`` enden.                                                                                                                                                                                                                                                                                                    |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--proxy host``              | Proxy Server für den Zugriff auf Modell Repositories                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--proxyPort port``          | Proxy Port für den Zugriff auf Modell Repositories                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--gui``                     | Es erscheint eine Bildschirmmaske, mit deren Hilfe die zu validierende Datei                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
|                               | ausgewählt und die Validierung gestartet werden kann.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
|                               | Die Pfad der Modell-Dateien und die Proxyeinstellungen werden aus der Datei $HOME/.ilivalidator gelesen.                                                                                                                                                                                                                                                                                                                                                                                                                                   |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --trace                       | Erzeugt zusätzliche Log-Meldungen (wichtig für Programm-Fehleranalysen)                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --help                        | Zeigt einen kurzen Hilfetext an.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --version                     | Zeigt die Version des Programmes an.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

Konfiguration
-------------
Die Konfiguration der einzelnen Prüfungen kann direkt im Modell über Metaaatribute konfiguriert werden oder 
in einer getrennten TOML Datei, so dass keine Änderung der ili-Datei notwendig ist.

Um z.B. bei einem Attribut den Mandatory Check ganz auszuschalten, schreibt man in der ili-Datei:

| CLASS Gebaeude =
|  !!@ ilivalid.multiplicity = off
|  Art : MANDATORY (...);

Um dieselbe Konfiguration ohne Änderung der ili-Datei vorzunehmen, 
schreibt man in der TOML-Datei:

| ["Beispiel1.Bodenbedeckung.Gebaeude.Art"]
| multiplicity="off"

Zusätzlich erlaubt die TOML Datei pauschale Konfigurationen im Abschnitt "PARAMETER". Um z.B. generell 
alle Prüfungen auszuschalten schreibt man in die TOML-Datei:

| ["PARAMETER"]
| validation="off"

TOML-Konfigurationsdatei
~~~~~~~~~~~~~~~~~~~~~~~~
`Beispiel1.toml`_

.. _Beispiel1.toml: Beispiel1.toml


TODO Doku aus Beispiel1.toml übernehmen


INTERLIS-Metaattribute
~~~~~~~~~~~~~~~~~~~~~~
Die Konfiguration der einzelnen Prüfungen kann direkt im Modell über Metaaatribute konfiguriert werden. 
Metaattribute stehen unmittelbar vor dem Modellelement das sie betreffen und beginnen mit ``!!@``.

`Beispiel1.ili`_

.. _Beispiel1.ili: Beispiel1.ili

+------------------+--------------------------+-----------------------------------------------------------------------------------+
| Modelelement     | Metaattribut             | Beschreibung                                                                      |
+==================+==========================+===================================================================================+
| ClassDef         | ::                       | Zusaetzlicher Text fuer die Objektidentifikation fuer alle Fehlermeldung          |
|                  |                          | die sich auf ein Objekt der diesem Metaattribut folgenden Klasse beziehen.        |
|                  |  ilivalid.keymsg         | Die TID und Zeilennummer erscheint immer, falls vorhanden. keymsg ist             |
|                  |  ilivalid.keymsg_de      | zusaetzlich (eine Benutzerdefinierte/verständliche Identifikation).               |
|                  |                          | Bei Export aus/Check auf DB ist TID evtl. nicht vorhanden. Bei XML                |
|                  |                          | ist Zeilennummer in der Regel nicht hilfreich.                                    |
|                  |                          | Inkl. Attributwerte in ${}.                                                       |
|                  |                          | Fuer irgendeine Sprache bzw. fuer DE.                                             |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.keymsg = "AssNr ${AssNr}"                                          |
|                  |                          |   !!@ ilivalid.keymsg_de = "Assekuranz-Nr ${AssNr}"                               |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| AttributeDef     | ::                       | Datentyppruefung ein/ausschalten bzw. nur als Hinweis.                            |                    
|                  |                          | z.B. ob eine Zahlenwert innerhalb des Bereichs ist, oder ein                      |
|                  |  ilivalid.type           | Aufzaehlwert dem Modell entspricht oder die Flaechen eine                         |
|                  |                          | Gebietseinteilung sind usw.                                                       |
|                  |                          | on/warning/off                                                                    |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.type = off                                                         |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| AttributeDef     | ::                       | Multiplizitaetpruefung ein/ausschalten bzw. nur als Hinweis.                      |                    
|                  |                          | z.B. ob bei MANDATORY ein Wert vorhanden ist, oder nicht bzw.                     |
|                  |  ilivalid.multiplicity   | bei BAG/LIST ob die entsprechende Anzahl Strukturelemente vorhanden ist           |
|                  |                          | on/warning/off                                                                    |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.multiplicity = warning                                             |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| RoleDef          | ::                       | Zielobjekt-Pruefung ein/ausschalten bzw. nur als Hinweis.                         |
|                  |                          | Prueft ob das referenzierte Objekt vorhanden ist und                              |
|                  |  ilivalid.target         | ob es von der gewuenschten Klasse ist.                                            |
|                  |                          | on/warning/off                                                                    |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.target = warning                                                   |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| RoleDef          | ::                       | Multiplizitaetpruefung ein/ausschalten bzw. nur als Hinweis.                      |
|                  |                          | Pruefen ob die vom Modell geforderte Anzahl Objekte referenziert wird             |
|                  |   ilivalid.multiplicity  | on/warning/off                                                                    |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.multiplicity = off                                                 |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| ConstraintDef    | ::                       | Constraint-Pruefung ein/ausschalten bzw. nur als Hinweis.                         |
|                  |                          | Pruefen ob die Konsistenzbedingung erfuellt ist oder nicht.                       |
|                  |  ilivalid.check          | on/warning/off                                                                    |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.check = warning                                                    |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| ConstraintDef    | ::                       | Meldungstext, falls dieses Constraint nicht erfuellt ist.                         |
|                  |                          | Wird ergaenzt um Objektidentifikation und Name des Constraints.                   |
|                  |  ilivalid.msg            | inkl. Attributwerte in ${}                                                        |
|                  |  ilivalid.msg_de         |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.msg_de = "AndereArt muss definiert sein"                           |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| ConstraintDef    | ::                       | Name des Constraints (ili2.3 oder bei ili2.4 falls constraint kein name hat)      |
|                  |                          | Ergaenzt Fehlermeldung (ohne Name wird interne Id des Constraints verwendet)      |
|                  |  name                    |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ name = c1023                                                                |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+


Modell IliVErrors
-----------------
`IliVErrors.ili`_

.. _IliVErrors.ili: IliVErrors.ili

