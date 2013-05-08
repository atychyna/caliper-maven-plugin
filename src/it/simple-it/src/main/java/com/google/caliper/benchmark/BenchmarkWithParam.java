package com.google.caliper.benchmark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.caliper.Benchmark;
import com.google.caliper.Param;

/**
 * @author Anton Tychyna
 */
public class BenchmarkWithParam extends Benchmark {
	@Param
	private int length;

	public long timeBenchmark(long reps) {
		long c = 0;
		for (long i = 0; i < reps; i++) {
			c += i;
		}
		writeResultToFile(c);
		return c;
	}

	private void writeResultToFile(long c) {
		File f = new File("/tmp/result");
		PrintWriter s = null;
		try {
			s = new PrintWriter(new FileOutputStream(f));
			s.write(Long.toString(c));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (s != null) {
				s.close();
			}
		}
	}
}
