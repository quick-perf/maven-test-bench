package org.quickperf.maven.bench.installers;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.quickperf.maven.bench.Archive;
import org.quickperf.maven.bench.Downloader;
import org.quickperf.maven.bench.Installer;
import org.quickperf.maven.bench.archivers.ZipArchive;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class MavenBuildFromSourceInstaller implements Installer {

    private final Downloader downloader;
    private final Archive zip = ZipArchive.getInstance();

    public MavenBuildFromSourceInstaller(Downloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public String install(String sourceUrl, String targetPath) {
        final String targetDirectoryPath;
        try {
            final Path tempDirectory = Files.createTempDirectory("maven-test-bench");
            targetDirectoryPath = downloader.download(sourceUrl, tempDirectory.toString());
        } catch (IOException e) {
            throw new IllegalStateException("Could not clone project " + sourceUrl + " into temporary directory", e);
        }

        try {
            final Process process = Runtime.getRuntime().exec("mvn clean package -DskipTests -f " + targetDirectoryPath + "/pom.xml");
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
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
        try (final Reader reader = new FileReader(pomXmlFile)){
            final MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
            final Model model = xpp3Reader.read(reader);
            System.out.println(model.getVersion());
            final String apacheMavenVersion = model.getVersion();
            apacheMavenBinDir = targetDirectoryPath + "/apache-maven/target/apache-maven-" + apacheMavenVersion + "-bin.zip";
        } catch (final IOException | XmlPullParserException e) {
            throw new IllegalStateException("Cannot read pom version.", e);
        }
        System.out.println("unziping to " + apacheMavenBinDir);
        return zip.unarchive(apacheMavenBinDir, targetPath);
    }
}
