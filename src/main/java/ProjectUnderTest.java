import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class ProjectUnderTest {

    private final String path;
    private final String downloadZipSource;

    public static void main(final String... args) throws IOException {
        final BenchProperties bp = BenchProperties.INSTANCE;
        final ProjectUnderTest prj = new ProjectUnderTest("/tmp/camel",
                "https://github.com/apache/camel/archive/camel-2.23.4.zip");
        prj.downloadProject();
    }

    public ProjectUnderTest(String projectPath, String downloadFrom) {
        this.path = projectPath;
        this.downloadZipSource = downloadFrom;
    }

    private String downloadProject() throws IOException {
        // IOUtils.download()
        return null;
    }


}
