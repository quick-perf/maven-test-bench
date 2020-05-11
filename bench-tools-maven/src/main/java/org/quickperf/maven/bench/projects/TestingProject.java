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

package org.quickperf.maven.bench.projects;

import org.quickperf.maven.bench.Command;
import org.quickperf.maven.bench.commands.GitClone;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestingProject {

    private final String name;
    private final String repository;
    private final String version;
    private final String targetPath;

    public TestingProject(String name, String repository, String version, String targetPath) {
        this.name = name;
        this.repository = repository;
        this.version = version;
        this.targetPath = targetPath;
    }

    public boolean isNotAlreadyInstalled() {
        return Files.notExists(Paths.get(this.targetPath));
    }

    public String getPath() {
        return targetPath;
    }

    public void installProject() {
        final Command install = new GitClone(this.repository, this.targetPath, this.version);
        install.execute();
    }
}
