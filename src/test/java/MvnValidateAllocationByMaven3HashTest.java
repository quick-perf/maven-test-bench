import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.runner.RunWith;
import org.quickperf.junit4.QuickPerfJUnitRunner;
import org.quickperf.jvm.allocation.AllocationUnit;
import org.quickperf.jvm.annotations.HeapSize;
import org.quickperf.jvm.annotations.MeasureHeapAllocation;
import org.quickperf.repository.LongFileRepository;
import org.quickperf.repository.ObjectFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import sun.awt.OSInfo;
import sun.awt.OSInfo.OSType;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.experimental.results.PrintableResult.testResult;

public class MvnValidateAllocationByMaven3HashTest {

    private Logger logger = LoggerFactory.getLogger(MvnValidateAllocationByMaven3HashTest.class);
	
	@RunWith(QuickPerfJUnitRunner.class)
	public static class MvnValidate {

		private final String pathOfMavenProjectUnderTest = BenchProperties.INSTANCE.getPathOfProjectUnderTest();

		private Verifier verifier;

		private final List<String> validate = Collections.singletonList("validate");

		@Before
		public void before() throws VerificationException, IOException {

			String mavenBuiltPath = System.getProperty("maven.built.home");
			
			System.out.println(mavenBuiltPath);
			System.setProperty("verifier.forkMode", "auto"); // embedded
			System.setProperty("maven.home", mavenBuiltPath);
			verifier = new Verifier(pathOfMavenProjectUnderTest);
			verifier.setSystemProperty("maven.multiModuleProjectDirectory", pathOfMavenProjectUnderTest);

		}

		@HeapSize(value = 6, unit = AllocationUnit.GIGA_BYTE)
		@MeasureHeapAllocation
		@Test
		public void execute_maven_validate() throws VerificationException {
			verifier.executeGoals(validate);
		}

	}

	public static final String MAVEN_3_VERSION_FILE_NAME = "Maven3Version";

	private final String tempDirPath = System.getProperty("java.io.tmpdir");

	private final FilenameFilter quickPerfDirFilter = (dir, name) -> name.contains("QuickPerf");

	private final File tempDir = new File(tempDirPath);

	@Test
	public void measure() throws IOException {
		logger.debug("measure - start");
		if (!Maven3Version.V_3_6_2.alreadyDownloaded()) {
			Maven3Version.V_3_6_2.download();
			System.out.println();
		}

		String mavenPath = Maven3Version.V_3_6_2.getMavenPath();

		System.setProperty("verifier.forkMode", "auto"); // embedded
		System.setProperty("maven.home", mavenPath);

		String dateTimeAsString = getDateTimeAsString();
		String resultFilePath = buildAllocationCsvExportPath(dateTimeAsString);

		String firstCommitHash = BenchProperties.INSTANCE.getCommitFirstHash();
		String lastCommitHash = BenchProperties.INSTANCE.getCommitLastHash();
		String sourcePath = BenchProperties.INSTANCE.getMavenSourcePath();
		String branch = BenchProperties.INSTANCE.getMavenSourceBranch();

		assertNotNull(firstCommitHash, "commit.first.hash must be provided in maven-bench.properties");
		assertNotNull(lastCommitHash, "commit.last.hash must be provided in maven-bench.properties");

		int numberOfMeasuresByVersion = BenchProperties.INSTANCE.getNumberOfMeasuresByMavenVersion();

		cloneAndCheckoutMavenHash(firstCommitHash, sourcePath, branch);

		buildMaven(sourcePath);

		
		Path hashMavenBuilt = getBuiltPath(sourcePath, "maven", "apache-maven");
		System.setProperty("maven.built.home", hashMavenBuilt.toAbsolutePath().toString());

		Class<?> testClass = MvnValidate.class;
		


		applyWarmMeasurements(testClass);
//		AllocationTimePair[] allocations = measureAllocationSeveralTimes(testClass, numberOfMeasuresByVersion);
//		AllocationCsvExporter.INSTANCE.writeAllocationsToCsv(allocations, resultFilePath);

		//ExecutionContextTextExporter.INSTANCE.writeExecutionContextToTextFile(dateTimeAsString);
		logger.debug("measure - end");
	}

	private Path getBuiltPath(String sourcePath, String name, String subProjectName) throws ZipException {
		Path path = Paths.get(sourcePath, name, subProjectName, "target");
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".zip") && name.startsWith("apache-maven-");
			}
		};
		String[] res = path.toFile().list(filter);
		Path zipPath = Paths.get(path.toAbsolutePath().toString(), res[0]);
		System.out.println("Found build zip " + zipPath.toAbsolutePath().toString());
		ZipFile zipFile = new ZipFile(zipPath.toFile());
		zipFile.extractAll(path.toAbsolutePath().toString());
		String extractedDir = zipPath.getFileName().toString().replace("-bin.zip", "");
		Path mavenExtractedPath = Paths.get(path.toAbsolutePath().toString(), extractedDir);
		System.out.println("Extracted directory " + mavenExtractedPath.toAbsolutePath().toString());
		return mavenExtractedPath;
	}

//    @Test
//    public void buildTest() {
//        String mavenPath = Maven3Version.V_3_6_2.getMavenPath();
//        System.setProperty("verifier.forkMode", "auto"); // embedded
//        System.setProperty("maven.home", mavenPath);         	
//        String sourcePath = BenchProperties.INSTANCE.getMavenSourcePath();
//
//    	buildMaven(sourcePath);
//    }

//	@Test
//	public void unzipTest() throws ZipException {
//		String sourcePath = BenchProperties.INSTANCE.getMavenSourcePath();
//		Path hashMavenBuilt = getBuiltPath(sourcePath, "maven", "apache-maven");
//		System.out.println();
//	}
	
	@Test
	//TODO: delete after proper implementation
	public void measureTest() throws IOException {
		String sourcePath = BenchProperties.INSTANCE.getMavenSourcePath();
		Path hashMavenBuilt = getBuiltPath(sourcePath, "maven", "apache-maven");
		System.setProperty("maven.built.home", hashMavenBuilt.toAbsolutePath().toString());

		Class<?> testClass = MvnValidate.class;
		
		//TODO: How to store a maven version if will be an hash ?
		saveMavenVersion(Maven3Version.HASH);
		applyWarmMeasurements(testClass);
	}
	

	private void saveMavenVersion(Maven3Version maven3Version) {
		FileUtils.deleteQuietly(new File(tempDirPath + File.separator + MAVEN_3_VERSION_FILE_NAME));
		ObjectFileRepository.INSTANCE.save(tempDirPath, MAVEN_3_VERSION_FILE_NAME, maven3Version);
	}

	private void applyWarmMeasurements(Class<?> testClass) throws IOException {
		int numberOfWarms = BenchProperties.INSTANCE.getNumberOfWarms();
		if (numberOfWarms != 0) {
			System.out.println("First hash - Start " + numberOfWarms + " warm up");
			System.out.println("-----------------------------");
			measureAllocationSeveralTimes(testClass, numberOfWarms);
			System.out.println("First hash - End warm up");
			System.out.println("----------------------------");
		}
	}

	private String buildAllocationCsvExportPath(String dateTimeAsString) {
		String measurementsExportPath = BenchProperties.INSTANCE.getExportPathOfMeasures();
		String fileName = "maven-memory-allocation" + "-" + dateTimeAsString + ".csv";
		return measurementsExportPath + File.separator + fileName;
	}

	private String getDateTimeAsString() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return df.format(new Date());
	}

	private AllocationTimePair[] measureAllocationSeveralTimes(Class<?> testClass, int numberOfTimes)
			throws IOException {
		AllocationTimePair[] allocations = new AllocationTimePair[numberOfTimes];
		for (int i = 0; i < numberOfTimes; i++) {
			allocations[i] = measureAllocation(testClass);
		}
		return allocations;
	}

	private AllocationTimePair measureAllocation(Class<?> testClass) throws IOException {
		deleteQuickPerfFoldersInTemp();
		long startTime = System.currentTimeMillis();
		PrintableResult printableResult = testResult(testClass);
		long executionTimeInMilliseconds = System.currentTimeMillis() - startTime;
		if (printableResult.failureCount() != 0) {
			System.out.println("Allocation can't be measured. " + printableResult.toString());
		}
		Long allocationInBytes = retrieveMeasuredAllocationInBytes();
		Long lenghtInSeconds = executionTimeInMilliseconds / 1000l;
		System.out.println("Allocation in bytes: " + allocationInBytes);
		System.out.println("Lenght in seconds: " + lenghtInSeconds);
		System.out.println("----------------");
		return new AllocationTimePair(allocationInBytes, lenghtInSeconds);
	}

	private void deleteQuickPerfFoldersInTemp() throws IOException {
		File[] quickPerfFoldersBeforeMeasure = tempDir.listFiles(quickPerfDirFilter);
		for (File quickPerfFolder : quickPerfFoldersBeforeMeasure) {
			FileUtils.deleteDirectory(quickPerfFolder);
		}
	}

	private Long retrieveMeasuredAllocationInBytes() {
		LongFileRepository longFileRepository = new LongFileRepository();
		String[] quickPerfFolders = tempDir.list(quickPerfDirFilter);
		if (quickPerfFolders.length != 1) {
			throw new IllegalStateException("Several QuickPerf folders found in temp.");
		}
		String quickPerfFolderPath = tempDirPath + File.separator + quickPerfFolders[0];
		return longFileRepository.find(quickPerfFolderPath, "allocation.ser");
	}

	private void cloneAndCheckoutMavenHash(String commitHash, String sourcePath, String branch) throws IOException {
		Path path = Paths.get(sourcePath);
		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("sourcePath must be a directory");
		}
		boolean empty = !Files.list(path).findAny().isPresent();
		if (!empty) {
			throw new IllegalArgumentException("provided maven source directory must be empty");
		}
		GitDelegate.clone(path);
		GitDelegate.checkOut(Paths.get(sourcePath, "maven"), branch, commitHash);
	}

	private void buildMaven(String sourcePath) {
		try {
			System.out.println("Mvn clean package ");
			ProcessBuilder pb = new ProcessBuilder();
			String mvnExec = null;
			if (OSInfo.getOSType().compareTo(OSType.WINDOWS) == 0) {
				mvnExec = "mvn.cmd";
			} else {
				mvnExec = "mvn";
			}

			pb.command(Paths.get(System.getProperty("maven.home"), "bin", mvnExec).toAbsolutePath().toString(), "clean",
					"package");
			pb.directory(Paths.get(sourcePath, "maven").toFile());
			Process process = pb.start();

			final StringWriter messageWriter = new StringWriter();
			final StringWriter errorWriter = new StringWriter();

			Thread outDrainer = new Thread(new Runnable() {
				public void run() {
					try {
						IOUtils.copy(process.getInputStream(), messageWriter);
					} catch (IOException e) {
					}
				}
			});

			Thread errorDrainer = new Thread(new Runnable() {
				public void run() {
					try {
						IOUtils.copy(process.getErrorStream(), errorWriter);
					} catch (IOException e) {
					}
				}
			});

			outDrainer.start();
			errorDrainer.start();

			int err = process.waitFor();

			outDrainer.join();
			errorDrainer.join();

			if (err != 0) {
				throw new RuntimeException("Error mvn clean package " + errorWriter.toString());
			}

			String message = messageWriter.toString();
			System.out.println("Mvn clean package completed " + message);
		} catch (IOException | InterruptedException ex) {
			throw new RuntimeException("Error mvn clean package " + ex.getMessage());
		}
	}

}
