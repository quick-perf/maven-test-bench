import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public enum Maven3Version {

     V_3_0_4("3.0.4")
    ,V_3_0_5("3.0.5")
    ,V_3_1_0("3.1.0")
    ,V_3_1_1("3.1.1")
    ,V_3_2_1("3.2.1")
    ,V_3_2_2("3.2.2")
    ,V_3_2_3("3.2.3")
    ,V_3_2_5("3.2.5")
    ,V_3_3_1("3.3.1")
    ,V_3_3_3("3.3.3")
    ,V_3_3_9("3.3.9")
    ,V_3_5_0("3.5.0")
    ,V_3_5_2("3.5.2")
    ,V_3_5_3("3.5.3")
    ,V_3_5_4("3.5.4")
    ,V_3_6_0("3.6.0")
    ,V_3_6_1("3.6.1")
    ,V_3_6_2("3.6.2")
    ,HEAD("head")
    ;

    private final String numVersion;

    Maven3Version(String numVersion) {
        this.numVersion = numVersion;
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

    public void download() throws IOException {
        downloadMavenZip();
        unzipMavenZip();
        deleteMavenZip();
    }

    private void downloadMavenZip() throws IOException {
        String urlAsString = "https://archive.apache.org/dist/maven/maven-3/" + numVersion + "/binaries/apache-maven-" + numVersion + "-bin.zip";
        URL url = new URL(urlAsString);
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        String mavenZipFilePath = findMavenZipFilePath();
        FileOutputStream fileOutputStream = new FileOutputStream(mavenZipFilePath);
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        fileOutputStream.close();
        readableByteChannel.close();
    }

    private String findMavenZipFilePath() {
        String mavenBinariesPath = BenchProperties.INSTANCE.getMavenBinariesPath();
        return mavenBinariesPath + File.separator + "apache-maven-" + numVersion + "-bin.zip";
    }

    private void unzipMavenZip() throws ZipException {
        String mavenZipFilePath = findMavenZipFilePath();
        ZipFile zipFile = new ZipFile(mavenZipFilePath);
        String mavenBinariesPath = BenchProperties.INSTANCE.getMavenBinariesPath();
        zipFile.extractAll(mavenBinariesPath);
    }

    private void deleteMavenZip() {
        String mavenZipFilePath = findMavenZipFilePath();
        File mavenZip = new File(mavenZipFilePath);
        mavenZip.delete();
    }

    public String getNumVersion() {
        return numVersion;
    }

    @Override
    public String toString() {
        return "Maven" + " " + numVersion;
    }

}
