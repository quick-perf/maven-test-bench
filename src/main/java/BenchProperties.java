import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class BenchProperties {

    public static final BenchProperties INSTANCE = new BenchProperties();

    private BenchProperties() {
        initializeProperties();
    }

    private String fileName = "maven-bench.properties";

    private int numberOfMeasuresByMavenVersion;

    private String exportPathOfMeasures;

    private int numberOfWarns;

    private String mavenBinariesPath;

    private String projectUnderTest;

    private List<Maven3Version> maven3VersionsToMeasure;

    private void initializeProperties() {
        try {
            Properties properties = loadProperties();

            String numberOfMeasuresByMavenVersionAsString = properties.getProperty("measures.number-by-maven-version");
            this.numberOfMeasuresByMavenVersion = Integer.parseInt(numberOfMeasuresByMavenVersionAsString);

            String numberOfWarnsAsString = properties.getProperty("warmup.number");
            this.numberOfWarns = Integer.parseInt(numberOfWarnsAsString);

            this.mavenBinariesPath = properties.getProperty("maven.binaries.path");

            this.projectUnderTest = properties.getProperty("project-under-test.path");

            this.exportPathOfMeasures = properties.getProperty("measures.export.path");

            this.maven3VersionsToMeasure = findMaven3VersionsToMeasure(properties);

        } catch (IOException e) {
            throw new IllegalStateException("Unable to load bench properties.", e);
        }

    }

    private Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        String propertiesFilePath = findSrcTestResourcePath() + File.separator + fileName;
        FileInputStream fileInputStream = new FileInputStream(propertiesFilePath);
        properties.load(fileInputStream);
        fileInputStream.close();
        return properties;
    }

    private static String findSrcTestResourcePath() {
        File file = new File("src/test/resources");
        return file.getAbsolutePath();
    }

    private List<Maven3Version> findMaven3VersionsToMeasure(Properties properties) {

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

    private boolean isMaven3VersionFrom(Maven3Version maven3Version, Properties properties) {
        return properties.getProperty("maven.version.from").equals(maven3Version.getNumVersion());
    }

    private boolean isMaven3VersionTo(Maven3Version maven3Version, Properties properties) {
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

    public String getPathOfProjectUnderTest() {
        return projectUnderTest;
    }

    public List<Maven3Version> getMaven3VersionsToMeasure() {
        return maven3VersionsToMeasure;
    }

}
