package com.google.caliper.maven;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * @author Anton Tychyna
 */
public class ClassLoaderUtils {
	private static final String PATH_SEPARATOR = System.getProperty("path.separator");
	private static final Joiner JOINER = Joiner.on(PATH_SEPARATOR);

	public static String getClassPathString(ClassLoader classLoader) {
		if (classLoader instanceof URLClassLoader) {
			URLClassLoader loader = (URLClassLoader) classLoader;
			List<String> elements = Lists.newArrayList();
			for (URL url : loader.getURLs()) {
				elements.add(url.getFile());
			}
			return JOINER.join(elements);
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
