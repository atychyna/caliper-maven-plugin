package com.google.caliper.maven;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * @author Anton Tychyna
 */
public class BenchmarkClassLoader extends URLClassLoader {
	public BenchmarkClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public BenchmarkClassLoader(URL[] urls) {
		super(urls);
	}

	public BenchmarkClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (name.endsWith("CaliperBenchmarkHarness")) {
			return findClass(name);
		}
		return super.loadClass(name);
	}
}
