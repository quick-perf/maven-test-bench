package org.quickperf.maven.bench.archivers;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.quickperf.maven.bench.Archive;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ZipArchive implements Archive {

    private static final ZipArchive INSTANCE = new ZipArchive();

    public static ZipArchive getInstance() {
        return INSTANCE;
    }

    private ZipArchive(){}

    @Override
    public String unarchive(String sourceZipFilePath, String targetDirectoryPath) {
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
