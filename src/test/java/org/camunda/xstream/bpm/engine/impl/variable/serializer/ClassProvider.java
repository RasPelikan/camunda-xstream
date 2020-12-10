package org.camunda.xstream.bpm.engine.impl.variable.serializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.camunda.xstream.bpm.engine.impl.variable.serializer.classes.TestClass;

public class ClassProvider {

	public static Collection<Class<?>> getAnnotatedClasses() {
		
		Set<Class<?>> ret = new HashSet<>();
        ret.add(TestClass.class);
        return ret;
		
	}
	
}
