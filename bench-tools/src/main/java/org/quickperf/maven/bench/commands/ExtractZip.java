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

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.quickperf.maven.bench.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExtractZip implements Command {
    private final static Logger LOGGER = LoggerFactory.getLogger(ExtractZip.class);
    private final String sourceZipFilePath;
    private final String targetDirectoryPath;

    public ExtractZip(String sourceZipFilePath, String targetDirectoryPath){
        this.sourceZipFilePath = sourceZipFilePath;
        this.targetDirectoryPath = targetDirectoryPath;
    }

    @Override
    public String execute() {
        LOGGER.info("Extracting {} into {}", sourceZipFilePath, targetDirectoryPath);
        final ZipFile zipFile = new ZipFile(sourceZipFilePath);
        try {
            final FileHeader rootFileHeader = zipFile.getFileHeaders().get(0);
            if (rootFileHeader.isDirectory()) {
                final Path currentProjectPath = Paths.get(targetDirectoryPath);
                System.out.println(targetDirectoryPath);
                System.out.println(currentProjectPath);
                final String parentDirectoryPath = currentProjectPath.getParent().toString();
                zipFile.extractAll(parentDirectoryPath);
                final Path extractProjectDirPath = Paths.get(parentDirectoryPath + File.separator + rootFileHeader.getFileName());
                Files.move(extractProjectDirPath, Paths.get(targetDirectoryPath));
            } else {
                zipFile.extractAll(targetDirectoryPath);
            }
        } catch (final IOException unableExtractingEx) {
            throw new IllegalStateException(
                    "Unable to extract zip file " + sourceZipFilePath + " to directory " + targetDirectoryPath,
                    unableExtractingEx);
        }
        return targetDirectoryPath;
    }

}
