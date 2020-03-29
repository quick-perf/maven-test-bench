package org.quickperf.maven.bench.downloaders;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.quickperf.maven.bench.Downloader;

import java.nio.file.Paths;

public class GitCloneDownloader implements Downloader {
    private static final GitCloneDownloader INSTANCE = new GitCloneDownloader();
    private GitCloneDownloader(){}

    public static GitCloneDownloader getInstance() {
        return INSTANCE;
    }

    @Override
    public String download(String sourceCloneUri, String targetPath) {
        try {
            Git.cloneRepository()
                .setURI(sourceCloneUri)
                .setDirectory(Paths.get(targetPath).toFile())
                .call();
        } catch (GitAPIException e) {
            throw new IllegalStateException("Could not clone " + sourceCloneUri + " repository.", e);
        }
        return targetPath;
    }

}
