package org.quickperf.maven.bench.commands;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.quickperf.maven.bench.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Command to run for building Maven from Source code.
 *
 * <p>
 *     This command execute the following step:
 *     <ul>
 *         <li>git clone sourceUrl@head</li>
 *         <li>mvn clean package -DskipTest</li>
 *         <li>unzip APACHE_MAVEN_DIR/target/apache-maven-version-bin.zip targetPath</li>
 *     </ul>
 * </p>
 */
public class MavenBuildFromGitSourceInstall implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenBuildFromGitSourceInstall.class);

    private final String sourceUrl;
    private final String targetPath;
    private final String head;

    public MavenBuildFromGitSourceInstall(String sourceUrl, String targetPath, String head) {
        this.sourceUrl = sourceUrl;
        this.targetPath = targetPath;
        this.head = head;
    }

    @Override
    public String execute() {
        LOGGER.info("Install Apache Maven project from GIT source code {} into {}", sourceUrl, targetPath);
        final String targetDirectoryPath;
        try {
            final Path tempDirectory = Files.createTempDirectory("maven-test-bench");
            targetDirectoryPath = new GitClone(sourceUrl, tempDirectory.toString(), head).execute();
        } catch (IOException e) {
            throw new IllegalStateException("Could not clone project " + sourceUrl + " into temporary directory", e);
        }

        try {
            LOGGER.debug("Running 'mvn clean package -DskipTest' for building Apache Maven project");
            final Process process = Runtime.getRuntime().exec("mvn clean package -DskipTests -f " + targetDirectoryPath + "/pom.xml");
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                LOGGER.trace(line);
            }
            input.close();

            final int exitValue = process.waitFor();
            if (exitValue != 0) {
                throw new IllegalStateException("mvn clean package -DskipTests has failed for maven project directory: " + targetDirectoryPath + ". Exit code: " + exitValue);
            }

        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Could not build with Maven from project directory " + targetDirectoryPath, e);
        }

        final String pomXmlFile = targetDirectoryPath + "/pom.xml";
        final String apacheMavenBinDir;
        LOGGER.trace("Getting project version from pom project.");
        try (final Reader reader = new FileReader(pomXmlFile)){
            final MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
            final Model model = xpp3Reader.read(reader);
            final String apacheMavenVersion = model.getVersion();
            apacheMavenBinDir = targetDirectoryPath + "/apache-maven/target/apache-maven-" + apacheMavenVersion + "-bin.zip";
        } catch (final IOException | XmlPullParserException e) {
            throw new IllegalStateException("Cannot read pom version.", e);
        }
        return new ExtractZip(apacheMavenBinDir, targetPath).execute();
    }
}
