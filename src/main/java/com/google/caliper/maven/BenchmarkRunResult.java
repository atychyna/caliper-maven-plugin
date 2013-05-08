package com.google.caliper.maven;

/**
 * @author Anton Tychyna
 */
public class BenchmarkRunResult {
	int successes;
	int failures;

	@Override
	public String toString() {
		return "Benchmarks run: " + (successes + failures) + ", failures: " + failures;
	}
}
