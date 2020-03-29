package org.quickperf.maven.bench.projects;

import org.quickperf.maven.bench.Installer;
import org.quickperf.maven.bench.archivers.ZipArchive;
import org.quickperf.maven.bench.config.BenchProperties;
import org.quickperf.maven.bench.downloaders.GitCloneDownloader;
import org.quickperf.maven.bench.downloaders.HttpGetDownloader;
import org.quickperf.maven.bench.installers.DownloadAndExtractInstaller;
import org.quickperf.maven.bench.installers.MavenBuildFromSourceInstaller;

import java.io.File;

public enum Maven3Version {
    V_3_0_4("3.0.4", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_0_5("3.0.5", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_1_0("3.1.0", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_1_1("3.1.1", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_2_1("3.2.1", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_2_2("3.2.2", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_2_3("3.2.3", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_2_5("3.2.5", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_3_1("3.3.1", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_3_3("3.3.3", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_3_9("3.3.9", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_5_0("3.5.0", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_5_2("3.5.2", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_5_3("3.5.3", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_5_4("3.5.4", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_6_0("3.6.0", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_6_1("3.6.1", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_6_2("3.6.2", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_6_3("3.6.3", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,HEAD("head", UrlPatterns.HTTP_GIT_REPO)
    ;

    private final String numVersion;
    private final String urlAsString;
    private final Installer installer;

    Maven3Version(String numVersion, String urlPattern) {
        this.numVersion = numVersion;
        this.urlAsString = String.format(urlPattern, numVersion, numVersion);
        Installer installer = null;
        switch (urlPattern) {
            case UrlPatterns.HTTP_RELEASE_DOWNLOAD:
                installer = new DownloadAndExtractInstaller(HttpGetDownloader.getInstance(), ZipArchive.getInstance());
                break;
            case UrlPatterns.HTTP_GIT_REPO:
                installer = new MavenBuildFromSourceInstaller(GitCloneDownloader.getInstance());
                break;
        }
        this.installer = installer;
    }

    public boolean alreadyDownloaded() {
        String mavenFilePath = getMavenPath();
        File mavenZip = new File(mavenFilePath);
        return mavenZip.exists();
    }

    public String getMavenPath() {
        String mavenBinariesPath = BenchProperties.INSTANCE.getMavenBinariesPath();
        return mavenBinariesPath + File.separator + "apache-maven-" + numVersion;
    }

    public void installMavenIfNotExists() {
        if (!alreadyDownloaded()) {
            installer.install(urlAsString, getMavenPath());
        }
    }

//    public void download() throws IOException {
//        downloadMavenZip();
//        unzipMavenZip();
//        deleteMavenZip();
//    }
//
//    private void downloadMavenZip() throws IOException {
//        URL url = new URL(urlAsString);
//        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
//        String mavenZipFilePath = findMavenZipFilePath();
//        File mavenDownloadDir = new File(BenchProperties.INSTANCE.getMavenBinariesPath());
//        if(!mavenDownloadDir.exists()) {
//            mavenDownloadDir.mkdir();
//        }
//        FileOutputStream fileOutputStream = new FileOutputStream(mavenZipFilePath);
//        FileChannel fileChannel = fileOutputStream.getChannel();
//        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
//        fileOutputStream.close();
//        readableByteChannel.close();
//    }
//
//    private String findMavenZipFilePath() {
//        String mavenBinariesPath = BenchProperties.INSTANCE.getMavenBinariesPath();
//        return mavenBinariesPath + File.separator + "apache-maven-" + numVersion + "-bin.zip";
//    }
//
//    private void unzipMavenZip() throws ZipException {
//        String mavenZipFilePath = findMavenZipFilePath();
//        ZipFile zipFile = new ZipFile(mavenZipFilePath);
//        String mavenBinariesPath = BenchProperties.INSTANCE.getMavenBinariesPath();
//        zipFile.extractAll(mavenBinariesPath);
//    }
//
//    private void deleteMavenZip() {
//        String mavenZipFilePath = findMavenZipFilePath();
//        File mavenZip = new File(mavenZipFilePath);
//        mavenZip.delete();
//    }

    public String getNumVersion() {
        return numVersion;
    }

    public String getUrlAsString() {
        return urlAsString;
    }

    @Override
    public String toString() {
        return "Maven" + " " + numVersion;
    }
    
    private static class UrlPatterns {
        private static final String HTTP_RELEASE_DOWNLOAD = "https://archive.apache.org/dist/maven/maven-3/%s/binaries/apache-maven-%s-bin.zip";
        private static final String HTTP_GIT_REPO = "https://gitbox.apache.org/repos/asf/maven.git";
    }

}
