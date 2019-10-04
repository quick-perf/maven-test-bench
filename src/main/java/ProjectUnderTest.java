import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ProjectUnderTest {

    private final String path;
    private final String downloadZipSource;

    public static void main(final String... args) throws IOException {
        final BenchProperties bp = BenchProperties.INSTANCE;
        final ProjectUnderTest prj = new ProjectUnderTest("/tmp/camel",
                "https://github.com/apache/camel/archive/camel-2.23.4.zip");
        final String path = prj.installProject();
        System.out.println(path);
    }

    public ProjectUnderTest(String projectPath, String downloadFrom) {
        this.path = projectPath;
        this.downloadZipSource = downloadFrom;
    }

    public String installProject() throws IOException {
        String projectPath = this.path;

        final String zipLocalPath = IOUtils.download(this.downloadZipSource, FileUtils.getTempDirectoryPath());
        final ZipFile zipFile = new ZipFile(zipLocalPath);
        zipFile.extractAll(this.path);

        final FileHeader rootFileHeader = zipFile.getFileHeaders().get(0);
        if (rootFileHeader.isDirectory()) {
            System.out.println(rootFileHeader.getFileName());
            projectPath = this.path + File.separator + rootFileHeader.getFileName();
        }

        return projectPath;
    }

    public String getPath() {
        return path;
    }
}
