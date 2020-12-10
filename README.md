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
            <property name="processAnnotations">
                true
            </property>
            <property name="ignoreUnknownElements">
                true
            </property>
            <property name="useExternalClassProvider">
                true
            </property>
            <property name="allowedTypes">
                my.project.**,
                other.project.**
            </property>
        </properties>
    </plugin>
    ....
</process-engine>
```

The XStream serialization can be chosen as default serialization by defining the property "defaultSerializationFormat" as shown. If doing so the serialization is done under the hood. Otherwise
XStream serialization might be used as shown in the [Camunda Documentation](https://docs.camunda.org/manual/7.4/user-guide/process-engine/variables/#object-value-serialization).

The plugin property "encoding" is optional (default: UTF-8) and specifies the encoding used for XML serialization.

Also the plugin property "processAnnotations" is optional (default: false) which defines whether XStream annotations should be processed (see http://x-stream.github.io/annotations-tutorial.html).

Serializing values
------------------

The plugin only serializes other values than
 * primitives
 * java.lang.Number and sub classes
 * java.lang.String
 * java.lang.Boolean
 * java.lang.Character
 * java.util.Date

But you don't have to take care about this. Camunda itself chooses a proper serializer for these unsupported types.

Example of the serialized value of a variable holding a complex type:
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

Spring
------

To use this plugin in Spring or Spring boot application add the plugin as a dependency:

```
	<dependency>
		<groupId>org.camunda</groupId>
		<artifactId>camunda-xstream</artifactId>
		<version>1.0.2</version>
	</dependency>
```

Additional you have to define a configuration class


```
@Configuration
public class CamundaXStreamPluginConfiguration extends org.camunda.xstream.spring.PluginConfiguration {

}
```

Afterwards you can add the configuration properties to your application.yml file

```
camunda:
	xstream-serialization:
		encoding: UTF-8
		allowed-types:
                     - my.project.**
                     - other.project.**
```


Security configuration
----------------------

Since XStream started to introduce security features. Therefore it is necessary to register types of your project which are allowed to deserialize. You can specify those types by
 * full qualified classname (no prefix)
 * type hierarchies (prefix '<')
 * wildcard patterns (classname contains '*')
 * regular expressions (defintion surrouned by '/')
To do so, list your types using the specified prefix, infix or postfix to signal the type of specification:

```xml
        <properties>
            <property name="allowedTypes">
                my.project.MyObject,
                <my.project.MyObjectHierarchy,
                my.project.*,
                my.project.**,
                /.*\\.myproject\\.*/
            </property>
        </properties>
```

For details see [XStream Documentation](http://x-stream.github.io/security.html).

Converters
----------

It is possible to register additional converters by naming their classes using the configuration property "converters":

```xml
        <properties>
            <property name="converters">
                my.project.xstream.converters.MyCustomConverter,
                my.project.xstream.converters.AnAdditionalConverter
            </property>
        </properties>
```

For converters available see [XStream Documentation](http://x-stream.github.io/converters.html).

External Class Provider
-----------------------

In order to lookup xStream Annotations in your project, [org.atteo.classindex](https://github.com/atteo/classindex) can be used to index all annotated Classes during compilation.

First create a custom annotation processor by extending ClassIndexProcessor 

```java
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.atteo.classindex.processor.ClassIndexProcessor;

public class XStreamAnnotatedClassIndexProcessor extends ClassIndexProcessor {
    public XStreamAnnotatedClassIndexProcessor() {
        indexAnnotations(XStreamAlias.class);
        indexAnnotations(XStreamAliasType.class);
        indexAnnotations(XStreamAsAttribute.class);
    }
}
```

Then create a ClassProvider having a public, static method "getAnnotatedClasses": 

```java
public class ClassProviderImpl {
    public static Set<Class<?>> getAnnotatedClasses() {
        Set<Class<?>> ret = new HashSet<>();
        ClassIndex.getAnnotated(XStreamAlias.class).forEach(ret::add);
        ClassIndex.getAnnotated(XStreamAliasType.class).forEach(ret::add);
        ClassIndex.getAnnotated(XStreamAsAttribute.class).forEach(ret::add);
        return ret;
    }
}
```

Finally 
 - register the processor by creating the file `META-INF/services/javax.annotation.processing.Processor` in your classpath with the full class name of your processor
 - register the ClassProvider by creating the file `META-INF/services/org.camunda.xstream.ClassProvider` in your classpath with the full class name of your processor