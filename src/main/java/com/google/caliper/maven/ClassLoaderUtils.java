/*
 * Copyright (C) 2013 Anton Tychyna <anton.tychina@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.caliper.maven;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

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
