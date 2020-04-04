package org.quickperf.maven.bench.commands;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.quickperf.maven.bench.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExtractZip implements Command {
    private final String sourceZipFilePath;
    private final String targetDirectoryPath;

    public ExtractZip(String sourceZipFilePath, String targetDirectoryPath){
        this.sourceZipFilePath = sourceZipFilePath;
        this.targetDirectoryPath = targetDirectoryPath;
    }

    @Override
    public String execute() {
        final ZipFile zipFile = new ZipFile(sourceZipFilePath);
        try {
            final FileHeader rootFileHeader = zipFile.getFileHeaders().get(0);
            if (rootFileHeader.isDirectory()) {
                final Path currentProjectPath = Paths.get(targetDirectoryPath);
                System.out.println(targetDirectoryPath);
                System.out.println(currentProjectPath);
                final String parentDirectoryPath = currentProjectPath.getParent().toString();
                zipFile.extractAll(parentDirectoryPath);
                final Path extractProjectDirPath = Paths.get(parentDirectoryPath + File.separator + rootFileHeader.getFileName());
                Files.move(extractProjectDirPath, Paths.get(targetDirectoryPath));
            } else {
                zipFile.extractAll(targetDirectoryPath);
            }
        } catch (final IOException unableExtractingEx) {
            throw new IllegalStateException(
                    "Unable to extract zip file " + sourceZipFilePath + " to directory " + targetDirectoryPath,
                    unableExtractingEx);
        }
        return targetDirectoryPath;
    }

}
