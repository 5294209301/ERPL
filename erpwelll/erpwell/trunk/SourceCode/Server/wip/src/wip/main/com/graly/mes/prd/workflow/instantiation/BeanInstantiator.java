package com.graly.mes.prd.workflow.instantiation;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.dom4j.Element;

public class BeanInstantiator extends FieldInstantiator implements Instantiator {

	static final Logger logger = Logger.getLogger(BeanInstantiator.class);
	
	protected void setPropertyValue(Class clazz, Object newInstance,
			String propertyName, Element propertyElement) {
		try {
			// create the setter method name from the property name
			String setterMethodName = "set"
					+ propertyName.substring(0, 1).toUpperCase()
					+ propertyName.substring(1);

			// find the setter method
			Method method = findSetter(clazz, setterMethodName);
			Class propertyType = method.getParameterTypes()[0];

			// if the setter method was found
			if (method != null) {
				// make it accessible
				method.setAccessible(true);
				// invoke it
				method.invoke(newInstance, new Object[] {getValue(propertyType, propertyElement)});
			} else {
				logger.error("couldn't set property '" + propertyName
						+ "' to value '" + propertyElement.asXML() + "'");
			}
		} catch (Exception e) {
			logger.error("couldn't parse property '" + propertyName
					+ "' to value '" + propertyElement.asXML() + "'", e);
		}
	}

	private Method findSetter(Class clazz, String setterMethodName) {
		Method method = null;
		Method[] methods = clazz.getDeclaredMethods();
		for (int i = 0; ((i < methods.length) && (method == null)); i++) {
			if ((setterMethodName.equals(methods[i].getName()))
					&& (methods[i].getParameterTypes() != null)
					&& (methods[i].getParameterTypes().length == 1)) {
				method = methods[i];
			}
		}
		if ((method == null) && (clazz != Object.class)) {
			method = findSetter(clazz.getSuperclass(), setterMethodName);
		}
		return method;
	}
}
