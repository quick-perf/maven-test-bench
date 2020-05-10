package org.quickperf.maven.bench;

import org.apache.commons.io.FileUtils;
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
import org.quickperf.maven.bench.config.BenchProperties;
import org.quickperf.maven.bench.commands.InstallMavenVersionIfNotExists;
import org.quickperf.maven.bench.projects.Maven3Version;
import org.quickperf.maven.bench.projects.TestingProject;
import org.quickperf.repository.LongFileRepository;
import org.quickperf.repository.ObjectFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.experimental.results.PrintableResult.testResult;

public class MvnValidateAllocationByMaven3VersionTest {
	
    private Logger logger = LoggerFactory.getLogger(MvnValidateAllocationByMaven3VersionTest.class);

    @RunWith(QuickPerfJUnitRunner.class)
    public static class MvnValidate {

        private static final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir");

        private static Maven3Version MAVEN_3_VERSION = (Maven3Version) ObjectFileRepository.INSTANCE
                .find(TEMP_DIR_PATH, MvnValidateAllocationByMaven3VersionTest.MAVEN_3_VERSION_FILE_NAME);

        private final TestingProject projectUnderTest = BenchProperties.INSTANCE.getTestingProject();

        private Verifier verifier;

        private final List<String> validate = Collections.singletonList("validate");

        @Before
        public void before() throws VerificationException {
            System.out.println(MAVEN_3_VERSION);

            String mavenPath = MAVEN_3_VERSION.getMavenPath();

            System.setProperty("verifier.forkMode", "auto"); // embedded

            System.setProperty("maven.home", mavenPath);

            if (projectUnderTest.isNotAlreadyInstalled()) {
                try {
                    projectUnderTest.installProject();
                } catch (IllegalStateException mavenProjectUnderTestNotInstallEx) {
                    throw new IllegalStateException(mavenProjectUnderTestNotInstallEx);
                }
            }

            final String projectDirectoryPath = projectUnderTest.getPath();
            verifier = new Verifier(projectDirectoryPath);
            verifier.setSystemProperty("maven.multiModuleProjectDirectory", projectDirectoryPath);
        }

        @HeapSize(value = 6, unit = AllocationUnit.GIGA_BYTE)
        @MeasureHeapAllocation
        @Test
        public void execute_maven_validate() throws VerificationException {
            verifier.executeGoals(validate);
        }

    }

    private static final String MAVEN_3_VERSION_FILE_NAME = "org.quickperf.maven.bench.projects.Maven3Version";

    private final String tempDirPath = System.getProperty("java.io.tmpdir");

    private final FilenameFilter quickPerfDirFilter = (dir, name) -> name.contains("QuickPerf");

    private final File tempDir = new File(tempDirPath);

    @Test
    public void measure() throws IOException {
    	logger.debug("measure - start");
        String dateTimeAsString = getDateTimeAsString();
        String resultFilePath = buildAllocationCsvExportPath(dateTimeAsString);

        int numberOfMeasuresByVersion = BenchProperties.INSTANCE.getNumberOfMeasuresByMavenVersion();

        Class<?> testClass = MvnValidate.class;

        List<Maven3Version> maven3VersionsToMeasure = BenchProperties.INSTANCE.getMaven3VersionsToMeasure();
        for (Maven3Version maven3Version : maven3VersionsToMeasure) {

            new InstallMavenVersionIfNotExists(maven3Version).execute();

            saveMavenVersion(maven3Version);

            applyWarmMeasurements(maven3Version, testClass);

            AllocationTimePair[] allocations = measureAllocationSeveralTimes(testClass, numberOfMeasuresByVersion);

            AllocationCsvExporter.INSTANCE.writeAllocationsToCsv(maven3Version
                                                               , allocations
                                                               , resultFilePath);
        }

        ExecutionContextTextExporter.INSTANCE.writeExecutionContextToTextFile(dateTimeAsString);
        logger.debug("measure - end");
    }

    private void saveMavenVersion(Maven3Version maven3Version) {
        FileUtils.deleteQuietly(new File(tempDirPath
                                         + File.separator
                                         + MAVEN_3_VERSION_FILE_NAME
                                         )
                               );
        ObjectFileRepository.INSTANCE.save(  tempDirPath
                                           , MAVEN_3_VERSION_FILE_NAME
                                           , maven3Version);
    }

    private void applyWarmMeasurements(Maven3Version maven3Version, Class<?> testClass) throws IOException {
        int numberOfWarms = BenchProperties.INSTANCE.getNumberOfWarms();
        if(numberOfWarms != 0) {
            System.out.println(maven3Version + " - Start " + numberOfWarms + " warm up");
            System.out.println("-----------------------------");
            measureAllocationSeveralTimes(testClass, numberOfWarms);
            System.out.println(maven3Version + " - End warm up");
            System.out.println("----------------------------");
        }
    }

    private String buildAllocationCsvExportPath(String dateTimeAsString) throws IOException {
        String measurementsExportPathName = BenchProperties.INSTANCE.getExportPathOfMeasures();
        final Path measurementsExportPath = Paths.get(measurementsExportPathName);
        if (Files.notExists(measurementsExportPath)) {
            Files.createDirectories(measurementsExportPath);
        }
        String fileName = "maven-memory-allocation" + "-" + dateTimeAsString + ".csv";
        return measurementsExportPathName + File.separator + fileName;
    }

    private String getDateTimeAsString() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return df.format(new Date());
    }

    private AllocationTimePair[] measureAllocationSeveralTimes(Class<?> testClass, int numberOfTimes) throws IOException {
    	AllocationTimePair[] allocations = new AllocationTimePair[numberOfTimes];
        for (int i = 0; i < numberOfTimes; i++) {
        	allocations[i] =measureAllocation(testClass);
        }
        return allocations;
    }

    private AllocationTimePair measureAllocation(Class<?> testClass) throws IOException {
        deleteQuickPerfFoldersInTemp();
        long startTime = System.currentTimeMillis();
        PrintableResult printableResult = testResult(testClass);
        long executionTimeInMilliseconds = System.currentTimeMillis() - startTime;
        if(printableResult.failureCount() != 0) {
            System.out.println("Allocation can't be measured. " + printableResult.toString());
        }
        Long allocationInBytes = retrieveMeasuredAllocationInBytes();
        Long lengthInSeconds = executionTimeInMilliseconds/1000l;
        System.out.println("Allocation in bytes: " + allocationInBytes);
        System.out.println("Length in seconds: " + lengthInSeconds);
        System.out.println("----------------");
        return new AllocationTimePair(allocationInBytes, lengthInSeconds);
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
        String quickPerfFolderPath = tempDirPath
                                   + File.separator
                                   + quickPerfFolders[0];
        return longFileRepository.find(quickPerfFolderPath, "allocation.ser");
    }

}
