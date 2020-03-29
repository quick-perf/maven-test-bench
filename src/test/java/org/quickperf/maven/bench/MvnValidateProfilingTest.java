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
import org.quickperf.maven.bench.downloaders.HttpGetDownloader;
import org.quickperf.maven.bench.archivers.ZipArchive;
import org.quickperf.maven.bench.installers.DownloadAndExtractInstaller;
import org.quickperf.maven.bench.projects.TestingProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quickperf.maven.bench.config.BenchProperties;
import org.quickperf.maven.bench.projects.Maven3Version;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RunWith(QuickPerfJUnitRunner.class)
public class MvnValidateProfilingTest {
	
    private Logger logger = LoggerFactory.getLogger(MvnValidateProfilingTest.class);

    public static Maven3Version MAVEN_3_VERSION = Maven3Version.V_3_2_5;

    private final TestingProject apacheCamelProject = new TestingProject(
            BenchProperties.INSTANCE.getPathOfProjectUnderTest(),
            "https://github.com/apache/camel/archive/camel-2.23.4.zip",
            new DownloadAndExtractInstaller(HttpGetDownloader.getInstance(), ZipArchive.getInstance())
    );


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

//        final Archive archiver = new ZipArchive();
//        final Installer mavenInstaller = new InstallerImpl(HttpGetDownloader.getInstance(), archiver);
//        final Path tPath = Paths.get(MAVEN_3_VERSION.getMavenPath());
//        final String parentPath = tPath.getParent().toFile().getAbsolutePath();
//
//        mavenInstaller.install(MAVEN_3_VERSION.getUrlAsString(), parentPath);

        // MAVEN_3_VERSION.installMavenIfNotExists();

        String mavenPath = MAVEN_3_VERSION.getMavenPath();
        System.setProperty("verifier.forkMode", "auto"); // embedded
        System.setProperty("maven.home", mavenPath);

        verifier = new Verifier(apacheCamelProject.getPath());
        verifier.setSystemProperty("maven.multiModuleProjectDirectory", apacheCamelProject.getPath());

    }

}
