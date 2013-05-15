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

import org.apache.commons.lang3.reflect.MethodUtils;

import static com.google.caliper.maven.ClassLoaderUtils.loadClass;

/**
 * Wrapper around {@link CaliperBenchmarkHarness}.
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
