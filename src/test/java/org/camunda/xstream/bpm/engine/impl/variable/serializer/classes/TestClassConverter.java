package org.camunda.xstream.bpm.engine.impl.variable.serializer.classes;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class TestClassConverter implements Converter {

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        TestClass val = (TestClass) o;
        writer.startNode("id");
        writer.setValue(val.getIdentifier());
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        TestClass ret = new TestClass();
        ret.setIdentifier(reader.getAttribute("id"));
        return ret;
    }

    @Override
    public boolean canConvert(Class aClass) {
        return aClass.equals(TestClass.class);
    }
}
