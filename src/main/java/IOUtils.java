import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class IOUtils {

    private IOUtils(){}

    public static String download(String sourceUrl, String targetPath) {
        URL url = null;
        try {
            url = new URL(sourceUrl);
        } catch (MalformedURLException malformedUrlEx) {
            throw new IllegalArgumentException("source url is not well formatted.", malformedUrlEx);
        }

        String fileName = getDirectoryNameFromUrl(url);
        File downloadDir = new File(targetPath);
        if(!downloadDir.exists()) {
            downloadDir.mkdir();
        }

        final String downloadedFilePath = String.format("%s/%s", targetPath, fileName);
        try (
                final FileOutputStream fileOutputStream = new FileOutputStream(downloadedFilePath);
                final FileChannel fileChannel = fileOutputStream.getChannel();
        ) {
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (final IOException downloadEx) {
            throw new IllegalStateException("Could not download project under test.", downloadEx);
        }

        return downloadedFilePath;
    }

    public static String getDirectoryNameFromUrl(URL url) {
        return url.getFile().substring(url.getFile().lastIndexOf("/") + 1);
    }

}
