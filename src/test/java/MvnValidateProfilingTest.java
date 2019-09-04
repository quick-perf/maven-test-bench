import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quickperf.junit4.QuickPerfJUnitRunner;
import org.quickperf.jvm.allocation.AllocationUnit;
import org.quickperf.jvm.annotations.HeapSize;
import org.quickperf.jvm.annotations.ProfileJvm;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RunWith(QuickPerfJUnitRunner.class)
public class MvnValidateProfilingTest {

    public static Maven3Version MAVEN_3_VERSION = Maven3Version.V_3_2_5;

    private final String pathOfMavenProjectUnderTest = BenchProperties.INSTANCE.getPathOfProjectUnderTest();

    private Verifier verifier;

    private final List<String> validate = Collections.singletonList("validate");

    @ProfileJvm
    @HeapSize(value = 6, unit = AllocationUnit.GIGA_BYTE)
    @Test
    public void execute_maven_validate() throws VerificationException {
        verifier.executeGoals(validate);
    }

    @Before
    public void before() throws IOException, VerificationException {

        System.out.println(MAVEN_3_VERSION);

        if(!MAVEN_3_VERSION.alreadyDownloaded()) {
            MAVEN_3_VERSION.download();
        }

        String mavenPath = MAVEN_3_VERSION.getMavenPath();

        System.setProperty("verifier.forkMode", "auto"); // embedded

        System.setProperty("maven.home", mavenPath);

        verifier = new Verifier(pathOfMavenProjectUnderTest);
        verifier.setSystemProperty("maven.multiModuleProjectDirectory", pathOfMavenProjectUnderTest);

    }

}
