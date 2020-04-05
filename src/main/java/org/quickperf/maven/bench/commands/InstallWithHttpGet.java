package org.quickperf.maven.bench.commands;

import org.quickperf.maven.bench.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
