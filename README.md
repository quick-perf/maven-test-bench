<div align="center">
<blockquote>
<p><h3>This project is a test bench to measure and investigate heap allocation of Apache Maven.</h3></p>
</blockquote>
</div>

<p align="center">
  <a href="#General-setup">General setup</a> •
  <a href="#Benchmark-heap-allocation-of-several-Maven-releases">Benchmark heap allocation of several Maven releases</a> 
</p>
<p align="center">
<a href="#Investigate-where-heap-allocation-comes-from">Investigate where heap allocation comes from</a>  •
  <a href="#perspectives">Perspectives</a> •
  <a href="#Contributors">Contributors</a> •
  <a href="#License">License</a> 
</p>

At this moment, this project is a test bench based on [QuickPerf](https://github.com/quick-perf/quickperf) to benchmark and understand heap allocation caused by `mvn validate` (the first phase before launching any plugin: see [Lifecycles Reference](https://maven.apache.org/ref/current/maven-core/lifecycles.html)).

Measures have been done with Apache Camel source code as a target project to execute Maven.

Feel free to use this project and contribute to it!

# General setup

This project contains two types of test:
- `org.quickperf.maven.bench.MvnValidateAllocationByMaven3VersionTest` can be used to evaluate the heap allocation level for a range of Maven versions,
- for a given Maven version, `org.quickperf.maven.bench.MvnValidateProfilingTest` can be used to investigate the origin of allocation.

This general setup part describes configurations common to both tests.

You have to define values to the `testing.project.path` and `maven.binaries.path` properties contained in the `maven-bench.properties` file. The other properties are only used by `org.quickperf.maven.bench.MvnValidateAllocationByMaven3VersionTest`.

The `testing.project.path` represents the path of the project on which `mvn validate` will be applied. 
Our next measures are based on the Apache Camel project, but you can choose your own target. For reproducibility of our measure, a precisely defined version of this project was chosen:
```
git clone -n https://github.com/apache/camel.git
git checkout c409ab7aabb971065fc8384a861904d2a2819be5
```
This Apache Camel version contains 841 modules: such a huge build is perfect to get significant measures.

The `maven.binaries.path` property corresponds to the path where the needed Maven distributions will be automatically downloaded by the tests. Downloads are performed during *@Before* execution.
If you want to apply measures on Maven HEAD, you can execute the following commands where {maven-distrib-location} has to be replaced with the url given by the `maven.binaries.path` property of `maven-bench.properties` file:
```
git clone https://github.com/apache/maven.git
cd maven
mvn -DdistributionTargetDir="{maven-distrib-location}/apache-maven-head" clean package
``` 

Heap size is fixed with the help of [@HeapSize](https://github.com/quick-perf/doc/wiki/JVM-annotations#heapsize).

# Benchmark heap allocation of several Maven releases

`org.quickperf.maven.bench.MvnValidateAllocationByMaven3VersionTest` test allows to benchmark the heap allocation level on several Maven 3 distributions.

Heap allocation level is measured with the help of [@MeasureHeapAllocation](https://github.com/quick-perf/doc/wiki/JVM-annotations#Verify-heap-allocation) QuickPerf annotation. This annotation measures the heap allocation level of the thread running the method annotated with @Test.
Feel free to contribute to QuickPerf by adding a feature allowing to measure the allocation level aggregated across all the threads! With `mvn validate`, we have checked that Maven code is not multithreaded during this validate phase by profiling the JVM with the help of [@ProfileJvm](https://github.com/quick-perf/doc/wiki/JVM-annotations#ProfileJvm).

Please read [General setup](#General-setup) to get some of the setup requirements.

You also have to give a value for the following properties contained in the [maven-bench.properties](src/test/resources/maven-bench.properties) file:
* `maven.version.from`
* `maven.version.to`
* `warmup.number`
* `measures.number-by-maven-version`

The meaning of these properties is given in the [maven-bench.properties](src/test/resources/maven-bench.properties) file.

Measures can be launched with this command line: ```mvn -Dtest=org.quickperf.maven.bench.MvnValidateAllocationByMaven3VersionTest test```.
Before doing it, you can close your IDE, web browser or other applications to free memory.

The benchmark results are exported into a `maven-memory-allocation-{date-time}.csv` file. The execution context (processor, OS, ...) is reported in an `execution-context-{date-time}.txt` file.

For several Maven versions, the following graph gives the average of ten heap allocations caused by the application of `mvn validate` on Apache Camel:
<p align="center">
    <img src="measures/mvn-validate-on-camel.png">
</p>

For this graph, you can consult:
* [the measures](measures/maven-memory-allocation-2019-09-01-18-48-41.csv)
* [the execution context](measures/execution-context-2019-09-01-18-48-41.txt)

Measures took around one hour and a quarter. 

From Maven versions 3.2.5 to 3.6.2, heap allocation level is the highest with Maven 3.2.5 and the smallest with Maven 3.6.2. *The heap allocation decreases from ~7 Gb with Maven 3.6.1 to ~3 Gb with Maven 3.6.2*.

Control and reduce heap allocation is an important matter for Maven project. Indeed, a part of the heap allocation is going to be garbage collected and the garbage collection activity is succeptible to slow down your build. In addition, less heap allocation means that you may execute Maven with a smaller heap size.

But where the allocation comes from? In the following part we will see how to spot the Java methods allocating a lot.

# Investigate where heap allocation comes from

You can use `org.quickperf.maven.bench.MvnValidateProfilingTest` to understand the origin of heap allocation.
Some of the set up requirements can be found in [General setup](#General-setup) part.

The Maven version under test can be set with the `MAVEN_3_VERSION` constant:
``` java
    public static org.quickperf.maven.bench.projects.Maven3Version MAVEN_3_VERSION = org.quickperf.maven.bench.projects.Maven3Version.V_3_6_2;
```

A test method is annotated with [@ProfileJvm](https://github.com/quick-perf/doc/wiki/JVM-annotations#Profile-or-check-your-JVM) to profile the test method with Java Flight Recorder (JFR).

The JFR file location is going to be displayed in the console:
```
[QUICK PERF] JVM was profiled with Java File Recorder (JFR).
The recording file can be found here: C:\Users\JEANBI~1\AppData\Local\Temp\QuickPerf-46868616\jvm-profiling.jfr
You can open it with Java Mission Control (JMC).
```

You can open it with Java Mission Control (JMC) to discover the methods contributing the most to heap allocation. 

Below a JFR file for Maven 3.2.5 and opened with JMC 5.5:
<p align="center">
    <img src="measures/Maven3.2.5-JMC.5.5JPG.jpg">
</p>


By the way, you can also benefit from an automatic performance analysis with [@ExpectNoJvmIssue](https://github.com/quick-perf/doc/wiki/JVM-annotations#ExpectNoJvmIssue).
For example, the following warning is reported with Maven 3.2.5:
```
Rule: Thrown Exceptions
Severity: WARNING
Score: 97
Message: The program generated 20 482 exceptions per second during 26,722 s starting at 
03/09/19 17:08:31.
```

# Perspectives
We have developed a test bench that is able to compare the heap allocation level between several Maven versions. We also have given a method to understand the origin of heap allocation.

Feel free to play with this bench and [QuickPerf](https://github.com/quick-perf/doc/wiki/QuickPerf), to perform measures (heap allocation, execution time, ...) with different plugins/goals, use different JDK or garbage collectors, ..., suggest new ideas, create new features or share your measures with PR!
Some issues are also available [here](https://github.com/quick-perf/maven-test-bench/issues)!

You also have [QuickPerf issues](https://github.com/quick-perf/quickperf/issues) to build new performance tools!

# Contributors
Many thanks to all our contributors!

<table>
    <tr>
        <td align="center">
            <a href="https://github.com/jeanbisutti">
                <img src="https://avatars1.githubusercontent.com/u/14811066?v=4" width="100px;" alt="Jean Bisutti"/>
                <br/>
                <sub><b>Jean Bisutti</b></sub>
            </a>
            <br/>
            <a href="https://github.com/quick-perf/maven-test-bench/commits?author=jeanbisutti" title="Ideas">🤔</a>
            <a href="https://github.com/quick-perf/maven-test-bench/commits?author=jeanbisutti" title="Code">💻</a>
            <a href="https://github.com/quick-perf/maven-test-bench/commits?author=jeanbisutti" title="Documentation">📖</a>
            <a href="https://github.com/quick-perf/maven-test-bench/commits?author=jeanbisutti" title="Reviewed Pull Requests">👀</a>
        </td>       
        <td align="center">
            <a href="https://github.com/hboutemy">
                <img src="https://avatars1.githubusercontent.com/u/237462?v=4" width="100px;" alt="Hervé Boutemy"/>
                <br/>
                <sub><b>Hervé Boutemy</b></sub>
            </a>
            <br/>
            <a href="https://github.com/quick-perf/maven-test-bench/commits?author=hboutemy" title="Ideas">🤔</a>
            <a href="https://github.com/quick-perf/maven-test-bench/commits?author=hboutemy" title="Documentation">📖</a>
        </td>        
        <td align="center">
            <a href="https://github.com/albertotn">
                <img src="https://avatars1.githubusercontent.com/u/12526457?v=4" width="100px;" alt="Alberto Martinelli"/>
                <br/>
                <sub><b>Alberto Martinelli</b></sub>
            </a>
            <br/>
            <a href="https://github.com/quick-perf/maven-test-bench/commits?author=albertotn" title="Code">💻</a>
        </td>
    </tr>
</table>
<a href = "https://allcontributors.org/docs/en/emoji-key">emoji key</a>

# License
[Apache License 2.0](/LICENSE.txt)