package org.quickperf.maven.bench.commands;

import org.quickperf.maven.bench.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
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

public class HttpGet implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpGet.class);

    private final String sourceUrl;
    private final String targetPath;

    public HttpGet(String sourceUrl, String targetPath) {
        this.sourceUrl = sourceUrl;
        this.targetPath = targetPath;
    }

    @Override
    public String execute() {
        LOGGER.info("Downloading to {} from HTTP GET {}", targetPath, sourceUrl);
        File downloadDir = new File(targetPath);
        if(!downloadDir.exists()) {
            downloadDir.mkdir();
        }

        URL url = buildURL(sourceUrl);
        String fileName = getDirectoryNameFromUrl(url);
        final String downloadedFilePath = String.format("%s/%s", targetPath, fileName);
        try (
                final FileOutputStream fileOutputStream = new FileOutputStream(downloadedFilePath);
                final FileChannel fileChannel = fileOutputStream.getChannel()
        ) {
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (final IOException downloadEx) {
            throw new IllegalStateException("Could not download project under test.", downloadEx);
        }

        return downloadedFilePath;
    }

    private URL buildURL(String sourceUrlAsString) {
        try {
            return new URL(sourceUrlAsString);
        } catch (MalformedURLException malformedUrlEx) {
            throw new IllegalArgumentException("source url is not well formatted.", malformedUrlEx);
        }
    }

    private String getDirectoryNameFromUrl(URL url) {
        return url.getFile().substring(url.getFile().lastIndexOf("/") + 1);
    }

}
