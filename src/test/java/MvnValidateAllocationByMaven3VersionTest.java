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
import org.quickperf.repository.LongFileRepository;
import org.quickperf.repository.ObjectFileRepository;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.experimental.results.PrintableResult.testResult;

public class MvnValidateAllocationByMaven3VersionTest {

    @RunWith(QuickPerfJUnitRunner.class)
    public static class MvnValidate {

        private static final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir");

        public static Maven3Version MAVEN_3_VERSION = (Maven3Version) ObjectFileRepository.INSTANCE
                .find(TEMP_DIR_PATH, MvnValidateAllocationByMaven3VersionTest.MAVEN_3_VERSION_FILE_NAME);

        private final String pathOfMavenProjectUnderTest = BenchProperties.INSTANCE.getPathOfProjectUnderTest();

        private Verifier verifier;

        private final List<String> validate = Collections.singletonList("validate");

        @Before
        public void before() throws VerificationException {

            System.out.println(MAVEN_3_VERSION);

            String mavenPath = MAVEN_3_VERSION.getMavenPath();

            System.setProperty("verifier.forkMode", "auto"); // embedded

            System.setProperty("maven.home", mavenPath);

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

        String dateTimeAsString = getDateTimeAsString();
        String resultFilePath = buildAllocationCsvExportPath(dateTimeAsString);

        int numberOfMeasuresByVersion = BenchProperties.INSTANCE.getNumberOfMeasuresByMavenVersion();

        Class<?> testClass = MvnValidate.class;

        List<Maven3Version> maven3VersionsToMeasure = BenchProperties.INSTANCE.getMaven3VersionsToMeasure();
        for (Maven3Version maven3Version : maven3VersionsToMeasure) {

            if(!maven3Version.alreadyDownloaded()) {
                maven3Version.download();
            }

            saveMavenVersion(maven3Version);

            applyWarmMeasurements(maven3Version, testClass);

            long[] allocations = measureAllocationSeveralTimes(testClass, numberOfMeasuresByVersion);

            AllocationCsvExporter.INSTANCE.writeAllocationsToCsv(maven3Version
                                                               , allocations
                                                               , resultFilePath);
        }

        ExecutionContextTextExporter.INSTANCE.writeExecutionContextToTextFile(dateTimeAsString);

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

    private String buildAllocationCsvExportPath(String dateTimeAsString) {
        String measurementsExportPath = BenchProperties.INSTANCE.getExportPathOfMeasures();
        String fileName = "maven-memory-allocation" + "-" + dateTimeAsString + ".csv";
        return measurementsExportPath + File.separator + fileName;
    }

    private String getDateTimeAsString() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return df.format(new Date());
    }

    private long[] measureAllocationSeveralTimes(Class<?> testClass, int numberOfTimes) throws IOException {
        long[] allocations = new long[numberOfTimes];
        for (int i = 0; i < numberOfTimes; i++) {
            long allocationInBytes = measureAllocation(testClass);
            allocations[i] = allocationInBytes;
        }
        return allocations;
    }

    private Long measureAllocation(Class<?> testClass) throws IOException {
        deleteQuickPerfFoldersInTemp();
        PrintableResult printableResult = testResult(testClass);
        if(printableResult.failureCount() != 0) {
            System.out.println("Allocation can't be measured. " + printableResult.toString());
        }
        Long allocationInBytes = retrieveMeasuredAllocationInBytes();
        System.out.println("Allocation in bytes: " + allocationInBytes);
        System.out.println("----------------");
        return allocationInBytes;
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
