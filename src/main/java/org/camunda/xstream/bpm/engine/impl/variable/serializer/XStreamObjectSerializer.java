package org.camunda.xstream.bpm.engine.impl.variable.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.camunda.bpm.engine.impl.util.ReflectUtil;
import org.camunda.bpm.engine.impl.variable.serializer.AbstractObjectValueSerializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.StaxWriter;

public class XStreamObjectSerializer extends AbstractObjectValueSerializer {

	public static final String CLASSPROVIDER = "org.camunda.xstream.ClassProvider";
    public static final String NAME = "xstream";
    public static final String DATAFORMAT = "application/xstream";

    private final Charset charset;

    private final List<String> converters;

    private final List<String> allowedTypes;

    private final boolean ignoreUnknownElements;

    private final boolean useExternalClassProvider;

    private Map<ClassLoader, XStream> xStream = new HashMap<>();

    public XStreamObjectSerializer(final String encoding,
                                   final List<String> converters,
                                   final List<String> allowedTypes,
                                   final boolean ignoreUnknownElements,
                                   final boolean useExternalClassProvider
                                   ) {
        super(DATAFORMAT);
        this.charset = Charset.forName(encoding);
        this.converters = converters;
        this.allowedTypes = allowedTypes;
        this.ignoreUnknownElements = ignoreUnknownElements;
        this.useExternalClassProvider = useExternalClassProvider;
    }

    public String log() {
        return "XStreamObjectSerializer configured | " +
                "charset=" + charset +
                ", converters=" + converters +
                ", allowedTypes=" + allowedTypes +
                ", ignoreUnknownElements=" + ignoreUnknownElements;
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
        // parameter "objectTypeName" is ignored to support refactoring of classes
        return getXStream().fromXML(reader);
    }

    @Override
    protected byte[] serializeToByteArray(final Object objectToBeSerialized) throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final OutputStreamWriter writer = new OutputStreamWriter(out, charset);
        getXStream().toXML(objectToBeSerialized, writer);
        return out.toByteArray();
    }

    private XStream getXStream() {
    	
    	final ClassLoader classLoader = ReflectUtil.getClassLoader();
    	XStream sharedXStream = xStream.get(classLoader);
    	if (sharedXStream == null) {
    		synchronized (xStream) {
    			sharedXStream = createXStream(classLoader);
    			xStream.put(classLoader, sharedXStream);
    		}
    	}
    	return sharedXStream;

    }

    private XStream createXStream(ClassLoader classLoader) {
    	
        final StaxDriver staxDriver = new StaxDriver() {
            @Override
            public StaxWriter createStaxWriter(final XMLStreamWriter out) throws XMLStreamException {
                // the boolean parameter controls the production of XML
                // declaration
                return createStaxWriter(out, false);
            }
        };

        final XStream xStream = new XStream(staxDriver);
        xStream.setMode(XStream.ID_REFERENCES); // no xpath, just ids
        xStream.setClassLoader(classLoader); // use isolated class loader

        if ( ignoreUnknownElements ) {
            xStream.ignoreUnknownElements();
        }
        if (useExternalClassProvider) {
            ingestExternalAnnotations(xStream);
        }
        registerConverters(xStream);
        setupSecurity(xStream);
        
        return xStream;
        
    }

    private void ingestExternalAnnotations(XStream xs) {
        try (InputStream in = ReflectUtil
                .getClassLoader()
                .getResourceAsStream("META-INF/services/" + CLASSPROVIDER)) {
        	if (in == null) {
        		return;
        	}
        	final ByteArrayOutputStream content = new ByteArrayOutputStream();
        	int read = -1;
        	while ((read = in.read()) != -1) {
        		content.write(read);
        	}
        	final String classProviderClass = new String(content.toByteArray());
        	@SuppressWarnings("unchecked")
			final Collection<Class<?>> classes = (Collection<Class<?>>) ReflectUtil
        			.getClassLoader()
        			.loadClass(String.valueOf(classProviderClass))
        			.getMethod("getAnnotatedClasses")
        			.invoke(null);
        	final Class<?>[] arrayOfClasses = classes.toArray(new Class[0]);
            xs.processAnnotations(arrayOfClasses);
        } catch (RuntimeException e) {
        	throw e;
        } catch (Exception e) {
        	throw new RuntimeException("Could not ingest external annotations", e);
        }
    }

    private void setupSecurity(final XStream result) {
		XStream.setupDefaultSecurity(result);
        for (final String allowedType : allowedTypes) {
        	final String trimmedAllowedType = allowedType.trim();
        	if (trimmedAllowedType.startsWith("/") && trimmedAllowedType.endsWith("/")) {
        		addTypesByRegExp(result, trimmedAllowedType);
        	}
        	else if (trimmedAllowedType.startsWith("<")) {
        		addTypeHierarchy(result, trimmedAllowedType);
        	}
        	else if (trimmedAllowedType.contains("*")) {
        		addTypesByWildfcard(result, trimmedAllowedType);
        	}
        	else {
        		addType(result, trimmedAllowedType);
        	}
        }
	}

	private void addType(final XStream result, final String allowedType) {
		if ((allowedType == null) || allowedType.isEmpty()) {
			return;
		}
		result.allowTypes(new String[] { allowedType });
	}

	private void addTypesByWildfcard(final XStream result, final String wildcardPattern) {
		if ((wildcardPattern == null) || wildcardPattern.isEmpty()) {
			return;
		}
		result.allowTypesByWildcard(new String[] { wildcardPattern });
	}

	private void addTypeHierarchy(final XStream result, final String allowedType) {
		if ((allowedType == null) || allowedType.isEmpty()) {
			return;
		}
		final Class<?> clasz = ReflectUtil.loadClass(allowedType.substring(1));
		result.allowTypeHierarchy(clasz);
	}

	private void addTypesByRegExp(final XStream result, final String regexp) {
		if ((regexp == null) || regexp.isEmpty()) {
			return;
		}
		result.allowTypesByRegExp(new String[] { regexp.substring(1, regexp.length() - 1) });
	}

	private void registerConverters(XStream result) {
		for (final String converterClassName : converters) {
        	try {
        		if (converterClassName == null) {
        			continue;
        		}
        		final String trimmedConverterClassName = converterClassName.trim();
        		if (trimmedConverterClassName.isEmpty()) {
        			continue;
        		}
	        	final Class<?> converterClass = ReflectUtil.getClassLoader().loadClass(
	        			trimmedConverterClassName);
	        	final Converter converter = (Converter) converterClass.newInstance();
	        	result.registerConverter(converter);
        	} catch (Exception e) {
        		throw new RuntimeException("Could not register converter '"
        				+ converterClassName + "'", e);
        	}
        }
	}
}
