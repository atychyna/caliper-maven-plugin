package com.google.caliper.maven;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.surefire.util.DirectoryScanner;

import com.google.common.collect.ImmutableList;

/**
 * @author Anton Tychyna
 */
public class BenchmarkDirectoryScanner {
	private File basedir;
	private List<String> includes = ImmutableList.of();
	private List<String> excludes = ImmutableList.of();
	private List<String> specificBenchmarks = ImmutableList.of();

	public BenchmarkDirectoryScanner(File basedir) {
		this.basedir = basedir;
	}

	public BenchmarkScanResult scan() {
		DirectoryScanner scanner = new DirectoryScanner(basedir, includes, excludes, specificBenchmarks);
		return new BenchmarkScanResult(scanner.scan());
	}

	public void setBasedir(File basedir) {
		this.basedir = basedir;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public void setSpecificBenchmarks(List<String> specificTests) {
		this.specificBenchmarks = specificTests;
	}
}
