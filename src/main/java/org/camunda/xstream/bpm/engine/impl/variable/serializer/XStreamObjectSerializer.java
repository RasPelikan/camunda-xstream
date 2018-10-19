package org.camunda.xstream.bpm.engine.impl.variable.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Date;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.camunda.bpm.engine.impl.util.ReflectUtil;
import org.camunda.bpm.engine.impl.variable.serializer.AbstractObjectValueSerializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.StaxWriter;

public class XStreamObjectSerializer extends AbstractObjectValueSerializer {

    public static final String NAME = "xstream";
    public static final String DATAFORMAT = "application/xstream";
    public static final String PROCESS_ANNOTATIONS = "processAnnotations";

    private final Charset charset;
    
    private final boolean processAnnotations;

    public XStreamObjectSerializer(final String encoding, final boolean processAnnotations) {
        super(DATAFORMAT);
        this.charset = Charset.forName(encoding);
        this.processAnnotations = processAnnotations;
    }

    @Override
    protected boolean isSerializationTextBased() {
        return true;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected boolean canSerializeObject(final Object value) {
    	
    	// see https://docs.camunda.org/manual/7.8/user-guide/process-engine/variables/#supported-variable-values    	
        if (value == null) {
            return false;
        } else if (value.getClass().isPrimitive()) {
        	return false;
        } else if (value instanceof Number) {
            return false;
        } else if (value instanceof String) {
            return false;
        } else if (value instanceof Boolean) {
            return false;
        } else if (value instanceof Character) {
            return false;
        } else if (value instanceof Date) {
        	return false;
        }
        return true;

    }

    // support old camunda engine
    protected boolean canSerializeValue(final Object value) {
        return canSerializeObject(value);
    }

    @Override
    protected String getTypeNameForDeserialized(final Object deserializedObject) {
        return deserializedObject.getClass().getName();
    }

    @Override
    protected Object deserializeFromByteArray(final byte[] object, final String objectTypeName) throws Exception {
        final ByteArrayInputStream in = new ByteArrayInputStream(object);
        final InputStreamReader reader = new InputStreamReader(in, charset);
        return getXStream(objectTypeName).fromXML(reader);
    }

    @Override
    protected byte[] serializeToByteArray(final Object objectToBeSerialized) throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final OutputStreamWriter writer = new OutputStreamWriter(out, charset);
        getXStream(objectToBeSerialized).toXML(objectToBeSerialized, writer);
        return out.toByteArray();
    }

    private XStream getXStream(final String objectTypeName) throws Exception {
    	
    	final Class<?> objectClass = ReflectUtil.getClassLoader().loadClass(objectTypeName);
    	
    	return getXStream(objectClass);
    	
    }
    
    private XStream getXStream(final Object objectToBeSerialized) {
    	
    	return getXStream(objectToBeSerialized.getClass());
    	
    }
    
    private XStream getXStream(final Class<?> objectClass) {
        
    	final StaxDriver staxDriver = new StaxDriver() {
            @Override
            public StaxWriter createStaxWriter(final XMLStreamWriter out) throws XMLStreamException {
                // the boolean parameter controls the production of XML
                // declaration
                return createStaxWriter(out, false);
            }
        };
        XStream result = new XStream(staxDriver);
        result.setMode(XStream.ID_REFERENCES); // no xpath, just ids
        result.setClassLoader(ReflectUtil.getClassLoader()); // use isolated
                                                             // class loader
        if (processAnnotations) {
        	result.processAnnotations(objectClass);
        }
        return result;
        
    }

}
