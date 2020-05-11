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

import java.util.*;

public class BenchProperties {

    public static final BenchProperties INSTANCE = new BenchProperties();

    private BenchProperties() {
        initializeProperties();
    }

    private int numberOfMeasuresByMavenVersion;

    private String exportPathOfMeasures;

    private int numberOfWarns;

    private String mavenBinariesPath;

    private TestingProject testingProject;

    private List<Maven3Version> maven3VersionsToMeasure;

    private void initializeProperties() {
        BenchPropertiesResolver properties  = new DefaultBenchPropertiesResolver();
        String numberOfMeasuresByMavenVersionAsString = properties.getProperty("measures.number-by-maven-version");
        this.numberOfMeasuresByMavenVersion = Integer.parseInt(numberOfMeasuresByMavenVersionAsString);
        String numberOfWarnsAsString = properties.getProperty("warmup.number");
        this.numberOfWarns = Integer.parseInt(numberOfWarnsAsString);
        this.mavenBinariesPath = properties.getProperty("maven.binaries.path");
        this.exportPathOfMeasures = properties.getProperty("measures.export.path");
        this.maven3VersionsToMeasure = findMaven3VersionsToMeasure(properties);

        this.testingProject = new TestingProject(
                properties.getProperty("testing.project.name"),
                properties.getProperty("testing.project.repository"),
                properties.getProperty("testing.project.version"),
                properties.getProperty("testing.project.path"));

    }

    private List<Maven3Version> findMaven3VersionsToMeasure(BenchPropertiesResolver properties) {

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

    private boolean isMaven3VersionFrom(Maven3Version maven3Version, BenchPropertiesResolver properties) {
        return properties.getProperty("maven.version.from").equals(maven3Version.getNumVersion());
    }

    private boolean isMaven3VersionTo(Maven3Version maven3Version, BenchPropertiesResolver properties) {
        return properties.getProperty("maven.version.to").equals(maven3Version.getNumVersion());
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
}
