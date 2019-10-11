
SHELL := /bin/bash # Use bash syntax
THIS_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
PARENT_DIR := target/test-classes/maven
APACHE_MAVEN_MASTER_DIR := $(THIS_DIR)$(PARENT_DIR)/apache-maven-master
APACHE_MAVEN_BUILT_TO_TEST_DIR := $(PARENT_DIR)/apache-maven-head
MVN_VERSION := $(shell mvn -Dexec.executable='echo' -Dexec.args='$${project.version}' --non-recursive exec:exec -q -f $(APACHE_MAVEN_MASTER_DIR)/pom.xml)

.PHONY: test

build:
	mvn clean package

test: build test.nonRegMvn

maven.clone:
	$(shell [[ -d $(APACHE_MAVEN_MASTER_DIR) ]] || ( \
			mkdir -p $(PARENT_DIR) \
			&& git clone https://gitbox.apache.org/repos/asf/maven.git $(APACHE_MAVEN_MASTER_DIR)) \
	)

maven.build:
	mvn clean package -DskipTests -B -f $(APACHE_MAVEN_MASTER_DIR)/pom.xml

test.settings:
	tar xfz $(APACHE_MAVEN_MASTER_DIR)/apache-maven/target/apache-maven-$(MVN_VERSION)-bin.tar.gz -C $(PARENT_DIR)
	mv $(PARENT_DIR)/apache-maven-$(MVN_VERSION) $(APACHE_MAVEN_BUILT_TO_TEST_DIR)

test.nonRegMvn: maven.clone maven.build test.settings
	mvn test -Dtest=org.quickperf.maven.bench.head.MvnValidateMaxAllocation \
			 -Dproject-under-test.path=$(pwd)/target/test-classes/camel \
			 -Dmaven.binaries.path=$(pwd)/target/test-classes/maven

