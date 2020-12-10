package org.camunda.xstream.bpm.engine.impl.variable.serializer.classes;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(TestClass.SHORTCUT)
public class TestClass {
	
	public static final String SHORTCUT = "test";
	
	private String identifier;
	
	private TestClass parent;
	
	public String getIdentifier() {
		return identifier;
	}
	
	public TestClass getParent() {
		return parent;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public void setParent(TestClass parent) {
		this.parent = parent;
	}
	
}
