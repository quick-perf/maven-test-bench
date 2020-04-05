SHELL := /bin/bash # Use bash syntax

.PHONY: runMeasureOnHead runTestNonRegMvn

clean:
	mvn clean

build:
	mvn package -B

runTestNonRegMvn:
	mvn test -Dtest=org.quickperf.maven.bench.head.MvnValidateMaxAllocation -B

runMeasureOnHead:
	@echo "maven.version.from=head" > src/test/resources/local.maven-bench.properties
	@echo "maven.version.to=head" >> src/test/resources/local.maven-bench.properties
	mvn test -Dtest=org.quickperf.maven.bench.MvnValidateAllocationByMaven3VersionTest -B
