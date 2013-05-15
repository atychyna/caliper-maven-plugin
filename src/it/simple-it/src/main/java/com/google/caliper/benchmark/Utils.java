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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

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
