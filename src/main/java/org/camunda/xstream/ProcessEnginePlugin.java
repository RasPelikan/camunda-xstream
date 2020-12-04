package org.camunda.xstream;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.variable.serializer.TypedValueSerializer;
import org.camunda.xstream.bpm.engine.impl.variable.serializer.XStreamObjectSerializer;

public class ProcessEnginePlugin extends AbstractProcessEnginePlugin {

    private String encoding = "UTF-8";

    private boolean processAnnotations = false;

    private boolean ignoreUnknownElements = false;

    private List<String> converters = new LinkedList<String>();

    private List<String> allowedTypes = new LinkedList<String>();

    @SuppressWarnings("rawtypes")
    @Override
    public void preInit(final ProcessEngineConfigurationImpl processEngineConfiguration) {
        if ((allowedTypes == null) || allowedTypes.isEmpty()
        		|| ((allowedTypes.size() == 1) && allowedTypes.get(0).isEmpty())) {
        	throw new RuntimeException("No or empty 'allowedTypes' parameter set!\n"
        			+ "Since XStream 1.4.10 (used by camunda-xstream) security rules "
        			+ "have to be declared, by explicitly naming classes allowed to be deserialized.\n"
        			+ "Have a look at https://github.com/RasPelikan/camunda-xstream/blob/master/README.md "
        			+ "how to do so.");
        }

        final List<TypedValueSerializer> customPreVariableSerializers = processEngineConfiguration.getCustomPreVariableSerializers();
        final List<TypedValueSerializer> newPreVariableSerializers = new LinkedList<TypedValueSerializer>();
        if (customPreVariableSerializers != null) {
            newPreVariableSerializers.addAll(customPreVariableSerializers);
        }
        newPreVariableSerializers.add(
        		new XStreamObjectSerializer(encoding, converters, allowedTypes, processAnnotations, ignoreUnknownElements));
        processEngineConfiguration.setCustomPreVariableSerializers(newPreVariableSerializers);

        System.out.println(((XStreamObjectSerializer) newPreVariableSerializers.get(newPreVariableSerializers.size()-1)).log());
    }

    public void setEncoding(final String encoding) {
        if (encoding != null) {
            this.encoding = encoding.trim();
        } else {
            this.encoding = null;
        }
    }

    // Used by wildfly plugin configuration
    public void setAllowedTypes(String allowedTypes) {
    	if (allowedTypes == null) {
    		this.allowedTypes = null;
    		return;
    	}
    	final String[] allowedTypesArray = allowedTypes.split(",");
        this.allowedTypes.addAll(Arrays.asList(allowedTypesArray));
    }

    // Used by wildfly plugin configuration
    public void setConverters(String converters) {
    	if (converters == null) {
    		this.converters = null;
    		return;
    	}
    	final String[] convertersArray = converters.split(",");
        this.converters.addAll(Arrays.asList(convertersArray));
    }

    public void setProcessAnnotations(final String processAnnotations) {
        if (processAnnotations != null) {
            this.processAnnotations = Boolean.parseBoolean(processAnnotations);
        } else {
            this.processAnnotations = false;
        }
    }

    public void setIgnoreUnknownElements(final String ignoreUnknownElements) {
        if (ignoreUnknownElements != null) {
            this.ignoreUnknownElements = Boolean.parseBoolean(ignoreUnknownElements);
        } else {
            this.ignoreUnknownElements = false;
        }
    }

}
