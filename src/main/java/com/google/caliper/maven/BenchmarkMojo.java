package com.google.caliper.maven;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Run Caliper benchmarks.
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.VERIFY, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class BenchmarkMojo extends AbstractMojo {
	public static final Joiner JOINER = Joiner.on(',');
	public static final String CALIPER_GROUP_ID = "com.google.caliper";
	public static final String CALIPER_ARTIFACT_ID = "caliper";

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	@Parameter(defaultValue = "${project.build.outputDirectory}")
	protected File benchmarkClassesDirectory;

	@Parameter
	protected String timeLimit;

	@Parameter
	protected boolean dryRun;

	@Parameter
	protected boolean failBuild;

	@Parameter
	protected Integer trials;

	@Parameter
	protected String instrument;

	@Parameter
	protected String runName;

	@Parameter
	protected boolean verbose;

	@Parameter
	protected String caliperConfigFile;

	@Parameter
	protected String caliperDirectory;

	@Parameter
	protected boolean printConfig;

	@Parameter
	protected List<String> vms;

	@Parameter
	protected Map<String, String> properties;

	@Parameter
	protected Map<String, String> params;

	@Parameter
	protected List<String> includes;

	@Parameter
	protected List<String> excludes;

	@Parameter(property = "benchmark")
	protected String benchmark;

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
		for (Object o : project.getArtifacts()) {
			Artifact a = (Artifact) o;
			if ((Artifact.SCOPE_COMPILE.equals(a.getScope()) || Artifact.SCOPE_RUNTIME.equals(a.getScope()))
					&& a.getGroupId().equals(CALIPER_GROUP_ID)
					&& a.getArtifactId().equals(CALIPER_ARTIFACT_ID)) {
				getLog().debug("Using Caliper library " + a);
				return;
			}
		}
		throw new MojoExecutionException(CALIPER_GROUP_ID + ":" + CALIPER_ARTIFACT_ID
				+ " dependency was not found in project \"" + project.getName()
				+ "\" in compile or runtime scopes");
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
		if (timeLimit != null) {
			options.add("-l " + timeLimit);
		}
		if (dryRun) {
			options.add("-n");
		}
		if (trials != null) {
			options.add("-t " + trials);
		}
		if (caliperConfigFile != null) {
			options.add("-c " + caliperConfigFile);
		}
		if (caliperDirectory != null) {
			options.add("--directory " + caliperDirectory);
		}
		if (printConfig) {
			options.add("-p");
		}
		if (vms != null) {
			options.add("-m " + JOINER.join(vms));
		}
		if (instrument != null) {
			options.add("-i " + instrument);
		}
		if (runName != null) {
			options.add("-r " + runName);
		}
		if (verbose) {
			options.add("-v");
		}
		if (properties != null) {
			for (Map.Entry<String, String> e : properties.entrySet()) {
				options.add("-C" + e.getKey() + "=" + e.getValue());
			}
		}
		if (params != null) {
			for (Map.Entry<String, String> e : params.entrySet()) {
				options.add("-D" + e.getKey() + "=" + e.getValue());
			}
		}
		return options.toArray(new String[options.size()]);
	}

	private static MojoExecutionException bug(Exception e) throws MojoExecutionException {
		return new MojoExecutionException("Please report this problem to caliper-maven-plugin bug tracker", e);
	}
}
