package org.quickperf.maven.bench.projects;

import org.quickperf.maven.bench.Command;
import org.quickperf.maven.bench.commands.GitClone;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestingProject {

    private final String name;
    private final String repository;
    private final String version;
    private final String targetPath;

    public TestingProject(String name, String repository, String version, String targetPath) {
        this.name = name;
        this.repository = repository;
        this.version = version;
        this.targetPath = targetPath;
    }

    public boolean isNotAlreadyInstalled() {
        return Files.notExists(Paths.get(this.targetPath));
    }

    public String getPath() {
        return targetPath;
    }

    public void installProject() {
        final Command install = new GitClone(this.repository, this.targetPath, this.version);
        install.execute();
    }
}
