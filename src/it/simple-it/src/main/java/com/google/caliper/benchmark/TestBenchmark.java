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

import java.io.PrintWriter;

public class TestBenchmark extends Benchmark {
	public int timeNoException(int reps) {
		int t = 0;
		for (long i = 0; i < reps; i++) {
			for (int j = 1; j < 100; j++) {
				t += (int) Math.pow(2, 30) % j;
			}
		}
		Utils.writeToFile("TestBenchmark", new Utils.WriteCallback() {
			@Override
			public void write(PrintWriter w) {
				w.write("success");
			}
		});
		return t;
	}
}
