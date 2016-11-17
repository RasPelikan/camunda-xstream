package org.camunda.xstream;

import java.util.LinkedList;
import java.util.List;

import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.variable.serializer.TypedValueSerializer;
import org.camunda.xstream.bpm.engine.impl.variable.serializer.XStreamObjectSerializer;

public class ProcessEnginePlugin extends AbstractProcessEnginePlugin {

    private String encoding = "UTF-8";

    @SuppressWarnings("rawtypes")
    @Override
    public void preInit(final ProcessEngineConfigurationImpl processEngineConfiguration) {
        final List<TypedValueSerializer> customPreVariableSerializers = processEngineConfiguration.getCustomPreVariableSerializers();
        final List<TypedValueSerializer> newPreVariableSerializers = new LinkedList<TypedValueSerializer>();
        if (customPreVariableSerializers != null) {
            newPreVariableSerializers.addAll(customPreVariableSerializers);
        }
        newPreVariableSerializers.add(new XStreamObjectSerializer(encoding));
        processEngineConfiguration.setCustomPreVariableSerializers(newPreVariableSerializers);
    }

    public void setEncoding(final String encoding) {
        if (encoding != null) {
            this.encoding = encoding.trim();
        } else {
            this.encoding = null;
        }
    }

    public String getEncoding() {
        return encoding;
    }

}
