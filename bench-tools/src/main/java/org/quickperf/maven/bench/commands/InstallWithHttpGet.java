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

import org.quickperf.maven.bench.Command;

public class InstallWithHttpGet implements Command {
    private String sourceUrl;
    private String targetPath;

    public InstallWithHttpGet(String sourceUrl, String targetPath) {
        this.sourceUrl = sourceUrl;
        this.targetPath = targetPath;
    }

    @Override
    public String execute() {
        final String targetFilePath = new HttpGet(sourceUrl, targetPath).execute();
        return new ExtractZip(targetFilePath, targetPath).execute();
    }
}
