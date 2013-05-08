package com.google.caliper.maven;

import java.util.List;
import java.util.Set;

import org.apache.maven.surefire.util.DefaultScanResult;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Anton Tychyna
 */
public class BenchmarkScanResult {
	private Set<String> files = Sets.newHashSet();

	@SuppressWarnings("unchecked")
	public BenchmarkScanResult(DefaultScanResult r) {
		files.addAll(r.getFiles());
	}

	public boolean isEmpty() {
		return files.isEmpty();
	}

	public List<CaliperBenchmark> toBenchmarks(ClassLoader loader) throws ClassNotFoundException {
		List<CaliperBenchmark> result = Lists.newArrayList();
		for (String f : files) {
			result.add(new CaliperBenchmark(loader, f));
		}
		return result;
	}
}
