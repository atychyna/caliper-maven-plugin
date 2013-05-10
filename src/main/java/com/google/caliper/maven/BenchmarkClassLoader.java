package com.google.caliper.maven;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Anton Tychyna
 */
public class BenchmarkClassLoader extends URLClassLoader {
	private Class benchmarkHarness;

	public BenchmarkClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (name.endsWith("CaliperBenchmarkHarness")) {
			if (benchmarkHarness == null) {
				benchmarkHarness = findClass(name);
			}
			return benchmarkHarness;
		}
		return super.loadClass(name);
	}
}
