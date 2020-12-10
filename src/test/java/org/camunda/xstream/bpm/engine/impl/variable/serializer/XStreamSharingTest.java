package org.camunda.xstream.bpm.engine.impl.variable.serializer;

import java.util.LinkedList;

import org.camunda.xstream.bpm.engine.impl.variable.serializer.classes.NotRelatedTestClass;
import org.camunda.xstream.bpm.engine.impl.variable.serializer.classes.TestClass;
import org.junit.Assert;
import org.junit.Test;

public class XStreamSharingTest {

	@Test
	public void testSharing() throws Exception {
		
        XStreamObjectSerializer s1 = new XStreamObjectSerializer("UTF-8", new LinkedList<>(),
                new LinkedList<String>(){{
                    add(TestClass.class.getTypeName());
                    add(NotRelatedTestClass.class.getTypeName());
                }}, false, true);

        final TestClass testObject = new TestClass();
        testObject.setIdentifier("me");
        
        final byte[] serialized = s1.serializeToByteArray(testObject);
        final Object newObject = s1.deserializeFromByteArray(serialized, TestClass.class.getName());
		Assert.assertNotNull(newObject);

        Assert.assertTrue(ClassProvider.counter == 1);
        
	}
	
}
