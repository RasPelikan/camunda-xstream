package org.camunda.xstream.bpm.engine.impl.variable.serializer;

import com.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter;
import org.camunda.xstream.bpm.engine.impl.variable.serializer.classes.NotRelatedTestClass;
import org.camunda.xstream.bpm.engine.impl.variable.serializer.classes.TestClass;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

public class XStreamIgnoreUnknownElementsTest {

    @Test
    public void test_throws_error_on_unknown_element() throws Exception {
        XStreamObjectSerializer s1 = new XStreamObjectSerializer("UTF-8", new LinkedList<>(),
                new LinkedList<String>(){{
                    add(TestClass.class.getTypeName());
                    add(NotRelatedTestClass.class.getTypeName());
                }}, true, false);

        TestClass t1 = new TestClass();
        t1.setIdentifier("123");

        Assert.assertThrows(AbstractReflectionConverter.UnknownFieldException.class, () -> s1.deserializeFromByteArray(s1.serializeToByteArray(t1), NotRelatedTestClass.class.getName()));
    }

    @Test
    public void test_no_error_on_unknown_element() throws Exception {
        XStreamObjectSerializer s1 = new XStreamObjectSerializer("UTF-8", new LinkedList<>(),
                new LinkedList<String>(){{
                    add(TestClass.class.getTypeName());
                    add(NotRelatedTestClass.class.getTypeName());
                }}, true, true);

        TestClass t1 = new TestClass();
        t1.setIdentifier("123");


        try {
            s1.deserializeFromByteArray(s1.serializeToByteArray(t1), NotRelatedTestClass.class.getName());
        } catch (Exception e) {
            Assert.fail();
        }
    }


}
