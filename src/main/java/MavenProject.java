import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MavenProject {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    private static final String HEAD_SPACES = "    ";

    private final int numberOfModules;

    public static void main(String[] args) throws IOException {
        int numberOfModules = 2_000;
        MavenProject mavenProject = new MavenProject(numberOfModules);
        mavenProject.generateMavenProjectWithModules();
    }

    public MavenProject(int numberOfModules) {
        this.numberOfModules = numberOfModules;
    }

    private final String xmlElement = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + LINE_SEPARATOR;

    private final String projectStartTag = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"" + LINE_SEPARATOR +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + LINE_SEPARATOR +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">" + LINE_SEPARATOR;

    private final String projectEndTag = "</project>";

    private final String modelVersionElement = "<modelVersion>4.0.0</modelVersion>" + LINE_SEPARATOR;

    private final String parentGroupIdElement = "<groupId>org.quickperf</groupId>" + LINE_SEPARATOR;

    private final String parentArtifactIdElement = "<artifactId>maven-project</artifactId>" + LINE_SEPARATOR;

    private final String parentVersionElement = "<version>1.0-SNAPSHOT</version>" + LINE_SEPARATOR;


    public void generateMavenProjectWithModules() throws IOException {

        if(isMavenProjectFolderAlreadyGenerated()) {
            System.out.println("A Maven project with " + numberOfModules
                             + " modules already exists.");
            return;
        }

        File mavenProjectFolder = createMavenProjectFolder();
        createParentPomFile(numberOfModules, mavenProjectFolder);

        for (int i = 1; i <= numberOfModules; i++) {
            File moduleFolder = createModuleFolder(mavenProjectFolder, i);
            createModulePom(i, moduleFolder);
        }

    }

    private boolean isMavenProjectFolderAlreadyGenerated() {
        File mavenProjectFolder = new File(getPath());
        return mavenProjectFolder.exists();
    }

    private void createModulePom(int i, File moduleFolder) throws IOException {
        File modulePom = new File(moduleFolder, "pom.xml");

        modulePom.createNewFile();

        try (FileWriter fileWriter = new FileWriter(modulePom);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(buildModulePomContent(i));
        }
    }

    private File createModuleFolder(File mavenProjectFolder, int i) {
        File moduleFolder = new File(mavenProjectFolder, "module-" + i);
        moduleFolder.mkdir();
        return moduleFolder;
    }

    private void createParentPomFile(int numberOfModules, File mavenProjectDirectory) throws IOException {
        File parentPom = new File(mavenProjectDirectory, "pom.xml");

        parentPom.createNewFile();

        try (FileWriter fileWriter = new FileWriter(parentPom);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(buildParentPomContent(numberOfModules));
        }
    }

    private File createMavenProjectFolder() {
        File mavenProjectFolder = new File(getPath());
        mavenProjectFolder.mkdir();
        return mavenProjectFolder;
    }

    public String getPath() {
        String relativePath = "src/test/resources/maven-project-" + numberOfModules + "-modules";
        return new File(relativePath).getAbsolutePath();
    }

    private String buildParentPomContent(int numberOfModules) {
        String parentPomContent = "";
        parentPomContent += xmlElement;
        parentPomContent += projectStartTag;
        parentPomContent += LINE_SEPARATOR;
        parentPomContent += HEAD_SPACES + modelVersionElement;
        parentPomContent += LINE_SEPARATOR;
        parentPomContent += HEAD_SPACES + parentGroupIdElement
                         +  HEAD_SPACES + parentArtifactIdElement
                         +  HEAD_SPACES + parentVersionElement;
        parentPomContent += LINE_SEPARATOR;
        parentPomContent += HEAD_SPACES + "<packaging>pom</packaging>";
        parentPomContent += LINE_SEPARATOR;
        parentPomContent += buildModulesPomPart(numberOfModules);
        parentPomContent += projectEndTag;
        return parentPomContent;
    }

    private String buildModulesPomPart(int numberOfModules) {
        String modulesPomPart = LINE_SEPARATOR;
        if (numberOfModules != 0) {
            modulesPomPart += HEAD_SPACES + "<modules>" + LINE_SEPARATOR;
            for (int i = 1; i <= numberOfModules; i++) {
                modulesPomPart += HEAD_SPACES + HEAD_SPACES;
                modulesPomPart += "<module>" + "module-" + (i) + "</module>";
                modulesPomPart += LINE_SEPARATOR;
            }
            modulesPomPart += HEAD_SPACES + "</modules>" + LINE_SEPARATOR;
            modulesPomPart += LINE_SEPARATOR;
        }
        return modulesPomPart;
    }

    private String buildModulePomContent(int moduleId) {
        String modulePomContent = "";
        modulePomContent += xmlElement;
        modulePomContent += projectStartTag;
        modulePomContent += LINE_SEPARATOR;
        modulePomContent += HEAD_SPACES + modelVersionElement;
        modulePomContent += LINE_SEPARATOR;
        modulePomContent += HEAD_SPACES + "<parent>";
        modulePomContent += LINE_SEPARATOR;
        modulePomContent += HEAD_SPACES + HEAD_SPACES + parentGroupIdElement
                         +  HEAD_SPACES + HEAD_SPACES + parentArtifactIdElement
                         +  HEAD_SPACES + HEAD_SPACES + parentVersionElement;
        modulePomContent += HEAD_SPACES + "</parent>";
        modulePomContent += LINE_SEPARATOR;
        modulePomContent += LINE_SEPARATOR;
        modulePomContent += HEAD_SPACES + "<artifactId>" + "module-" + moduleId + "</artifactId>";
        modulePomContent += LINE_SEPARATOR;
        modulePomContent += LINE_SEPARATOR;
        modulePomContent += projectEndTag;
        return modulePomContent;
    }

}
