# camunda-xstream
XStream based serialization and deserialization of Camunda process engine variables and within Spin.

Hint: The implementation does not support Spin yet.

Plugin-Configuration
--------------------

```xml
<process-engine name="default" default="true">
    ...
    <properties>
        <property name="defaultSerializationFormat">
            application/xstream
        </property>
    </properties>
    ...
    <plugin>
        <class>org.camunda.xstream.ProcessEnginePlugin</class>
        <properties>
            <property name="encoding">
                UTF-8
            </property>
        </properties>
    </plugin>
    ....
</process-engine>
```

The XStream serialization can be choosen as default serialization by defining the property "defaultSerializationFormat" as shown. If doing so the serialization is done under the hood. Otherwise
XStream serialization might be used as shown in the [Camunda Documentation](https://docs.camunda.org/manual/7.4/user-guide/process-engine/variables/#object-value-serialization).

The plugin propperty "encoding" is optional (default: UTF-8) and specifies the encoding used for XML serialization.

Examples of serialized values
-----------------------------

The plugin only serializes other values than
 * java.lang.Number and sub classes
 * java.lang.String
 * java.lang.Boolean
 * java.lang.Character

```xml
<com.best.Example id="1">
<name>Best</name>
</com.best.Example>
```

Hint: Since the encoding is given by configuration the xml definition
```xml
<?xml version="1.0" encoding="UTF-8"?>
```
is suppressed to save space.

Wildfly module
--------------

To use XStream serialization in Wildfly/JBoss you have to add the contents of the ZIP file "camunda-xstream-XXX-jboss-module.zip" to the directory "modules" within the Wildfly's installation directory. Additionally the file "modules/org/camunda/bpm/wildfly/camunda-wildfly-subsystem/main/module.xml" has to be extended by the line
```xml
<module name="org.camunda.xstream" />
```
within the dependency tag.