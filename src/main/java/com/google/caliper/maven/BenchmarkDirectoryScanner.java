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
package com.google.caliper.maven;

import com.google.common.collect.ImmutableList;
import org.apache.maven.plugin.surefire.util.DirectoryScanner;

import java.io.File;
import java.util.List;

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
