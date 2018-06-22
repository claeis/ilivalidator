========================
ilivalidator-Instructivo
========================

Resumen
=======

Ilivalidator es un programa creado en Java que verifica un archivo de transferencia de Interlis (itf o xtf) de acuerdo con un modelo de Interlis (ili).
Existen las siguientes opciones de configuración: 
- habilitar o deshabilitar validaciones especificas.
- definir mensajes de error específicos incluyendo valores de atributos.
- definir condiciones adicionales de validación.
- implementar funciones adicionales de INTERLIS.
- establecer nombres de modelos adicionales.

Requisitos de sistema 
---------------------

El programa requiere Java 1.6.

Licencia
--------

GNU Lesser General Public License

Funcionamiento
==============

En las siguientes secciones se describe cómo funciona la aplicación según algunos casos de uso. La descripción detallada de las funciones especificas se encuentra en el capítulo "Referencia".

Ejemplos
--------

Caso 1
~~~~~~

Se valida un archivo INTERLIS 1 (itf).

``java -jar ilivalidator.jar path/to/data.itf``

Caso  2
~~~~~~~~

Se valida un archivo INTERLIS 2 con una configuración específica.

``java -jar ilivalidator.jar --config config.toml path/to/data.xtf``

El archivo config.toml se utiliza para definir qué validaciones no se deben realizar o sólo se deben notificar como advertencias cuando no se cumplen.

Caso 3
~~~~~~

Un archivo INTERLIS 2 es validado y los mensajes de error se escriben en un archivo de texto.

``java -jar ilivalidator.jar --log result.log path/to/data.xtf``

Die Fehlermeldungen inkl. Warnungen werden in die Datei result.log geschrieben.

Caso 4
~~~~~~

Un archivo INTERLIS 2 es validado y los mensajes de error se escriben como datos en un archivo xtf.

``java -jar ilivalidator.jar --xtflog result.xtf path/to/data.xtf``

Los mensajes de error incluyendo advertencias se escriben en el archivo result.xtf. El archivo result.xtf corresponde al modelo IliVErrors y se puede importar como un archivo normal de transferencia INTERLIS 2. Esto permite visualizar los errores.

Caso 5
~~~~~~

Se muestra una ventana con la que se puede seleccionar el archivo a validar y se puede iniciar la validación.

``java -jar ilivalidator.jar``

Caso 6
~~~~~~

Un archivo INTERLIS 2 es validado, usando modelos específicos. Para ello, se define la ruta a los modelos específicos.

``java -jar ilivalidator.jar --models modelname1;modelname2 --modeldir path/to/data path/to/data.csv``


Referencia
==========

Las siguientes secciones describen aspectos específicos en detalle. El funcionamiento como un todo se describe a modo de casos de uso específicos en el capítulo "Funcionamiento" (véase más arriba).

Sintaxis de llamada
-------------------

``java -jar ilivalidator.jar [Options] [file]``

Sin argumentos de línea de comandos, se muestra la interfaz, con la que se puede seleccionar el archivo a validar y para iniciar la validación.

Opciones:

+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Opción                            | Descripción                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
+===================================+========================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================+
| ``--config  filename``            | Configura la validación de datos mediante un archivo TOML.                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
|                                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--forceTypeValidation``         | Ignora la configuración de la comprobación de tipo del archivo TOML, es decir sólo se puede “suavizar” la multiplicidad.                                                                                                                                                                                                                                                                                                                                                                                                               |
|                                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--disableAreaValidation``       | Deshabilita la validación de topología de AREA.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--disableConstraintValidation`` | Deshabilita la validación de constraints.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|                                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--allObjectsAccessible``        | Con la opción, el validador supone que tiene acceso a todos los objetos. Por ejemplo, examinar la multiplicidad de relaciones en objetos externos.                                                                                                                                                                                                                                                                                                                                                                                     |
|                                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--multiplicityOff``             | Desactivar la validación de multiplicidad en general.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
|                                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--skipPolygonBuilding``         | Omite la formación de polígonos (solo ITF).                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--allowItfAreaHoles``           | Para los atributos de ÁREA ITF, permite areas vacías que no están asociados con un objeto.                                                                                                                                                                                                                                                                                                                                                                                                                                             |
|                                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--models modelnames``           | Establece nombres de modelos específicos, que se encuentran dentro de los archivos ili. Varios nombres de modelos pueden ser separados por punto y coma (;). La configuración de la ruta que conduce a los modelos se debe especificar mediante el '- modeldir path'.                                                                                                                                                                                                                                                                  |
|                                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--modeldir path``               | Archivos de archivos que contienen archivos de modelo (archivos ili). Varias rutas pueden ser separados por punto y coma. Las URL de los repositorios de modelos también son posibles. El valor predeterminado es                                                                                                                                                                                                                                                                                                                      |
|                                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                                   | %ITF\_DIR;http://models.interlis.ch/;%JAR\_DIR/ilimodels                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
|                                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                                   | %ITF\_DIR es un carácter comodín para el directorio con el archivo de transferencia.                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
|                                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                                   | %JAR\_DIR es un carácter comodín para el directorio del programa iliValidator (archivo ilivalidator.jar).                                                                                                                                                                                                                                                                                                                                                                                                                              |
|                                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                                   | El nombre del primer modelo (modelo principal) para el cual ili2db busca el archivo ili no depende de la versión de lenguaje de INTERLIS. Se busca con la siguiente secuencia para un archivo ili: primero INTERLIS 2.3, luego 1.0 y último 2.2.                                                                                                                                                                                                                                                                                       |
|                                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                                   | Cuando se resuelve un import, se tiene en cuenta la versión de lenguaje de INTERLIS del modelo principal. De esta manera se distingue del modelo Units para ili2.2 o ili2.3.                                                                                                                                                                                                                                                                                                                                                           |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--log filename``                | Escribe los mensajes log (registro) en un archivo de texto.                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--xtflog filename``             | Escribe los mensajes de log (registro) en un archivo INTERLIS 2. El archivo result.xtf corresponde al modelo IliVErrors.                                                                                                                                                                                                                                                                                                                                                                                                               |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--plugins folder``              | Directorio que contiene archivos JAR que contienen funciones adicionales. Las funciones adicionales deben implementar la interfaz Java  ``ch.interlis.iox_j.validator.InterlisFunction`` y el nombre de la clase Java debe terminar con ``IoxPlugin``.                                                                                                                                                                                                                                                                                 |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--proxy host``                  | Servidor proxy para el acceso a repositorios de modelos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--proxyPort port``              | Puerto proxy para el acceso a repositorios de modelos                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--gui``                         | Se muestra una ventana con la que se puede seleccionar el archivo a validar y se puede iniciar la validación.                                                                                                                                                                                                                                                                                                                                                                                                                          |
|                                   | La ruta de los archivos de modelo y la configuración de proxy se leen desde el archivo $HOME/.ilivalidator.                                                                                                                                                                                                                                                                                                                                                                                                                            |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--trace``                       | Genera mensajes de log (registro) adicionales (importante para análisis de errores de programa)                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--help``                        | Muestra un texto de ayuda corto.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--version``                     | Muestra la versión del programa.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
+-----------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

Configuración
-------------
La configuración de las pruebas individuales se puede definir directamente en el modelo a través de meta-atributos o en un archivo TOML independiente, por lo que no es necesario ningún cambio en el archivo ili.

Por ejemplo, para deshabilitar completamente la validación Mandatory para un atributo, se escribe en el archivo ili:

| CLASS Gebaeude =
|  !!@ ilivalid.multiplicity = off
|  Art : MANDATORY (...);

Para implementar la misma configuración sin cambio del archivo ili, se escribe en el archivo TOML:

| ["Beispiel1.Bodenbedeckung.Gebaeude.Art"]
| multiplicity="off"

Además, el archivo TOML permite configuraciones globales en la sección "PARAMETER". Por ejemplo, para deshabilitar las validaciones en general, se escribe en el archivo TOML:

| ["PARAMETER"]
| validation="off"

TOML-Archivo de Configuración
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
`Beispiel1_ES.toml`_

.. _Beispiel1_ES.toml: Beispiel1_ES.toml


TO DO (por hacer) Integrar documentación de Beispiel1.toml


Metaatributos de INTERLIS
~~~~~~~~~~~~~~~~~~~~~~~~~
La configuración de las pruebas individuales se puede configurar directamente en el modelo a través de meta-atributos. 
Los meta-atributos están ubicados inmediatamente antes del elemento del modelo correspondiente y comienzan con !!@

`Beispiel1.ili`_

.. _Beispiel1.ili: Beispiel1.ili

+------------------+--------------------------+-----------------------------------------------------------------------------------+
|Elemento de modelo| Meta-atributo            | Descripción                                                                       |
+==================+==========================+===================================================================================+
| ClassDef         | ::                       | Texto adicional para la identificación de objetos para todos los mensajes de error|
|                  |                          | relacionados con un objeto de la clase que sigue después de este meta-atributo.   |
|                  |  ilivalid.keymsg         | Siempre aparece el TID y el número de línea, si están disponibles.                |
|                  |  ilivalid.keymsg_de      | keymsg es adicional (un mensaje definido por el usuario/comprensible).            |
|                  |                          | Al exportar el valor de TID eventualmente puede no existir.                       |
|                  |                          | Para XML, el número de línea normalmente no es útil,                              |
|                  |                          | para incluir valores de atributo indicar el nombre entre los caracteres {}.       |
|                  |                          | Para mensajes de diferente idioma incluir guión bajo seguido del idioma           |
|                  |                          | Ejemplo: _DE, para Alemán                                                         |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.keymsg = "AssNr {AssNr}"                                           |
|                  |                          |   !!@ ilivalid.keymsg_de = "Assekuranz-Nr {AssNr}"                                |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| AttributeDef     | ::                       | Activar/desactivar la validación de datos o como Alerta/Error.                    |                    
|                  |                          | por ejemplo, si un valor numérico está dentro de un rango                         |
|                  |  ilivalid.type           | o si una enumeración corresponde al modelo                                        |
|                  |                          | o si las áreas son una subdivisión. etc                                           |
|                  |                          | on/warning/off                                                                    |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.type = off                                                         |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| AttributeDef     | ::                       | Activar/desactivar la validación de multiplicidad o solo como una alerta/mensaje. |                    
|                  |                          | Por ejemplo, si MANDATORY tiene o no un valor. o en el caso de BAG/LIST           |
|                  |  ilivalid.multiplicity   | si el número correspondiente de elementos estructurales está disponible           |
|                  |                          | on/warning/off                                                                    |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.multiplicity = warning                                             |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| RoleDef          | ::                       | Activar/desactivar la validación del objeto destino o solo como una Alerta/mensaje|
|                  |                          | Valida si el objeto referenciado está presente y si es de la clase esperada.      |
|                  |  ilivalid.target         |                                                                                   |
|                  |                          | on/warning/off                                                                    |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.target = warning                                                   |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| RoleDef          | ::                       | Activar/desactivar la validación de multiplicidad o como una Alerta/mensaje.      |
|                  |                          | Valida si el número de objetos requerido por el modelo esta presente              |
|                  |   ilivalid.multiplicity  | on/warning/off                                                                    |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.multiplicity = off                                                 |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| ConstraintDef    | ::                       | Activar/desactivar validación de restricciones o sólo como una Alerta/mensaje.    |
|                  |                          | Comprueba si la condición de consistencia se cumple.                              |
|                  |  ilivalid.check          | on/warning/off                                                                    |
|                  |                          |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.check = warning                                                    |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| ConstraintDef    | ::                       | Texto de mensaje si esta restricción no se cumple.                                |
|                  |                          | Se agrega la identificación del objeto y el nombre de la restricción.             |
|                  |  ilivalid.msg            | Se pueden incluir valores de los atributos usando {}                              |
|                  |  ilivalid.msg_de         |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ ilivalid.msg_de = "AndereArt muss definiert sein"                           |
|                  |                          |   !!@ ilivalid.msg_es = "Se debe definir otro tipo"                               |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| ConstraintDef    | ::                       | Nombre de la restricción (ili2.3 o ili2.4 si la restricción no tiene nombre).     |
|                  |                          | si no hay nombre, el ID interno de la restricción es utilizado                    |
|                  |  name                    |                                                                                   |
|                  |                          | ::                                                                                |
|                  |                          |                                                                                   |
|                  |                          |   !!@ name = c1023                                                                |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+


Modelo IliVErrors
-----------------
`IliVErrors_ES.ili`_

.. _IliVErrors_ES.ili: IliVErrors_ES.ili

