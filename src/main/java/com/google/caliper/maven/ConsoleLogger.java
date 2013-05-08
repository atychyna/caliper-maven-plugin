package com.google.caliper.maven;

import java.io.PrintStream;

/**
 * @author Anton Tychyna
 */
public class ConsoleLogger {
	private PrintStream out;

	public ConsoleLogger(PrintStream out) {
		this.out = out;
	}

	public void info(Object info) {
		out.println(info);
	}
}
