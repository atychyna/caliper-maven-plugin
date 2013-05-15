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
package com.google.caliper.benchmark;

import com.google.caliper.Benchmark;
import com.google.caliper.Param;

import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

public class BenchmarkWithParam extends Benchmark {
	private static Set<Integer> result = new LinkedHashSet<Integer>();

	@Param
	private int length;

	public int timeBenchmark(int reps) {
		int c = 0;
		for (int i = 0; i < reps; i++) {
			for (int j = 0; j < 1000; j++) {
				c += (j * 5 + 7) % 1000;
			}
		}
		result.add(length);
		return c;
	}

	@Override
	protected void tearDown() throws Exception {
		writeResultToFile();
	}

	private void writeResultToFile() {
		Utils.writeToFile("BenchmarkWithParam", new Utils.WriteCallback() {
			@Override
			public void write(PrintWriter w) {
				boolean first = true;
				for (Integer i : result) {
					if (!first) {
						w.print(",");
					}
					w.print(i);
					first = false;
				}
			}
		});
	}
}
