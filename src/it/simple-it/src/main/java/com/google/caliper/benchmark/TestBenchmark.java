package com.google.caliper.benchmark;

import java.io.PrintWriter;

import com.google.caliper.Benchmark;

/**
 * @author Anton Tychyna
 */
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
