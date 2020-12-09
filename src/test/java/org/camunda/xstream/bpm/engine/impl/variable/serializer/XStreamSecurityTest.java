package org.camunda.xstream.bpm.engine.impl.variable.serializer;

import com.thoughtworks.xstream.security.ForbiddenClassException;
import org.camunda.xstream.bpm.engine.impl.variable.serializer.classes.TestClass;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

public class XStreamSecurityTest {


    @Test
    public void test_dont_parse_not_allowed_Class () {
        final XStreamObjectSerializer xSOS =
                new XStreamObjectSerializer("UTF-8",
                        new LinkedList<>(),
                        new LinkedList<>(),
                        false,
                        false,
                        false);

        final TestClass testClass = new TestClass();

        Assert.assertThrows(ForbiddenClassException.class, () -> xSOS.deserializeFromByteArray(xSOS.serializeToByteArray(testClass), TestClass.class.getName()));
    }

    @Test
    public void test_parse_allowed_Class () {
        final XStreamObjectSerializer xSOS =
                new XStreamObjectSerializer("UTF-8",
                        new LinkedList<>(),
                        new LinkedList<String>(){{
                            add(TestClass.class.getTypeName());
                        }},
                        false,
                        false,
                        false);

        final TestClass t1 = new TestClass();
        t1.setIdentifier("211");

        try {
            xSOS.deserializeFromByteArray(xSOS.serializeToByteArray(t1), TestClass.class.getName());
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
