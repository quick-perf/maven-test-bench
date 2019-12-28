package org.quickperf.maven.bench.projects;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.FileUtils;
import org.quickperf.maven.bench.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestingProject {

    private String path;
    private final String downloadZipSource;

    public TestingProject(String projectPath, String downloadFrom) {
        this.path = projectPath;
        this.downloadZipSource = downloadFrom;
    }

    public boolean isNotAlreadyInstalled() {
        return Files.notExists(Paths.get(this.path));
    }

    public void installProject() throws IOException {
        final String zipLocalPath = IOUtils.download(this.downloadZipSource, FileUtils.getTempDirectoryPath());

        final ZipFile zipFile = new ZipFile(zipLocalPath);
        final FileHeader rootFileHeader = zipFile.getFileHeaders().get(0);
        if (rootFileHeader.isDirectory()) {
            final Path currentProjectPath = Paths.get(this.path);
            System.out.println(path);
            System.out.println(currentProjectPath);
            final String parentDirectoryPath = currentProjectPath.getParent().toString();
            zipFile.extractAll(parentDirectoryPath);
            final Path extractProjectDirPath = Paths.get(parentDirectoryPath + File.separator + rootFileHeader.getFileName());
            Files.move(extractProjectDirPath, Paths.get(this.path));
        } else {
            zipFile.extractAll(this.path);
        }
    }

    public String getPath() {
        return path;
    }
}
