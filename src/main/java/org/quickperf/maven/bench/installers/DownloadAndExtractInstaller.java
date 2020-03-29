package org.quickperf.maven.bench.installers;

import org.quickperf.maven.bench.Archive;
import org.quickperf.maven.bench.Downloader;
import org.quickperf.maven.bench.Installer;

public class DownloadAndExtractInstaller implements Installer {

    private final Downloader downloader;
    private final Archive archive;

    public DownloadAndExtractInstaller(Downloader downloader, Archive archive) {
        this.downloader = downloader;
        this.archive = archive;
    }

    @Override
    public String install(String sourceUrl, String targetPath) {
        final String targetFilePath = downloader.download(sourceUrl, targetPath);
        if (archive == null) {
            return targetFilePath;
        }
        return archive.unarchive(targetFilePath, targetPath);
    }
}
