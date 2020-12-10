package org.camunda.xstream.bpm.engine.impl.variable.serializer;

import java.util.LinkedList;

import org.camunda.xstream.bpm.engine.impl.variable.serializer.classes.NotRelatedTestClass;
import org.camunda.xstream.bpm.engine.impl.variable.serializer.classes.TestClass;
import org.junit.Assert;
import org.junit.Test;

import com.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter;

public class XStreamIgnoreUnknownElementsTest {

	private static final String SERIALIZED = "<org.camunda.xstream.bpm.engine.impl.variable.serializer.classes.NotRelatedTestClass id=\"1\"><identifier>123</identifier></org.camunda.xstream.bpm.engine.impl.variable.serializer.classes.NotRelatedTestClass>\n";

    @Test
    public void test_throws_error_on_unknown_element() throws Exception {
        
        XStreamObjectSerializer s1 = new XStreamObjectSerializer("UTF-8", new LinkedList<>(),
                new LinkedList<String>(){{
                    add(TestClass.class.getTypeName());
                    add(NotRelatedTestClass.class.getTypeName());
                }}, false, false);

        Assert.assertThrows(AbstractReflectionConverter.UnknownFieldException.class,
        		() -> s1.deserializeFromByteArray(SERIALIZED.getBytes(), NotRelatedTestClass.class.getName()));
        
    }

    @Test
    public void test_no_error_on_unknown_element() throws Exception {
    	
        XStreamObjectSerializer s1 = new XStreamObjectSerializer("UTF-8", new LinkedList<>(),
                new LinkedList<String>(){{
                    add(TestClass.class.getTypeName());
                    add(NotRelatedTestClass.class.getTypeName());
                }}, true, false);

        try {
            s1.deserializeFromByteArray(SERIALIZED.getBytes(), NotRelatedTestClass.class.getName());
        } catch (Exception e) {
            Assert.fail();
        }
    }


}
