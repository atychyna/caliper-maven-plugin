package com.google.caliper.benchmark;

import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.caliper.Benchmark;
import com.google.caliper.Param;

/**
 * @author Anton Tychyna
 */
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
