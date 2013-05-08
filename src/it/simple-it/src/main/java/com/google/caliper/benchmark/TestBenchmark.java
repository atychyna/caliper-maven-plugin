package com.google.caliper.benchmark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.caliper.Benchmark;

/**
 * @author Anton Tychyna
 */
public class TestBenchmark extends Benchmark {
	public long timeBenchmark(long reps) {
		throw new IllegalArgumentException("asdsa asd");
		// long c = 0;
		// for (long i = 0; i < reps; i++) {
		// c += i;
		// }
		// writeResultToFile(c);
		// return c;
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
