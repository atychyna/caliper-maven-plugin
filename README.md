# Installing

Add following XML to your *$HOME/.m2/settings.xml* if you want to be able to use shortened commands like `mvn caliper:run`.

```xml
<pluginGroups>
    <pluginGroup>org.atychyna</pluginGroup>
<pluginGroups>
````

#Using

You need to have Caliper declared as a dependency in runtime or compile scope of your project (plugin's dependencyResolution = **COMPILE_PLUS_RUNTIME**).

To run single benchmark use `mvn -Dbenchmark=**/SpecificBenchmark* caliper:run`

You can also run your benchmarks as a part of a build. By default plugin binds to **VERIFY** lifecycle phase.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.atychyna</groupId>
            <artifactId>caliper-maven-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
            <executions>
                <execution>
                    <id>run</id>
                    <goals>
                        <goal>run</goal>
                    </goals>
                    <configuration>
                    <!-- see possible configuration options below -->
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## Configurations options

#### benchmarkClassesDirectory
* type: *String*
* default: *${project.build.outputDirectory}*

Where to look for compiled benchmark classes.

#### timeLimit
* type: *String*
* default: *30s*

Maximum length of time allowed for a single trial. Use 0 to allow trials to run indefinitely.

#### dryRun
* type: *boolean*
* default: *false*

Instead of measuring, execute a single rep for each scenario.

#### failBuild
* type: *boolean*
* default: *false*

Fail build if benchmark throws an exception.

#### trials
* type: *Integer*
* default: *1*

Number of independent trials to peform per benchmark scenario.

#### instruments
* type: *List&lt;String&gt;*
* default: *Caliper's default*

List of measuring instruments to use.

#### runName
* type: *String*

A user-friendly string used to identify the run.

#### verbose
* type: *boolean*
* default: *false*

In addition to normal console output, display a raw feed of very detailed information.

#### caliperConfigFile
* type: *String*
* default: *$HOME/.caliper/config.properties*

Location of Caliper's configuration file.

#### caliperDirectory
* type: *String*
* default: *$HOME/.caliper*

Location of Caliper's configuration and data directory.

#### printConfig
* type: *boolean*
* default: *false*

Print the effective configuration that will be used by Caliper.

#### vms
* type: *List&lt;String&gt;*
* default: *Caliper's default*

List of VMs to test on.

```xml
<!-- example:
if ~/.caliper/config.properties contains:

vm.jdk6.home=/path/to/jdk6
vm.jdk7.home=/path/to/jdk7
 -->
<vms>
    <vm>jdk6</vm>
    <vm>jdk7</vm>
</vms>
```

#### properties
* type: *List&lt;String&gt;*

Specifies a value for any property that could otherwise be specified in *$HOME/.caliper/config.properties*.

```xml
<properties>
    <vm.jdk6.home>/path/to/jdk6</vm.jdk6.home>
    <vm.jdk7.home>/path/to/jdk7</vm.jdk7.home>
</properties>
```

#### params
* type: *List&lt;String&gt;*

Specifies the values to inject into the 'param' field of the benchmark.

```xml
<params>
    <length>1,2,3</length>
    <width>2</width>
</params>
```

#### includes
* type: *List&lt;String&gt;*

Benchmarks to include in this run. By default all classes that begin or end with *Benchmark* are included.

```xml
<includes>
    <include>**/Bench*</include>
</includes>
```

#### excludes
* type: *List&lt;String&gt;*

Benchmarks to exclude from this run.

```xml
<excludes>
    <exclude>**/DoNotRun*</exclude>
</excludes>
```
