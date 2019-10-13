
SHELL := /bin/bash # Use bash syntax
THIS_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
TESTING_DIR := $(THIS_DIR)target/test-classes

APACHE_MAVEN_DIR := $(TESTING_DIR)/maven
APACHE_MAVEN_MASTER_DIR := $(APACHE_MAVEN_DIR)/apache-maven-master
APACHE_MAVEN_BUILT_TO_TEST_DIR := $(APACHE_MAVEN_DIR)/apache-maven-head

APACHE_CAMEL_DIR := $(TESTING_DIR)/camel

.PHONY: test

build:
	mvn clean package

test: build test.nonRegMvn

maven.clone:
	$(shell [[ -d $(APACHE_MAVEN_MASTER_DIR) ]] || ( \
			mkdir -p $(APACHE_MAVEN_DIR) \
			&& git clone https://gitbox.apache.org/repos/asf/maven.git $(APACHE_MAVEN_MASTER_DIR)) \
	)

maven.build:
	mvn clean package -DskipTests -B -f $(APACHE_MAVEN_MASTER_DIR)/pom.xml

test.settings:
	$(eval $@_MVN_VERSION := $(shell mvn -Dexec.executable='echo' -Dexec.args='$${project.version}' --non-recursive exec:exec -q -f $(APACHE_MAVEN_MASTER_DIR)/pom.xml))
	tar xfz $(APACHE_MAVEN_MASTER_DIR)/apache-maven/target/apache-maven-$($@_MVN_VERSION)-bin.tar.gz -C $(APACHE_MAVEN_DIR)
	mv -n $(APACHE_MAVEN_DIR)/apache-maven-$($@_MVN_VERSION) $(APACHE_MAVEN_BUILT_TO_TEST_DIR)

test.runNonRegMvn:
	export TESTING_PROJECT_PATH=$(APACHE_CAMEL_DIR) \
	&& export MAVEN_BINARIES_PATH=$(APACHE_MAVEN_DIR) \
	&& mvn test -Dtest=org.quickperf.maven.bench.head.MvnValidateMaxAllocation

test.nonRegMvn: maven.clone maven.build test.settings test.runNonRegMvn