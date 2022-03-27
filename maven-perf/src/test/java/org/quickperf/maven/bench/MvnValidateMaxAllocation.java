package org.quickperf.maven.bench;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.runner.RunWith;
import org.quickperf.junit4.QuickPerfJUnitRunner;
import org.quickperf.jvm.allocation.AllocationUnit;
import org.quickperf.jvm.annotations.ExpectMaxHeapAllocation;
import org.quickperf.jvm.annotations.HeapSize;
import org.quickperf.jvm.annotations.MeasureHeapAllocation;
import org.quickperf.maven.bench.commands.InstallMavenVersionIfNotExists;
import org.quickperf.maven.bench.config.BenchConfiguration;
import org.quickperf.maven.bench.projects.Maven3Version;
import org.quickperf.maven.bench.projects.TestingProject;
import org.quickperf.writer.WriterFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

public class MvnValidateMaxAllocation {

	@RunWith(QuickPerfJUnitRunner.class)
	public static class MvnValidate {

		private final TestingProject apacheCamelProject = BenchConfiguration.INSTANCE.getTestingProject();

		private Verifier verifier;
		private final List<String> validate = Collections.singletonList("validate");

		@Before
		public void before() throws VerificationException {
			final var mavenVersion = BenchConfiguration.INSTANCE.getMavenGitVersion();
			final Maven3Version version = Maven3Version.parse(mavenVersion);
			new InstallMavenVersionIfNotExists(version).execute();
			System.setProperty("verifier.forkMode", "auto"); // embedded
			System.setProperty("maven.home", version.getMavenPath());
			if (apacheCamelProject.isNotAlreadyInstalled()) {
				try {
					apacheCamelProject.installProject();
				} catch (IllegalStateException mavenProjectUnderTestNotInstallEx) {
					throw new IllegalStateException(mavenProjectUnderTestNotInstallEx);
				}
			}

			final String projectDirectoryPath = apacheCamelProject.getPath();
			verifier = new Verifier(projectDirectoryPath);
			verifier.setSystemProperty("maven.multiModuleProjectDirectory", projectDirectoryPath);
		}

		@Test
		@HeapSize(value = 4, unit = AllocationUnit.GIGA_BYTE)
		@ExpectMaxHeapAllocation(value = 4.50, unit = AllocationUnit.GIGA_BYTE)
		@MeasureHeapAllocation(writerFactory = AllocationExporter.class, format = "%s")
		public void execute_maven_validate() throws VerificationException {
			verifier.executeGoals(validate);
		}

		public static class AllocationExporter implements WriterFactory {

			@Override
			public Writer buildWriter() throws IOException {
				return new FileWriter("./target/Allocation.txt");
			}

		}


	}

	@Test
	public void verify_heap_allocation_of_mvn_validate() {

		// GIVEN
		Class<?> testClass = MvnValidate.class;

		// WHEN
		PrintableResult printableResult = PrintableResult.testResult(testClass);

		// THEN
		if(testHasFailed(printableResult)) {
			String junit4ErrorReport = printableResult.toString();
			String errorMessage = buildErrorMessage(junit4ErrorReport);
			throw new AssertionError(errorMessage
					+ System.lineSeparator()
					+ junit4ErrorReport);

		}

	}

	private String buildErrorMessage(String junit4ErrorReport) {
		if(isAllocationIssue(junit4ErrorReport)) {
			return "Allocation greater than expected.";
		}
		return  "Unexpected failure.";
	}

	private boolean isAllocationIssue(String junit4ErrorReport) {
		return junit4ErrorReport.contains("Expected heap allocation");
	}

	private boolean testHasFailed(PrintableResult printableResult) {
		return printableResult.failureCount() != 0;
	}

}
