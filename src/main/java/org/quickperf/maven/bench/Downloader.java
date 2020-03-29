package org.quickperf.maven.bench;

public interface Downloader {

    String download(String sourceUrlAsString, String targetPath) throws IllegalStateException;

}
