import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

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
        // Properties properties = loadProperties();
        BenchPropertiesResolver properties  = new DefaultBenchPropertiesResolver(fileName);

        String numberOfMeasuresByMavenVersionAsString = properties.getProperty("measures.number-by-maven-version");
        this.numberOfMeasuresByMavenVersion = Integer.parseInt(numberOfMeasuresByMavenVersionAsString);

        String numberOfWarnsAsString = properties.getProperty("warmup.number");
        this.numberOfWarns = Integer.parseInt(numberOfWarnsAsString);

        this.mavenBinariesPath = properties.getProperty("maven.binaries.path");

        this.projectUnderTest = properties.getProperty("project-under-test.path");

        this.exportPathOfMeasures = properties.getProperty("measures.export.path");

        this.maven3VersionsToMeasure = findMaven3VersionsToMeasure(properties);

    }

    private static String findSrcTestResourcePath() {
        File file = new File("src/test/resources");
        return file.getAbsolutePath();
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

    public String getPathOfProjectUnderTest() {
        return projectUnderTest;
    }

    public List<Maven3Version> getMaven3VersionsToMeasure() {
        return maven3VersionsToMeasure;
    }

    @FunctionalInterface
    private interface BenchPropertiesResolver {
        String getProperty(String key);
    }

    private static class DefaultBenchPropertiesResolver implements BenchPropertiesResolver {

        private final List<BenchPropertiesResolver> resolvers = new ArrayList<>();

        private DefaultBenchPropertiesResolver(String fileName) {
            resolvers.add(key -> System.getenv(key));
            resolvers.add(key -> System.getProperty(key));
            resolvers.add(new BenchPropertiesFileBasedResolver("local." + fileName));
            resolvers.add(new BenchPropertiesFileBasedResolver(fileName));
        }

        @Override
        public String getProperty(String key) {
            return resolvers.stream()
                .map(benchPropertiesResolver -> benchPropertiesResolver.getProperty(key))
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
        }
    }

    private static class BenchPropertiesFileBasedResolver implements BenchPropertiesResolver {
        private Properties props;

        private BenchPropertiesFileBasedResolver(String fileName) {
            this.props = this.loadProperties(fileName);
        }

        private Properties loadProperties(String fileName) {
            Properties properties = new Properties();
            String propertiesFilePath = findSrcTestResourcePath() + File.separator + fileName;
            try (final FileInputStream fileInputStream = new FileInputStream(propertiesFilePath)) {
                properties.load(fileInputStream);
            } catch (IOException fileNotFoundEx) {
                throw new IllegalStateException("Unable to load bench properties.", fileNotFoundEx);
            }
            return properties;
        }

        @Override
        public String getProperty(String key) {
            return props.getProperty(key);
        }
    }
}
