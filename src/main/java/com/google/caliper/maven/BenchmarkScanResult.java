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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.maven.surefire.util.DefaultScanResult;

import java.util.List;
import java.util.Set;

public class BenchmarkScanResult {
	private Set<String> files = Sets.newHashSet();

	@SuppressWarnings("unchecked")
	public BenchmarkScanResult(DefaultScanResult r) {
		files.addAll(r.getFiles());
	}

	public boolean isEmpty() {
		return files.isEmpty();
	}

	public List<CaliperBenchmark> toBenchmarks(ClassLoader loader) throws ClassNotFoundException {
		List<CaliperBenchmark> result = Lists.newArrayList();
		for (String f : files) {
			if (!f.contains("$")) {
				result.add(new CaliperBenchmark(loader, f));
			}
		}
		return result;
	}
}
