package com.graly.mes.prd.designer.common;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

	public static final String PLUGIN_ID = "com.graly.mes.prd.designer.common";

	private static Activator activator;
	
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		activator = this;
	}

	public void stop(BundleContext context) throws Exception {
		activator = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return activator;
	}

}
