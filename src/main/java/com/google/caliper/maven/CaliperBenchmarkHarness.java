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

import com.google.caliper.Benchmark;
import com.google.caliper.config.InvalidConfigurationException;
import com.google.caliper.runner.CaliperMain;
import com.google.caliper.runner.InvalidBenchmarkException;
import com.google.caliper.util.InvalidCommandException;
import com.google.common.collect.ObjectArrays;

import java.io.PrintWriter;

/**
 * Wrapper around actual benchmark. This class is loaded using custom {@link BenchmarkClassLoader} which
 * contains library dependencies (including Caliper) as Maven doesn't include libraries and project
 * tests/classes in plugin classloader classpath.
 * 
 * @see BenchmarkClassLoader
 * @see CaliperBenchmark
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
