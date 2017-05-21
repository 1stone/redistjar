# RedistJar Maven Plugin

This is a simple Maven plugin, that allows you to distribute pre-build JAR files
to your preferred Maven repository in a more controlled fashion then you would
do that with `mvn install:install-file ...`.

It behaves pretty much like the well-known [Maven Jar Plugin](https://maven.apache.org/plugins/maven-jar-plugin/), which generates a jar artifact, but with the difference, that the default lifecycle is pretty much reduced to the package, install and deploy phases.


## Getting Started

To use this plugin, just clone the repository and install the plugin bits with

    mvn install

This should install you a copy of the plugin in your local maven repository.

## Create RedistJar Project

With the plugin installed, you can now create a new Maven Project for wrapping your pre-build jar you like to redistribute.
For the following let's call that `MyPrebuild.jar`.

Create a new maven project with a `pom.xml` that has this components:

Make sure to use the jar build lifecycle:

    <packaging>jar</packaging>

Add any dependencies your JAR requires to run.
In your example we require `commons-codec` and `commons-httpclient`. This will insure, that any project depending on your redistributed artifact is pulling the required libraries automatically.

    <dependencies>
      <dependency>
        <groupId>commons-codec</groupId>
        <artifactId>commons-codec</artifactId>
        <version>1.3</version>
        <scope>runtime</scope>
      </dependency>
      <dependency>
        <groupId>commons-httpclient</groupId>
        <artifactId>commons-httpclient</artifactId>
        <version>3.1</version>
        <scope>runtime</scope>
      </dependency>
    </dependencies>

Finally, let maven know about the redistjar plugin that will replace the jar default lifecycle.
Make sure to specify the location of your jar file in the configuration.

    <build>
      <plugins>     
        <plugin>
          <groupId>maven.plugin</groupId>
          <artifactId>pseudojar-maven-plugin</artifactId>
          <version>1.0-SNAPSHOT</version>
          <extensions>true</extensions>
          <configuration>
            <jarFile>${basedir}/src/main/lib/myCustom.jar</jarFile>
          </configuration>
        </plugin>
      </plugins>
    </build>

## Build your RedistJar Project

With that `pom.xml` and the `MyCustom.jar` file in the specified location, you are ready to run.

Install the jar with a proper pom.xml in your repository:

    mvn install

## Notes

Effectively, all this has the same result as if you would have prepared a proper `pom.xml` (with dependencies!) and installed the jar manually with `mvn install:install-file ...` (following a ton of parameters).

The problem with that is, you have to document all your steps somewhere, or put them in a batch file, to make that process reproducable later somehow.

Bundling this into a nice and version-controlled maven project, makes this much more user-friendly.

## Contributing

Any contributions are welcome.

## Authors

* **Joerg Delker** - *Initial work* - [1stone](https://github.com/1stone)

See also the list of [contributors](https://github.com/1stone/redistjar/graphs/contributors) who participated in this project.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details
