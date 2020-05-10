package org.quickperf.maven.bench.commands;

import org.quickperf.maven.bench.Command;
import org.quickperf.maven.bench.projects.Maven3Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class InstallMavenVersionIfNotExists implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstallMavenVersionIfNotExists.class);
    private final Maven3Version maven3Version;

    public InstallMavenVersionIfNotExists(Maven3Version maven3Version) {
        this.maven3Version = maven3Version;
    }

    @Override
    public String execute() {
        LOGGER.info("Installing Maven {} if it has not been already installed", maven3Version);
        Command install = null;
        switch (maven3Version.getInstallProcess()) {
            case HTTP:
                install = new InstallWithHttpGet(maven3Version.getUrlAsString(), maven3Version.getMavenPath());
                break;
            case GIT:
                install = new MavenBuildFromGitSourceInstall(maven3Version.getUrlAsString(), maven3Version.getMavenPath(), maven3Version.getNumVersion());
                break;
        }

        String mavenPath = maven3Version.getMavenPath();
        if (alreadyDownloaded(mavenPath)) {
            LOGGER.debug("Maven version has been already downloaded");
        } else {
            LOGGER.debug("Maven version is absent, installing maven {}", maven3Version);
            mavenPath = install.execute();
        }

        return mavenPath;
    }

    private boolean alreadyDownloaded(final String mavenFilePath) {
        File mavenFile = new File(mavenFilePath);
        return mavenFile.exists();
    }

}
