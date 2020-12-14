package org.camunda.xstream.spring;

import java.util.List;

import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.spring.boot.starter.configuration.Ordering;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@ConfigurationProperties(prefix = "camunda.xstream-serialization")
public class PluginConfiguration {

	@Value("${encoding:UTF-8}")
	private String encoding;

	private boolean useExternalClassProvider = false;

	private boolean ignoreUnknownElements = true;

	private List<String> allowedTypes;

	private List<String> converters;

	@Bean(name = "xstreamProcessEnginePlugin")
	@Order(Ordering.DEFAULT_ORDER + 1)
	public ProcessEnginePlugin xstreamProcessEnginePlugin() {

		final org.camunda.xstream.ProcessEnginePlugin plugin = new org.camunda.xstream.ProcessEnginePlugin();
		if (encoding != null) {
			plugin.setEncoding(encoding);
		}
		if (allowedTypes != null) {
			plugin.setAllowedTypes(String.join(",", allowedTypes));
		}
		if (converters != null) {
			plugin.setConverters(String.join(",", converters));
		}
		if (ignoreUnknownElements) {
			plugin.setIgnoreUnknownElements("true");
		} else {
			plugin.setIgnoreUnknownElements("false");
		}
		if (useExternalClassProvider) {
			plugin.setUseExternalClassProvider("true");
		} else {
			plugin.setUseExternalClassProvider("false");
		}
		return plugin;
		
	}
	
	public void setAllowedTypes(List<String> allowedTypes) {
		this.allowedTypes = allowedTypes;
	}
	
	public void setConverters(List<String> converters) {
		this.converters = converters;
	}

	public void setIgnoreUnknownElements(Boolean ignoreUnknownElements) {
		this.ignoreUnknownElements = ignoreUnknownElements;
	}

	public void setuseExternalClassProvider(Boolean ignoreUnknownElements) {
		this.useExternalClassProvider = ignoreUnknownElements;
	}
	
}
