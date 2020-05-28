package org.quickperf.maven.bench.commands;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Arrays.asList;
import static org.quickperf.maven.bench.commands.PropertiesUtils.loadBenchToolsProperties;

public class GitCloneTest {
    private static Path GIT_CLONE_WORKING_DIR_PATH;
    public static Path FAKE_ORIGINAL_GIT_REPO_PATH;

    @BeforeClass
    public static void setUpBeforeClass() throws IOException, GitAPIException {
        GIT_CLONE_WORKING_DIR_PATH = Files.createTempDirectory("org-quickperf-maven-bench-commands-gitclone");
        FAKE_ORIGINAL_GIT_REPO_PATH = GIT_CLONE_WORKING_DIR_PATH.resolve("fakeOriginalGitRepo");

        final File gitRepoDir = FAKE_ORIGINAL_GIT_REPO_PATH.toFile();
        Git.init().setDirectory(gitRepoDir).call();
        final Path fakeReadMePath = FAKE_ORIGINAL_GIT_REPO_PATH.resolve("README.md");
        Files.write(fakeReadMePath, "# READE ME\n".getBytes(StandardCharsets.UTF_8));
        Git.open(gitRepoDir).add().addFilepattern("README.md").call();
        Git.open(gitRepoDir).commit().setAll(true).setMessage("Initial Commit").call();
    }

    @AfterClass
    public static void tearDownAfterClass() throws IOException {
        Files.walk(GIT_CLONE_WORKING_DIR_PATH)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    public void execute() throws IOException {
        final Path outputDirPath = GIT_CLONE_WORKING_DIR_PATH.resolve("test");
        final GitClone gitClone = new GitClone(FAKE_ORIGINAL_GIT_REPO_PATH.toUri().toString(),
                outputDirPath.toString(),
                "master");

        final String result = gitClone.execute();

        assertThat(result).isEqualTo(outputDirPath.toString());
        final Path resultDirPath = Paths.get(result);
        final List<Path> files = Files.list(resultDirPath).collect(Collectors.toList());
        assertThat(files).containsOnly(
                resultDirPath.resolve(".git"),
                resultDirPath.resolve("README.md")
        );
    }
}