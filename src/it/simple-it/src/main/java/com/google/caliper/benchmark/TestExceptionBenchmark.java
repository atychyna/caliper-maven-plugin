package com.google.caliper.benchmark;

import com.google.caliper.Benchmark;

/**
 * @author Anton Tychyna
 */
public class TestExceptionBenchmark extends Benchmark {
	public void timeException(int reps) {
		throw new IllegalArgumentException("Time493");
	}
}
