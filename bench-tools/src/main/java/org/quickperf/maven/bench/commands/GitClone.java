/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *
 * Copyright 2019-2020 the original author or authors.
 */

package org.quickperf.maven.bench.commands;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.quickperf.maven.bench.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class GitClone implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitClone.class);

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
            LOGGER.info("Cloning {} into {}", sourceCloneUri, targetPath);
            final Git gitRepository = Git.cloneRepository()
                .setURI(sourceCloneUri)
                .setDirectory(Paths.get(targetPath).toFile())
                .call();
            LOGGER.info("Checking out {} commit", checkoutBranch);
            gitRepository.checkout().setName(checkoutBranch).call();
        } catch (GitAPIException e) {
            throw new IllegalStateException("Could not clone " + sourceCloneUri + " repository.", e);
        }
        return targetPath;
    }

}
