package com.google.caliper.maven;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Anton Tychyna
 */
public class ClassLoaderUtils {

	public static String getClassPathString(ClassLoader classLoader) {
		if (classLoader instanceof URLClassLoader) {
			URLClassLoader loader = (URLClassLoader) classLoader;
			StringBuilder result = new StringBuilder();
			boolean first = true;
			for (URL url : loader.getURLs()) {
				if (!first) {
					result.append(":");
				} else {
					first = false;
				}
				result.append(url.getFile());
			}
			return result.toString();
		}
		return "";
	}

	public static Class<?> loadClass(ClassLoader loader, String className, String errorMessage)
			throws ClassNotFoundException {
		Class clazz;
		try {
			clazz = loader.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException(errorMessage, e);
		}
		return clazz;
	}
}
