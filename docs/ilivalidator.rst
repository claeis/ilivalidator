======================
ilivalidator-Anleitung
======================

Überblick
=========

Ilivalidator ist ein in Java erstelltes Programm, das eine
Interlis-Transferdatei (itf oder xtf) gemäss einem Interlis-Modell 
(ili) überprüft.

Es bestehen u.a. folgende Konfigurationsmöglichkeiten:

- einzelne Prüfungen ein oder auszuschalten
- eigene Fehlermeldungen inkl. Attributwerte zu definieren
- zusätzliche Bedingung zu definieren
- zusätzliche INTERLIS-Funktionen zu implementieren
- Modellnamen zu setzen

Zusätzlich umfasst der IliValidator Hilfsfunktionen betrf. 
Daten (z.B. Kataloge) in einem Repository.

Log-Meldungen
-------------
Die Log-Meldungen sollen dem Benutzer zeigen, was das Programm macht.
Am Anfang erscheinen Angaben zur Programm-Version.
Falls das Programm ohne Fehler durchläuft, wird das am Ende ausgegeben.::
	
  Info: ilivalidator-1.0.0
  ...
  Info: compile models...
  ...
  Info: ...validation done

Bei einem Fehler wird das am Ende des Programms vermerkt. Der eigentliche 
Fehler wird aber in der Regel schon früher ausgegeben.::
	
  Info: ilivalidator-1.0.0
  ...
  Info: compile models...
  ...
  Error: DM01.Bodenbedeckung.BoFlaeche_Geometrie: intersection tids 48, 48
  ...
  Error: ...validation failed

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

Je nach Betriebssystem kann das Programm auch einfach durch Doppelklick mit linker Maustaste 
auf  ```ilivalidator.jar``` gestartet werden.

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

Fall 6
~~~~~~

Es wird eine INTERLIS 2-Datei validiert/geprüft. Wobei spezifische Modelle gesetzt werden.
Dazu wird der Pfad zu den spezifischen Modellen gesetzt.

``java -jar ilivalidator.jar --models modelname1;modelname2 --modeldir path/to/data path/to/data.xtf``

Fall 7
~~~~~~

Es werden alle Dateien (ITF und XTF) im gegebenen Repository geprüft/validiert.

``java -jar ilivalidator.jar --check-repo-data http://models.geo.admin.ch``

Fall 8
~~~~~~

Es werden alle Dateien (ITF und XTF) im gegebenen Verzeichnis ``folder`` analysiert
und dann ein neues ``newIlidata.xml`` mit den entsprechenden Metadaten erstellt.

``java -jar ilivalidator.jar --createIliData --ilidata newIlidata.xml --repos folder``

Fall 9
~~~~~~

Es werden alle Dateien (ITF und XTF) gemäss Dateiliste ``files.txt`` 
im Repository ``http://models.geo.admin.ch`` analysiert
und dann ein neues ``newIlidata.xml`` mit den entsprechenden Metadaten erstellt.

``java -jar ilivalidator.jar --createIliData --ilidata newIlidata.xml --repos http://models.geo.admin.ch --srcfiles files.txt``

Fall 10
~~~~~~~

Es wird die gegebene Datei ``newVersionOfData.xml`` (ITF oder XTF)
analysiert, und dann das ilidata.xml aus dem gegebenen Repository 
``http://models.geo.admin.ch`` mit einem neuen Eintrag für 
den Datensatz mit der ID ``datasetId`` aktualisiert. Die neue Version des 
ilidata.xml wird in die Datei ``updatedIlidata.xml`` geschrieben und muss
durch den Benutzer ins Repository übertragen werden.

``java -jar ilivalidator.jar --updateIliData --ilidata updatedIlidata.xml --repos http://models.geo.admin.ch --datasetId datasetId newVersionOfData.xml``


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

Der Rückgabewert ist wie folgt:

  - 0 Validierung ok, keine Fehler festgestellt
  - !0 Validierung nicht ok, Fehler festgestellt

Optionen:

+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Option                                      | Beschreibung                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
+=============================================+========================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================+
| ``--config  filename``                      | Konfiguriert die Datenprüfung mit Hilfe einer TOML-Datei.                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--forceTypeValidation``                   | Ignoriert die Konfiguration der Typprüfung aus der TOML-Datei, d.h. es kann nur die Multiplizität aufgeweicht werden.                                                                                                                                                                                                                                                                                                                                                                                                                  |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--disableAreaValidation``                 | Schaltet die AREA Topologieprüfung aus (XTF).                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--disableConstraintValidation``           | Schaltet die Constraint prüfung aus.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--allObjectsAccessible``                  | Mit der Option nimmt der Validator an, dass er Zugriff auf alle Objekte hat. D.h. es wird z.B. auch die Multiplizität von Beziehungen auf externe Objekte geprüft.                                                                                                                                                                                                                                                                                                                                                                     |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--multiplicityOff``                       | Schaltet die Prüfung der Multiplizität generell aus.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--skipPolygonBuilding``                   | Schaltet die Bildung der Polygone aus (nur ITF).                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--allowItfAreaHoles``                     | Lässt bei ITF AREA Attributen innere Ränder zu, die keinem Objekt zugeordnet sind.                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--models modelnames``                     | Setzt spezifische Modellnamen, welche sich innerhalb von ili-Dateien befinden. Mehrere Modellnamen können durch Semikolon ‚;‘ getrennt werden. Das Setzen des Pfades, der zu den Modellen führt, muss mittels '--modeldir path' angegeben werden.                                                                                                                                                                                                                                                                                      |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--modeldir path``                         | Dateipfade, die Modell-Dateien (ili-Dateien) enthalten. Mehrere Pfade können durch Semikolon ‚;‘ getrennt werden. Es sind auch URLs von Modell-Repositories möglich. Default ist                                                                                                                                                                                                                                                                                                                                                       |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                                             | %ITF\_DIR;http://models.interlis.ch/;%JAR\_DIR/ilimodels                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                                             | %ITF\_DIR ist ein Platzhalter für das Verzeichnis mit der Transferdatei.                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                                             | %JAR\_DIR ist ein Platzhalter für das Verzeichnis des ilivalidator Programms (ilivalidator.jar Datei).                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                                             | Der erste Modellname (Hauptmodell), zu dem ili2db die ili-Datei sucht, ist nicht von der INTERLIS-Sprachversion abhängig. Es wird in folgender Reihenfolge nach einer ili-Datei gesucht: zuerst INTERLIS 2.3, dann 1.0 und zuletzt 2.2.                                                                                                                                                                                                                                                                                                |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                                             | Beim Auflösen eines IMPORTs wird die INTERLIS Sprachversion des Hauptmodells berücksichtigt, so dass also z.B. das Modell Units für ili2.2 oder ili2.3 unterschieden wird.                                                                                                                                                                                                                                                                                                                                                             |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--check-repo-data repositoryUrl``         | Es werden alle Daten (ITF und XTF) im gegebenen Repository geprüft/validiert. (Alle aktuellen Daten (gemäss precursorVersion))                                                                                                                                                                                                                                                                                                                                                                                                         |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--createIliData``                         | Es werden alle Daten (ITF und XTF) im gegebenen Folder/Repository analysiert und dann ein neues ilidata.xml mit den entsprechenden Metadaten erstellt. Wenn ``repository`` ein remote Repository bezeichnet, muss mit ``--srcfiles`` die Liste der Dateien angegeben werden.                                                                                                                                                                                                                                                           |
| ``--ilidata ilidata.xml``                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| ``--repos repository``                      |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--srcfiles files.txt``                    | Liste mit relativen Dateipfaden (relativ zum gegebenen Folder/Repository). Ein Pfad pro Zeile.                                                                                                                                                                                                                                                                                                                                                                                                                                         |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--updateIliData``                         | Es wird die gegebene Datei ``newVersionOfData.xml`` (ITF oder XTF) analysiert, und dann das ilidata.xml aus dem gegebenen Repository ``repository`` mit einem neuen Eintrag für  den Datensatz mit der ID ``datasetId`` aktualisiert. Die neue Version des ilidata.xml wird in die Datei ``updatedIlidata.xml`` geschrieben und muss durch den Benutzer ins Repository übertragen werden.                                                                                                                                              |
| ``--ilidata updatedIlidata.xml``            |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| ``--repos repository``                      |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| ``--dataset datasetId``                     |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| ``newVersionOfData.xml``                    |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                                             |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--log filename``                          | Schreibt die log-Meldungen in eine Text-Datei.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--xtflog filename``                       | Schreibt die log-Meldungen in eine INTERLIS 2-Datei.  Die Datei result.xtf entspricht dem Modell IliVErrors.                                                                                                                                                                                                                                                                                                                                                                                                                           |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--plugins folder``                        | Verzeichnis mit JAR-Dateien, die Zusatzfunktionen enthalten. Die Zusatzfunktionen müssen das Java-Interface ``ch.interlis.iox_j.validator.InterlisFunction`` implementieren, und der Name der Java-Klasse muss mit ``IoxPlugin`` enden.                                                                                                                                                                                                                                                                                                |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--proxy host``                            | Proxy Server für den Zugriff auf Modell Repositories                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--proxyPort port``                        | Proxy Port für den Zugriff auf Modell Repositories                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--gui``                                   | Es erscheint eine Bildschirmmaske, mit deren Hilfe die zu validierende Datei                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
|                                             | ausgewählt und die Validierung gestartet werden kann.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
|                                             | Die Pfad der Modell-Dateien und die Proxyeinstellungen werden aus der Datei $HOME/.ilivalidator gelesen.                                                                                                                                                                                                                                                                                                                                                                                                                               |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--trace``                                 | Erzeugt zusätzliche Log-Meldungen (wichtig für Programm-Fehleranalysen)                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--help``                                  | Zeigt einen kurzen Hilfetext an.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--version``                               | Zeigt die Version des Programmes an.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
+---------------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

Konfiguration
-------------
Die einzelnen Prüfungen können direkt im Modell über Metaaatribute konfiguriert werden oder 
in einer getrennten Konfigurations-Datei, so dass keine Änderung der ili-Datei notwendig ist.

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

TOML-Globale Konfigurationen
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

+---------------------------------+-------------------------------------------+-----------------------------------------------------------------------------------+
| Konfiguration                   | Beispiel                                  | Beschreibung                                                                      |
+=================================+===========================================+===================================================================================+
| additionalModels                | ["PARAMETER"]                             | "Model1" und "Modell2" sind die Namen der Modelle mit Definitionen von            |
|                                 | additionalModels="Model1;Modell2"         | zusätzlichen Validierungen (in Form von Interlis Konsistenbedingungen).           |
|                                 |                                           |                                                                                   |
|                                 |                                           | Mehrere Zusatzmodelle werden mit einem Strichpunkt ";" getrennt.                  |
|                                 |                                           |                                                                                   |
+---------------------------------+-------------------------------------------+-----------------------------------------------------------------------------------+
| validation                      | ["PARAMETER"]                             | "off" schaltet generell alle Prüfungen aus.                                       |
|                                 | validation="off"                          | Mögliche Einstellungen sind: "off", "on". DEFAULT ist "on".                       |
|                                 |                                           |                                                                                   |
+---------------------------------+-------------------------------------------+-----------------------------------------------------------------------------------+
| areaOverlapValidation           | ["PARAMETER"]                             | "off" schaltet die AREA-Topology Prüfung aus.                                     |
|                                 | areaOverlapValidation="off"               | Mögliche Einstellungen sind: "off", "on". DEFAULT ist "on".                       |
|                                 |                                           |                                                                                   |
+---------------------------------+-------------------------------------------+-----------------------------------------------------------------------------------+
| constraintValidation            | ["PARAMETER"]                             | "off" schaltet alle Prüfungen von Konsistenzbedingungen aus.                      |
|                                 | constraintValidation="off"                | Mögliche Einstellungen sind: "off", "on". DEFAULT ist "on".                       |
|                                 |                                           |                                                                                   |
+---------------------------------+-------------------------------------------+-----------------------------------------------------------------------------------+
| defaultGeometryTypeValidation   | ["PARAMETER"]                             | Der Default-Wert für die Datentypprüfung bei Geometrie-Attributen.                |
|                                 | defaultGeometryTypeValidation="off"       | Mögliche Einstellungen sind: "warning", "off", "on". DEFAULT ist "on".            |
|                                 |                                           |                                                                                   |
+---------------------------------+-------------------------------------------+-----------------------------------------------------------------------------------+
| allowOnlyMultiplicityReduction  | ["PARAMETER"]                             | "true" ignoriert die Konfiguration der Typprüfungen aus der TOML-Datei,           |
|                                 | allowOnlyMultiplicityReduction="true"     | d.h. es kann nur die Prüfung der Multiplizität konfiguriert werden.               |
|                                 |                                           | Mögliche Einstellungen sind: "true", "false". DEFAULT ist "false".                |
|                                 |                                           |                                                                                   |
+---------------------------------+-------------------------------------------+-----------------------------------------------------------------------------------+
| allObjectsAccessible            | ["PARAMETER"]                             | "true" definiert, dass die mitgegebenen Dateien alle                              |
|                                 | allObjectsAccessible="true"               | Objekte enthalten, d.h. dass alle Referenzen (insb. mit EXTERNAL) auflösbar sind. |
|                                 |                                           | Mit false können bei Referenzen mit EXTERNAL                                      |
|                                 |                                           | nicht alle Prüfungen durchgeführt werden.                                         |
|                                 |                                           | Mögliche Einstellungen sind: "true", "false". DEFAULT ist "false".                |
|                                 |                                           |                                                                                   |
+---------------------------------+-------------------------------------------+-----------------------------------------------------------------------------------+
| multiplicity                    | ["PARAMETER"]                             | "off" schaltet die Multiplizitätsprüfung für alle Attribute und Rollen aus.       |
|                                 | multiplicity="off"                        | Mögliche Einstellungen sind: "on", "warning", "off". DEFAULT ist "on".            |
|                                 |                                           |                                                                                   |
+---------------------------------+-------------------------------------------+-----------------------------------------------------------------------------------+
| disableRounding                 | ["PARAMETER"]                             | "true" schaltet das Runden vor der Validierung von                                |
|                                 | disableRounding="true"                    | numerischen Werten aus (inkl. Koordinaten).                                       |
|                                 |                                           | Mögliche Einstellungen sind: "true", "false". DEFAULT ist "false".                |
+---------------------------------+-------------------------------------------+-----------------------------------------------------------------------------------+
| disableAreAreasMessages         | ["PARAMETER"]                             | "true" schaltet die Meldungen bei areAreas() Funktionen aus, d.h. die Funktion    |
|                                 | disableAreAreasMessages="true"            | gibt keine Meldung aus, und liefert nur via den Funktioneswert, ob die Daten die  |
|                                 |                                           | AREA Bedingung erfüllen, oder nicht.                                              |
|                                 |                                           | Bei "false" gibt die areAreas() Funktionen zusätzlich zum Funktionswert           |
|                                 |                                           | Meldungen aus, wo die Daten die                                                   |
|                                 |                                           | AREA Bedingung nicht erfüllen.                                                    |
|                                 |                                           | Betrifft: INTERLIS.areAreas(), INTERLIS_ext.areAreas2(), INTERLIS_ext.areaAreas3()|
|                                 |                                           | Mögliche Einstellungen sind: "true", "false". DEFAULT ist "false".                |
+---------------------------------+-------------------------------------------+-----------------------------------------------------------------------------------+
| verifyModelVersion              | ["PARAMETER"]                             | "true" es wird geprüft, ob die VERSIONs Angabe zum Model in der HEADERSECTION     |
|                                 | verifyModelVersion="true"                 | der XTF-Datei mit der Angabe im Modell (.ili-Datei)  übereinstimmt.               |
|                                 |                                           | Wenn die Angabe nicht übereinstimmt, erfolt eine Info-Meldung.                    |
|                                 |                                           | Mögliche Einstellungen sind: "true", "false". DEFAULT ist "false".                |
+---------------------------------+-------------------------------------------+-----------------------------------------------------------------------------------+

INTERLIS-Metaattribute
~~~~~~~~~~~~~~~~~~~~~~
Die einzelnen Prüfungen können direkt im Modell über Metaaatribute konfiguriert werden. 
Metaattribute stehen unmittelbar vor dem Modellelement das sie betreffen und beginnen mit ``!!@``.
Falls der Wert (rechts von ```=```) aus mehreren durch Leerstellen getrennten Wörtern besteht, muss er mit Gänsefüsschen eingerahmt werden (```"..."```).

`Beispiel1.ili`_

.. _Beispiel1.ili: Beispiel1.ili

+------------------+--------------------------+-----------------------------------------------------------------------------------+
| Modelelement     | Metaattribut             | Beschreibung                                                                      |
+==================+==========================+===================================================================================+
| ClassDef         | ::                       | Zusätzlicher Text für die Objektidentifikation für alle Fehlermeldung             |
|                  |                          | die sich auf ein Objekt der diesem Metaattribut folgenden Klasse beziehen.        |
|                  |  ilivalid.keymsg         | Die TID und Zeilennummer erscheint immer, falls vorhanden. keymsg ist             |
|                  |  ilivalid.keymsg_de      | zusätzlich (eine Benutzerdefinierte/verständliche Identifikation).                |
|                  |                          | Bei Export aus/Check auf DB ist TID evtl. nicht vorhanden. Bei XML                |
|                  |                          | ist die Zeilennummer in der Regel nicht hilfreich.                                |
|                  |                          | Inkl. Attributwerte in {}.                                                        |
|                  |                          | Für irgendeine Sprache bzw. fuer DE.                                              |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.keymsg = "AssNr {AssNr}"                                           |
|                  |                          |   !!@ ilivalid.keymsg_de = "Assekuranz-Nr {AssNr}"                                |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| AttributeDef     | ::                       | Datentyppruefung ein/ausschalten bzw. nur als Hinweis.                            |                    
|                  |                          | z.B. ob eine Zahlenwert innerhalb des Bereichs ist, oder ein                      |
|                  |  ilivalid.type           | Aufzählwert dem Modell entspricht oder die Flächen eine                           |
|                  |                          | Gebietseinteilung sind usw.                                                       |
|                  |                          | Werte sind on/warning/off                                                         |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.type = off                                                         |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| AttributeDef     | ::                       | Multiplizitätprüfung ein/ausschalten bzw. nur als Hinweis.                        |                    
|                  |                          | z.B. ob bei MANDATORY ein Wert vorhanden ist, oder nicht bzw.                     |
|                  |  ilivalid.multiplicity   | bei BAG/LIST ob die entsprechende Anzahl Strukturelemente vorhanden ist           |
|                  |                          | Werte sind on/warning/off                                                         |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.multiplicity = warning                                             |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| AttributeDef     | ::                       | Bei einem Referenz-Attribut oder Struktur-Attribut definieren, dass nur Objekte   |                    
|                  |                          | referenziert werden dürfen, die im Behälter mit der                               |
|                  |  ilivalid.requiredIn     | gegebenen BID vorkommen. Wenn das Metaattribut bei einem Struktur-Attribut        |
|                  |                          | benutzt wird, muss die Struktur ein Referenzattribut enthalten,                   |
|                  |                          | und die Restriktion betrifft dann die von diesem                                  |
|                  |                          | Referenz-Attribut referenzierten Objekte.                                         |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.requiredIn = bid1                                                  |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| RoleDef          | ::                       | Zielobjekt-Prüfung ein/ausschalten bzw. nur als Hinweis.                          |
|                  |                          | Prüft ob das referenzierte Objekt vorhanden ist und                               |
|                  |  ilivalid.target         | ob es von der gewünschten Klasse ist.                                             |
|                  |                          | Werte sind on/warning/off                                                         |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.target = warning                                                   |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| RoleDef          | ::                       | Multiplizitätprüfung ein/ausschalten bzw. nur als Hinweis.                        |
|                  |                          | Prüfen ob die vom Modell geforderte Anzahl Objekte referenziert wird.             |
|                  |   ilivalid.multiplicity  | Werte sind on/warning/off                                                         |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.multiplicity = off                                                 |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| RoleDef          | ::                       | Bei einer Rolle definieren, dass nur Objekte                                      |                    
|                  |                          | referenziert werden dürfen, die im Behälter mit der                               |
|                  |  ilivalid.requiredIn     | gegebenen BID vorkommen.                                                          |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.requiredIn = bid1                                                  |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| ConstraintDef    | ::                       | Constraint-Prüfung ein/ausschalten bzw. nur als Hinweis.                          |
|                  |                          | Prüfen ob die Konsistenzbedingung erfüllt ist oder nicht.                         |
|                  |  ilivalid.check          | Werte sind on/warning/off                                                         |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.check = warning                                                    |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| ConstraintDef    | ::                       | Meldungstext, falls dieses Constraint nicht erfüllt ist.                          |
|                  |                          | Wird ergänzt um Objektidentifikation und Name des Constraints.                    |
|                  |  ilivalid.msg            | inkl. Attributwerte in {}                                                         |
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
|                  |                          | Ergänzt die Fehlermeldung (ohne Name wird interne Id des Constraints verwendet)   |
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


INTERLIS 1
~~~~~~~~~~

Das Interlis 1 Modell wird intern in ein Interlis 2 Modell übersetzt. Tabellen werden zu Klassen, Attribute bleiben Attribute. 
Referenzattribute werden zu Assoziationen. Für die Namen der Assoziation und Rollen gelten folgende Regeln.

Normalerweise ist ein Rollenname der Name des Referenzattributes und der andere ist der Tabellenname, der das Referenzattribut enthält.
Und der Assoziationsname ist die Verkettung der beiden (falls dies nicht zu einem Namenskonflikt führt). Zum Beispiel folgendes 
Interlis 1 Modell:

.. code:: class

	MODEL M =
		TOPIC T =
		    TABLE A =
			    AttrA1: TEXT*20;
		    END A;
			TABLE B = 
				AttrB1: TEXT*10;
				AttrB2: -> A;
				AttrB3: -> A;
			END B;
		END T.
	END M.

``AttrB2`` wird wie folgt übersetzt:

.. code:: class

	ASSOCIATION BAttrB2 =
		B -- {0..*} B;
		AttrB2 -- {1} A;
	END BAttrB2;

Somit sind die qualifizierten Namen der Rollen (die sich aus dem Referenzattribut ergeben): ``M.T.BAttrB2.B`` und ``M.T.BAttrB2.AttrB2``.

Wenn ein Namenskonflikt besteht (wie bei ``AttrB3`` im Beispiel), wird der Name um einen Index (beginnend bei 2 pro Tabelle) verlängert. ``AttrB3`` führt also zu:

.. code:: class

   ASSOCIATION B2AttrB3 =
     B2 -- {0..*} B;
     AttrB3 -- {1} A;
   END B2AttrB3;

Somit sind die qualifizierten Namen: ``M.T.B2AttrB3.B2`` und ``M.T.B2AttrB3.AttrB3``.

Die qualifizierten Rollennamen werden auch im Log aufgeführt. z.B.

.. code:: class

 Info: validate target of role ``M.T.BAttrB2.B``...
 Info: validate multiplicity of role ``M.T.BAttrB2.B``...
