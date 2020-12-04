package org.camunda.xstream.bpm.engine.impl.variable.serializer;

import org.camunda.xstream.bpm.engine.impl.variable.serializer.classes.TestClass;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class XStreamObjectSerializerTest {
	
	@Test
	public void testSerialization() throws Exception {
	
		final XStreamObjectSerializer serializer = new XStreamObjectSerializer("UTF-8", new LinkedList<>(), new LinkedList<String>(){{
			add(TestClass.class.getTypeName());
		}},false,
				false);
		
		final TestClass test1 = new TestClass();
		test1.setIdentifier("4711");
		
		final byte[] serialized1 = serializer.serializeToByteArray(test1);

		Assert.assertNotNull(serialized1);
		Assert.assertFalse(new String(serialized1, StandardCharsets.UTF_8).contains(TestClass.SHORTCUT));
		Assert.assertTrue(new String(serialized1, StandardCharsets.UTF_8).contains(TestClass.class.getName()));
		
		final Object result1 = serializer.deserializeFromByteArray(serialized1, TestClass.class.getName());
		
		Assert.assertNotNull(result1);
		Assert.assertEquals(TestClass.class, result1.getClass());
		final TestClass resultTest1 = (TestClass) result1;
		Assert.assertEquals(resultTest1.getIdentifier(), test1.getIdentifier());
		Assert.assertNull(resultTest1.getParent());

		final TestClass test2 = new TestClass();
		test2.setIdentifier("0815");
		test2.setParent(test1);
		
		final byte[] serialized2 = serializer.serializeToByteArray(test2);
		
		final Object result2 = serializer.deserializeFromByteArray(serialized2, TestClass.class.getName());
		
		Assert.assertNotNull(result2);
		Assert.assertEquals(TestClass.class, result2.getClass());
		final TestClass resultTest2 = (TestClass) result2;
		Assert.assertEquals(test2.getIdentifier(), resultTest2.getIdentifier());
		Assert.assertNotNull(resultTest2.getParent());
		Assert.assertEquals(test1.getIdentifier(), resultTest2.getParent().getIdentifier());
		Assert.assertNull(resultTest2.getParent().getParent());
		
	}

	@Test
	public void testSerializationWithAnnotation() throws Exception {
	
		final XStreamObjectSerializer serializer = new XStreamObjectSerializer("UTF-8",  new LinkedList<>(), new LinkedList<String>(){{
			add(TestClass.class.getTypeName());
		}},true,
				false);
		
		final TestClass test1 = new TestClass();
		test1.setIdentifier("4711");
		
		final byte[] serialized1 = serializer.serializeToByteArray(test1);

		Assert.assertNotNull(serialized1);
		Assert.assertTrue(new String(serialized1, StandardCharsets.UTF_8).contains(TestClass.SHORTCUT));
		Assert.assertFalse(new String(serialized1, StandardCharsets.UTF_8).contains(TestClass.class.getName()));

		final Object result1 = serializer.deserializeFromByteArray(serialized1, TestClass.class.getName());
		
		Assert.assertNotNull(result1);
		Assert.assertEquals(TestClass.class, result1.getClass());
		final TestClass resultTest1 = (TestClass) result1;
		Assert.assertEquals(resultTest1.getIdentifier(), test1.getIdentifier());
		Assert.assertNull(resultTest1.getParent());

		final TestClass test2 = new TestClass();
		test2.setIdentifier("0815");
		test2.setParent(test1);
		
		final byte[] serialized2 = serializer.serializeToByteArray(test2);
		
		final Object result2 = serializer.deserializeFromByteArray(serialized2, TestClass.class.getName());
		
		Assert.assertNotNull(result2);
		Assert.assertEquals(TestClass.class, result2.getClass());
		final TestClass resultTest2 = (TestClass) result2;
		Assert.assertEquals(test2.getIdentifier(), resultTest2.getIdentifier());
		Assert.assertNotNull(resultTest2.getParent());
		Assert.assertEquals(test1.getIdentifier(), resultTest2.getParent().getIdentifier());
		Assert.assertNull(resultTest2.getParent().getParent());
		
	}

}
