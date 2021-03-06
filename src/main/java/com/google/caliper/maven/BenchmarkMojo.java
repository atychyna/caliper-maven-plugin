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

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Run Caliper benchmarks.
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.VERIFY, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class BenchmarkMojo extends AbstractMojo {
	private static final Joiner JOINER = Joiner.on(',');
	private static final String CALIPER_GROUP_ID = "com.google.caliper";
	private static final String CALIPER_ARTIFACT_ID = "caliper";
	private static final String ALLOCATION_INSTRUMENTER_CLASSNAME = "com.google.monitoring.runtime.instrumentation.AllocationInstrumenter";
	private static Predicate<Object> CALIPER_PREDICATE = new Predicate<Object>() {
		@Override
		public boolean apply(@Nullable Object o) {
			if (o instanceof Artifact) {
				Artifact a = (Artifact) o;
				if ((Artifact.SCOPE_COMPILE.equals(a.getScope()) || Artifact.SCOPE_RUNTIME.equals(a
						.getScope()))
						&& a.getGroupId().equals(CALIPER_GROUP_ID)
						&& a.getArtifactId().equals(CALIPER_ARTIFACT_ID)) {
					return true;
				}
			}
			return false;
		}
	};
	private static Predicate<Object> ALLOCATION_PREDICATE = new Predicate<Object>() {
		@Override
		public boolean apply(@Nullable Object o) {
			if (o instanceof Artifact) {
				Artifact a = (Artifact) o;
				if ("jar".equals(a.getType())) {
					try {
						JarFile jarFile = null;
						try {
							jarFile = new JarFile(a.getFile());
							Manifest manifest = jarFile.getManifest();
							if ((manifest != null)
									&& ALLOCATION_INSTRUMENTER_CLASSNAME.equals(manifest.getMainAttributes()
											.getValue("Premain-Class"))) {
								return true;
							}
						} finally {
							if (jarFile != null) {
								jarFile.close();
							}
						}
					} catch (IOException e) {
						// do nothing
					}

				}
			}
			return false;
		}
	};

	@Component
	private MavenProject project;

	/**
	 * Where to look for compiled benchmark classes.
	 */
	@Parameter(defaultValue = "${project.build.outputDirectory}")
	protected File benchmarkClassesDirectory;

	/**
	 * Maximum length of time allowed for a single trial. Use 0 to allow trials to run indefinitely.
	 */
	@Parameter(property = "timeLimit")
	protected String timeLimit;

	/**
	 * Instead of measuring, execute a single rep for each scenario.
	 */
	@Parameter(property = "dryRun")
	protected boolean dryRun;

	/**
	 * Fail build if benchmark throws an exception.
	 */
	@Parameter(property = "failBuild")
	protected boolean failBuild;

	/**
	 * Number of independent trials to peform per benchmark scenario.
	 */
	@Parameter(property = "trials")
	protected Integer trials;

	/**
	 * List of measuring instruments to use.
	 */
	@Parameter(property = "instruments")
	protected List<String> instruments;

	/**
	 * A user-friendly string used to identify the run.
	 */
	@Parameter(property = "runName")
	protected String runName;

	/**
	 * In addition to normal console output, display a raw feed of very detailed information.
	 */
	@Parameter(property = "verbose")
	protected boolean verbose;

	/**
	 * Location of Caliper's configuration file.
	 */
	@Parameter(property = "caliperConfigFile")
	protected String caliperConfigFile;

	/**
	 * Location of Caliper's configuration and data directory.
	 */
	@Parameter(property = "caliperDirectory")
	protected String caliperDirectory;

	/**
	 * Print the effective configuration that will be used by Caliper.
	 */
	@Parameter(property = "printConfig")
	protected boolean printConfig;

	/**
	 * List of VMs to test on.
	 */
	@Parameter(property = "vms")
	protected List<String> vms;

	/**
	 * Specifies a value for any property that could otherwise be specified in $HOME/.caliper/config.properties.
	 */
	@Parameter
	protected Map<String, String> properties;

	/**
	 * Specifies the values to inject into the 'param' field of the benchmark.
	 */
	@Parameter
	protected Map<String, String> params;

	/**
	 * Benchmarks to include in this run. By default all classes that begin or end with Benchmark are included.
	 */
	@Parameter(property = "includes")
	protected List<String> includes;

	/**
	 * Benchmarks to exclude from this run.
	 */
	@Parameter(property = "excludes")
	protected List<String> excludes;

	/**
	 * Run single benchmark specified by regexp.
	 */
	@Parameter(property = "benchmark")
	protected String benchmark;

	/**
	 * Java agent for allocation instrument. Plugin will look for agent on a classpath if not defined.
	 */
	@Parameter(property = "allocationAgentJar")
	private String allocationAgentJar;

	protected List<String> getDefaultIncludes() {
		return Lists.newArrayList("**/*Benchmark.java", "**/Benchmark*.java");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		checkCaliperDependency();
		ConsoleLogger console = new ConsoleLogger(System.out);
		console.info("");
		console.info("-------------------------------------------------------");
		console.info(" B E N C H M A R K S ");
		console.info("-------------------------------------------------------");
		ClassLoader benchmarkClassloader = getBenchmarkClassloader();
		Properties systemProperties = System.getProperties();
		String oldClassPath = systemProperties.getProperty("java.class.path");
		// set "java.class.path" system property as it's used by Caliper runner
		if (getLog().isDebugEnabled()) {
			getLog().debug("Using classpath: " + ClassLoaderUtils.getClassPathString(benchmarkClassloader));
		}
		systemProperties.setProperty("java.class.path",
				ClassLoaderUtils.getClassPathString(benchmarkClassloader));
		List<CaliperBenchmark> benchmarks = getBenchmarks(benchmarkClassloader);
		if (benchmarks.isEmpty()) {
			getLog().info("No benchmarks to run");
		}
		BenchmarkRunResult result = new BenchmarkRunResult();
		for (CaliperBenchmark benchmark : benchmarks) {
			console.info("\nRunning " + benchmark);
			try {
				String[] commandLineOptions = getCommandLineOptions();
				if (getLog().isDebugEnabled()) {
					getLog().debug("Command line options: " + Joiner.on(' ').join(commandLineOptions));
				}
				benchmark.run(commandLineOptions);
				result.successes++;
			} catch (Exception e) {
				String exception = "Exception was thrown while running " + benchmark;
				if (failBuild) {
					getLog().error(exception, e);
					throw new MojoFailureException(exception);
				} else {
					getLog().warn(exception, e);
					result.failures++;
				}
			}
		}
		console.info("");
		console.info(result);
		// restore old class path
		systemProperties.setProperty("java.class.path", oldClassPath);
	}

	private void checkCaliperDependency() throws MojoExecutionException {
		// check Caliper library is available
		Optional caliper = Iterables.tryFind(project.getArtifacts(), CALIPER_PREDICATE);
		if (!caliper.isPresent()) {
			throw dependencyNotFound(CALIPER_GROUP_ID, CALIPER_ARTIFACT_ID);
		}
		getLog().debug("Using Caliper library " + caliper.get());

		// get java agent for allocation instrument, Caliper won't run without it
		if (isNullOrEmpty(allocationAgentJar)) {
			Optional allocation = Iterables.tryFind(project.getArtifacts(), ALLOCATION_PREDICATE);
			if (!allocation.isPresent()) {
				throw new IllegalArgumentException("Can't find allocation agent jar on the classpath");
			}
			Artifact a = (Artifact) allocation.get();
			allocationAgentJar = a.getFile().getAbsolutePath();
		}
		File agentJar = new File(allocationAgentJar);
		if (!agentJar.isFile() || !agentJar.canRead()) {
			throw new IllegalArgumentException("Can't read agent jar " + allocationAgentJar
					+ " (check file exists and its permissions)");
		}
		getLog().debug("Using allocation library " + allocationAgentJar);
	}

	protected List<CaliperBenchmark> getBenchmarks(ClassLoader benchmarkClassloader)
			throws MojoExecutionException {
		BenchmarkDirectoryScanner scanner = new BenchmarkDirectoryScanner(benchmarkClassesDirectory);
		List<String> includes = Lists.newArrayList(getDefaultIncludes());
		if (this.includes != null) {
			includes.addAll(this.includes);
		}
		scanner.setIncludes(includes);
		if (excludes != null) {
			scanner.setExcludes(excludes);
		}
		if (benchmark != null) {
			scanner.setSpecificBenchmarks(ImmutableList.of(benchmark));
		}
		try {
			BenchmarkScanResult scan = scanner.scan();
			return scan.toBenchmarks(benchmarkClassloader);
		} catch (ClassNotFoundException e) {
			throw bug(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected ClassLoader getBenchmarkClassloader() throws MojoExecutionException {
		try {
			Collection<String> urls = project.getTestClasspathElements();
			URL[] runtimeUrls = new URL[urls.size() + 1];
			int i = 0;
			for (String url : urls) {
				runtimeUrls[i++] = new File(url).toURI().toURL();
			}
			runtimeUrls[i] = getPathToPluginJar();
			return new BenchmarkClassLoader(runtimeUrls, Thread.currentThread().getContextClassLoader());
		} catch (MalformedURLException e) {
			throw bug(e);
		} catch (DependencyResolutionRequiredException e) {
			throw bug(e);
		} catch (IOException e) {
			throw bug(e);
		}
	}

	private URL getPathToPluginJar() throws IOException {
		URL url = getClass().getResource(getClass().getSimpleName() + ".class");
		if (!"jar".equalsIgnoreCase(url.getProtocol()))
			throw new IllegalArgumentException("caliper-maven-plugin classes are not in a jar file");
		JarURLConnection connection = (JarURLConnection) url.openConnection();
		return connection.getJarFileURL();
	}

	protected String[] getCommandLineOptions() {
		List<String> options = Lists.newArrayList();
		if (!isNullOrEmpty(timeLimit)) {
			options.add("-l" + timeLimit);
		}
		if (dryRun) {
			options.add("-n");
		}
		if (trials != null) {
			options.add("-t" + trials);
		}
		if (!isNullOrEmpty(caliperConfigFile)) {
			options.add("-c" + caliperConfigFile);
		}
		if (!isNullOrEmpty(caliperDirectory)) {
			options.add("--directory" + caliperDirectory);
		}
		if (printConfig) {
			options.add("-p");
		}
		if (vms != null && !vms.isEmpty()) {
			options.add("-m" + JOINER.join(vms));
		}
		if (instruments != null && !instruments.isEmpty()) {
			options.add("-i" + JOINER.join(instruments));
		}
		if (!isNullOrEmpty(runName)) {
			options.add("-r" + runName);
		}
		if (verbose) {
			options.add("-v");
		}
		if (properties != null && !properties.isEmpty()) {
			for (Map.Entry<String, String> e : properties.entrySet()) {
				options.add("-C" + e.getKey() + "=" + e.getValue());
			}
		}
		options.add("-Cinstrument.allocation.options.allocationAgentJar=" + allocationAgentJar);
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> e : params.entrySet()) {
				options.add("-D" + e.getKey() + "=" + e.getValue());
			}
		}
		return options.toArray(new String[options.size()]);
	}

	private static MojoExecutionException bug(Exception e) throws MojoExecutionException {
		return new MojoExecutionException("Please report this problem to caliper-maven-plugin bug tracker", e);
	}

	private MojoExecutionException dependencyNotFound(String groupId, String artifactId)
			throws MojoExecutionException {
		throw new MojoExecutionException(groupId + ":" + artifactId
				+ " dependency was not found in project \"" + project.getName()
				+ "\" in compile or runtime scopes");
	}
}
