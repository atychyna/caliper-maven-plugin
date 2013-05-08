# Installing

Add following XML to your <HOME>/.m2/settings.xml

```xml
<pluginGroups>
    <pluginGroup>org.atychyna</pluginGroup>
<pluginGroups>
````

#Using

You need to have Caliper declared as a dependency in runtime or compile scope of your project.

To run single benchmark use `mvn -Dbenchmark=*Benchmark* caliper:run`

You can also run your benchmarks as a part of a build.

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

### printConfig

Print the effective configuration that will be used by Caliper.