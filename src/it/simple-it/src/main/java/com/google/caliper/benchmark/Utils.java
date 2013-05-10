package com.google.caliper.benchmark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Anton Tychyna
 */
public class Utils {
	public static interface WriteCallback {
		void write(PrintWriter w);
	}

	public static void writeToFile(String name, WriteCallback callback) {
		File f = new File(System.getProperty("java.io.tmpdir") + "/" + name);
		PrintWriter s = null;
		try {
			s = new PrintWriter(new FileOutputStream(f));
			callback.write(s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (s != null) {
				s.close();
			}
		}
	}
}
