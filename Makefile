SHELL := /bin/bash # Use bash syntax

.PHONY: help build runMeasures runValidateMaxAllocation

help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

clean: 						## Cleanup project files (basically run `mvn clean`)
	mvn clean

build: 						## Build project with running all tests (basically run `mvn package`)
	mvn package -B

ci:
	mvn package -B -P ci

install-only-bench-tools: 	## Install locally all tools lib necessary to run maven perf tests.
	mvn install -pl bench-tools-maven -am -B

runValidateMaxAllocation: install-only-bench-tools	## Running only memory allocation needed for last commit from Maven GIT Repository on master branch
	mvn test -pl maven-perf -Dtest=org.quickperf.maven.bench.head.MvnValidateMaxAllocation -B

runMeasures: install-only-bench-tools	## Running only measures
	mvn test -pl maven-perf -Dtest=org.quickperf.maven.bench.MvnValidateAllocationByMaven3VersionTest -B
