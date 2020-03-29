package org.quickperf.maven.bench.projects;

import org.quickperf.maven.bench.Installer;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestingProject {

    private String path;
    private final String downloadZipSource;
    private final Installer installer;


    public TestingProject(String projectPath, String downloadFrom, Installer installer) {
        this.path = projectPath;
        this.downloadZipSource = downloadFrom;
        this.installer = installer;
    }

    public boolean isNotAlreadyInstalled() {
        return Files.notExists(Paths.get(this.path));
    }

    public String getPath() {
        return path;
    }

    public void installProject() {
        installer.install(downloadZipSource, path);
    }
}
