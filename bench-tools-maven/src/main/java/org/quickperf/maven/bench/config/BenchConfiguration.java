/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *
 * Copyright 2019-2020 the original author or authors.
 */

package org.quickperf.maven.bench.config;

import org.quickperf.maven.bench.projects.Maven3Version;
import org.quickperf.maven.bench.projects.TestingProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Benchmark configuration needed to run all bench tests on Apache Maven.
 *
 * <p>
 *     The configuration are loaded using differents {@link BenchConfigurationResolver} configured uppon
 *     {@link DefaultBenchConfigurationResolver}.
 * </p>
 */
public class BenchConfiguration {

    public static final BenchConfiguration INSTANCE = new BenchConfiguration();

    private BenchConfiguration() {
        initialize();
    }

    private int numberOfMeasuresByMavenVersion;

    private String exportPathOfMeasures;

    private int numberOfWarns;

    private String mavenBinariesPath;

    private TestingProject testingProject;

    private List<Maven3Version> maven3VersionsToMeasure;

    private String mavenGitVersion;

    private void initialize() {
        BenchConfigurationResolver properties = new DefaultBenchConfigurationResolver();
        String numberOfMeasuresByMavenVersionAsString = properties.resolve("measures.number-by-maven-version");
        this.numberOfMeasuresByMavenVersion = Integer.parseInt(numberOfMeasuresByMavenVersionAsString);
        String numberOfWarnsAsString = properties.resolve("warmup.number");
        this.numberOfWarns = Integer.parseInt(numberOfWarnsAsString);
        this.mavenBinariesPath = properties.resolve("maven.binaries.path");
        this.exportPathOfMeasures = properties.resolve("measures.export.path");
        this.maven3VersionsToMeasure = findMaven3VersionsToMeasure(properties);
        this.mavenGitVersion = properties.resolve("maven.git.version");
        this.testingProject = new TestingProject(
                properties.resolve("testing.project.name"),
                properties.resolve("testing.project.repository"),
                properties.resolve("testing.project.version"),
                properties.resolve("testing.project.path"));

    }

    private List<Maven3Version> findMaven3VersionsToMeasure(BenchConfigurationResolver properties) {

        List<Maven3Version> maven3VersionsToMeasure = new ArrayList<>();

        boolean mavenVersionStartFound = false;
        boolean mavenVersionToAlreadyFound = false;

        for (Maven3Version maven3Version : Maven3Version.values()) {

            if(isMaven3VersionFrom(maven3Version, properties)) {
                mavenVersionStartFound = true;
            }

            if(mavenVersionStartFound && !mavenVersionToAlreadyFound) {
                maven3VersionsToMeasure.add(maven3Version);
            }

            if(isMaven3VersionTo(maven3Version, properties)) {
                mavenVersionToAlreadyFound = true;
            }

        }

        return Collections.unmodifiableList(maven3VersionsToMeasure);

    }

    private boolean isMaven3VersionFrom(Maven3Version maven3Version, BenchConfigurationResolver properties) {
        return properties.resolve("maven.version.from").equals(maven3Version.getNumVersion());
    }

    private boolean isMaven3VersionTo(Maven3Version maven3Version, BenchConfigurationResolver properties) {
        return properties.resolve("maven.version.to").equals(maven3Version.getNumVersion());
    }

    public int getNumberOfMeasuresByMavenVersion() {
        return numberOfMeasuresByMavenVersion;
    }

    public String getExportPathOfMeasures() {
        return exportPathOfMeasures;
    }

    public int getNumberOfWarms() {
        return numberOfWarns;
    }

    public String getMavenBinariesPath() {
        return mavenBinariesPath;
    }

    public TestingProject getTestingProject() {
        return testingProject;
    }

    public List<Maven3Version> getMaven3VersionsToMeasure() {
        return maven3VersionsToMeasure;
    }

    public String getMavenGitVersion() {
        return mavenGitVersion;
    }
}
