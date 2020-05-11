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

package org.quickperf.maven.bench;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quickperf.junit4.QuickPerfJUnitRunner;
import org.quickperf.jvm.allocation.AllocationUnit;
import org.quickperf.jvm.annotations.HeapSize;
import org.quickperf.jvm.annotations.ProfileJvm;
import org.quickperf.maven.bench.commands.InstallMavenVersionIfNotExists;
import org.quickperf.maven.bench.projects.TestingProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quickperf.maven.bench.config.BenchProperties;
import org.quickperf.maven.bench.projects.Maven3Version;

import java.util.Collections;
import java.util.List;

@RunWith(QuickPerfJUnitRunner.class)
public class MvnValidateProfilingTest {
	
    private Logger logger = LoggerFactory.getLogger(MvnValidateProfilingTest.class);

    public static Maven3Version MAVEN_3_VERSION = Maven3Version.V_3_2_5;

    private final TestingProject apacheCamelProject = BenchProperties.INSTANCE.getTestingProject();

    private Verifier verifier;

    private final List<String> validate = Collections.singletonList("validate");

    @ProfileJvm
    @HeapSize(value = 6, unit = AllocationUnit.GIGA_BYTE)
    @Test
    public void execute_maven_validate() throws VerificationException {
    	logger.debug("execute_maven_validate - start");
        verifier.executeGoals(validate);
        logger.debug("execute_maven_validate - end");
    }

    @Before
    public void before() throws VerificationException {
        if (apacheCamelProject.isNotAlreadyInstalled()) {
            try {
                apacheCamelProject.installProject();
            } catch (IllegalStateException mavenProjectUnderTestNotInstallEx) {
                throw new IllegalStateException(mavenProjectUnderTestNotInstallEx);
            }
        }

        new InstallMavenVersionIfNotExists(MAVEN_3_VERSION).execute();

        String mavenPath = MAVEN_3_VERSION.getMavenPath();
        System.setProperty("verifier.forkMode", "auto"); // embedded
        System.setProperty("maven.home", mavenPath);

        verifier = new Verifier(apacheCamelProject.getPath());
        verifier.setSystemProperty("maven.multiModuleProjectDirectory", apacheCamelProject.getPath());
    }

}
