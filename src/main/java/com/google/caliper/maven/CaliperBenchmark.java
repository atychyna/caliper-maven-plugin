package com.google.caliper.maven;

import static com.google.caliper.maven.ClassLoaderUtils.loadClass;

import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * Wrapper around {@link CaliperBenchmarkHarness}.
 * 
 * @author Anton Tychyna
 */
public class CaliperBenchmark {
	private Object benchmark;

	public CaliperBenchmark(ClassLoader loader, String benchmarkClass) throws ClassNotFoundException {
		Class caliperBenchmark = loadClass(loader, "com.google.caliper.maven.CaliperBenchmarkHarness",
				"Please report this issue to bug tracker");
		try {
			benchmark = MethodUtils.invokeStaticMethod(caliperBenchmark, "create", benchmarkClass);
		} catch (Exception e) {
			throw new RuntimeException("Please report this problem to caliper-maven-plugin bug tracker", e);
		}
	}

	public void run(String[] args) throws Exception {
		MethodUtils.invokeMethod(benchmark, "run", new Object[] { args });
	}

	@Override
	public String toString() {
		return String.valueOf(benchmark);
	}
}
