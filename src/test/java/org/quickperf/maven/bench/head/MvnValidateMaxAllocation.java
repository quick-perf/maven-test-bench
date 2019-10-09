package org.quickperf.maven.bench.head;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.runner.RunWith;
import org.quickperf.junit4.QuickPerfJUnitRunner;
import org.quickperf.jvm.allocation.AllocationUnit;
import org.quickperf.jvm.annotations.ExpectMaxHeapAllocation;
import org.quickperf.jvm.annotations.HeapSize;
import org.quickperf.jvm.annotations.MeasureHeapAllocation;
import org.quickperf.maven.bench.config.BenchProperties;
import org.quickperf.maven.bench.projects.Maven3Version;
import org.quickperf.maven.bench.projects.ProjectUnderTest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MvnValidateMaxAllocation {

	@RunWith(QuickPerfJUnitRunner.class)
	public static class MvnValidate {

		private final ProjectUnderTest apacheCamelProject = new ProjectUnderTest(
				BenchProperties.INSTANCE.getPathOfProjectUnderTest(),
				"https://github.com/apache/camel/archive/camel-2.23.4.zip"
		);

		private Verifier verifier;

		private static final Maven3Version CURRENT_MAVEN_HEAD = Maven3Version.valueOf("HEAD");

		private final List<String> validate = Collections.singletonList("validate");

		@Before
		public void before() throws VerificationException {
			System.setProperty("verifier.forkMode", "auto"); // embedded
			System.setProperty("maven.home", CURRENT_MAVEN_HEAD.getMavenPath());
			if (apacheCamelProject.isNotAlreadyInstalled()) {
				try {
					apacheCamelProject.installProject();
				} catch (IOException mavenProjectUnderTestNotInstallEx) {
					throw new IllegalStateException(mavenProjectUnderTestNotInstallEx);
				}
			}

			final String projectDirectoryPath = apacheCamelProject.getPath();
			verifier = new Verifier(projectDirectoryPath);
			verifier.setSystemProperty("maven.multiModuleProjectDirectory", projectDirectoryPath);
		}

		@HeapSize(value = 1, unit = AllocationUnit.GIGA_BYTE)
		@ExpectMaxHeapAllocation(value = 3.2, unit = AllocationUnit.GIGA_BYTE)
		@Test
		public void execute_maven_validate() throws VerificationException {
			verifier.executeGoals(validate);
		}

	}

	@Test
	public void verify_heap_allocation_of_mvn_validate() {

		// GIVEN
		Class<?> testClass = MvnValidate.class;

		// WHEN
		PrintableResult printableResult = PrintableResult.testResult(testClass);
		// THEN
		if(printableResult.failureCount() != 0) {
			System.out.println("Allocation can't be measured. " + printableResult.toString());
			Assert.fail();
		}
		//  En cas d'échec cela serait pas mal d'afficher le message d'erreur
		// du rapport de test : printableResult.toString()

	}


}
