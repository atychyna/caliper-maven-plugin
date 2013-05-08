package com.google.caliper.maven;

import java.io.PrintWriter;

import com.google.caliper.Benchmark;
import com.google.caliper.config.InvalidConfigurationException;
import com.google.caliper.runner.CaliperMain;
import com.google.caliper.runner.InvalidBenchmarkException;
import com.google.caliper.util.InvalidCommandException;
import com.google.common.collect.ObjectArrays;

/**
 * @author Anton Tychyna
 */
public class CaliperBenchmarkHarness {
	public static final String BENCHMARK_CLASS_NAME = "com.google.caliper.Benchmark";
	private Class<?> benchmarkClass;

	private CaliperBenchmarkHarness(Class<?> clazz) {
		benchmarkClass = clazz;
	}

	public void run(String[] args) throws InvalidConfigurationException, InvalidCommandException,
			InvalidBenchmarkException {
		PrintWriter stdout = new PrintWriter(System.out, true);
		PrintWriter stderr = new PrintWriter(System.err, true);
		CaliperMain.exitlessMain(ObjectArrays.concat(args, benchmarkClass.getName()), stdout, stderr);
	}

	public static CaliperBenchmarkHarness create(String benchmarkClassName) throws ClassNotFoundException {
		Class<?> clazz = ClassLoaderUtils.loadClass(CaliperBenchmarkHarness.class.getClassLoader(),
				benchmarkClassName, "Can't find benchmark class " + benchmarkClassName);
		if (!Benchmark.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException("Class " + benchmarkClassName + " is not inherited from "
					+ BENCHMARK_CLASS_NAME);
		}
		return new CaliperBenchmarkHarness(clazz);
	}

	public Class<?> getBenchmarkClass() {
		return benchmarkClass;
	}

	@Override
	public String toString() {
		return benchmarkClass.getName();
	}
}
