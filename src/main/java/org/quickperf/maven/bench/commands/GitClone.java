package org.quickperf.maven.bench.commands;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.quickperf.maven.bench.Command;

import java.nio.file.Paths;

public class GitClone implements Command {

    private final String sourceCloneUri;
    private final String targetPath;
    private final String checkoutBranch;

    public GitClone(String sourceCloneUri, String targetPath, String checkoutBranch) {
        this.sourceCloneUri = sourceCloneUri;
        this.targetPath = targetPath;
        this.checkoutBranch = checkoutBranch;
    }

    @Override
    public String execute() {
        try {
            Git.cloneRepository()
                .setURI(sourceCloneUri)
                .setDirectory(Paths.get(targetPath).toFile())
                .setBranch(checkoutBranch)
                .call();
        } catch (GitAPIException e) {
            throw new IllegalStateException("Could not clone " + sourceCloneUri + " repository.", e);
        }
        return targetPath;
    }

}
