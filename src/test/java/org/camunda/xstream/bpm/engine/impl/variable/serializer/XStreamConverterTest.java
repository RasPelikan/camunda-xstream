package org.camunda.xstream.bpm.engine.impl.variable.serializer;

import org.camunda.xstream.bpm.engine.impl.variable.serializer.classes.TestClass;
import org.camunda.xstream.bpm.engine.impl.variable.serializer.classes.TestClassConverter;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

public class XStreamConverterTest {

    @Test
    public void test_using_custom_converter_loose_parent() throws Exception {
        final XStreamObjectSerializer serializer =
                new XStreamObjectSerializer("UTF-8",
                        new LinkedList<String>(){{
                            add(TestClassConverter.class.getTypeName());
                        }},
                        new LinkedList<String>(){{
                            add(TestClass.class.getTypeName());
                        }},false,
                        false,
                        false);

        final TestClass test1 = new TestClass();
        test1.setIdentifier("4711");

        final TestClass test2 = new TestClass();
        test2.setIdentifier("0815");
        test2.setParent(test1);

        final TestClass remarshalledTest2 = (TestClass) serializer.deserializeFromByteArray(serializer.serializeToByteArray(test2), TestClass.class.getName());
        System.out.println(new String(serializer.serializeToByteArray(test2)));
        Assert.assertNull(remarshalledTest2.getParent());
    }

    @Test
    public void test_using_default_converter_doesnt_loose_parent() throws Exception {
        final XStreamObjectSerializer serializer =
                new XStreamObjectSerializer("UTF-8",
                        new LinkedList<>(),
                        new LinkedList<String>(){{
                            add(TestClass.class.getTypeName());
                        }},false,
                        false,
                        false);

        final TestClass test1 = new TestClass();
        test1.setIdentifier("4711");

        final TestClass test2 = new TestClass();
        test2.setIdentifier("0815");
        test2.setParent(test1);

        final TestClass remarshalledTest2 = (TestClass) serializer.deserializeFromByteArray(serializer.serializeToByteArray(test2), TestClass.class.getName());
        System.out.println(new String(serializer.serializeToByteArray(test2)));
        Assert.assertEquals(test2.getParent().getIdentifier(), remarshalledTest2.getParent().getIdentifier());
    }
}
