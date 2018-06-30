package com.graly.promisone.runtime.model;

import java.net.URL;
import org.osgi.framework.Bundle;

import com.graly.promisone.runtime.RuntimeService;

public interface RuntimeContext {
	
	RuntimeService getRuntime();
	
	Bundle getBundle();
	
	Class loadClass(String className) throws ClassNotFoundException;
	
	URL getResource(String name);
	
	URL getLocalResource(String name);
	
	public void deploy(URL url) throws Exception;
	
	public void destroy();
}
