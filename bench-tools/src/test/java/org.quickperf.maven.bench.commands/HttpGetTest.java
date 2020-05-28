package org.quickperf.maven.bench.commands;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.quickperf.maven.bench.commands.PropertiesUtils.loadBenchToolsProperties;

public class HttpGetTest {
    private static final String SOURCE_URL = "org.quickperf.maven.bench.commands.httpGet.sourceUrl";
    private static final String TARGET_PATH = "org.quickperf.maven.bench.commands.httpGet.targetPath";
    private static final String WRONG_SOURCE_URL = "org.quickperf.maven.bench.commands.httpGet.wrongSourceUrl";
    private static final String NOT_EXISTS_SOURCE_URL = "org.quickperf.maven.bench.commands.httpGet.notExistsSourceUrl";
    private static final Properties PROPERTIES = loadBenchToolsProperties();

    @Test
    public void executeShouldReturnDownloadedFilePath() {
        final HttpGet command = new HttpGet(PROPERTIES.getProperty(SOURCE_URL), PROPERTIES.getProperty(TARGET_PATH));

        final String result = command.execute();

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(PROPERTIES.getProperty(TARGET_PATH) + "/index.txt");
    }

    @Test
    public void executeShouldThrowIllegalArgumentExWhenSourceUrlIsNotAnUrl() {
        final HttpGet command = new HttpGet(PROPERTIES.getProperty(WRONG_SOURCE_URL), PROPERTIES.getProperty(TARGET_PATH));

        assertThatThrownBy(command::execute).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void executeShouldThrowIllegalStateExWhenSourceUrlCouldNotBeDownloaded() {
        final HttpGet command = new HttpGet(PROPERTIES.getProperty(NOT_EXISTS_SOURCE_URL), PROPERTIES.getProperty(TARGET_PATH));

        assertThatThrownBy(command::execute).isInstanceOf(IllegalStateException.class);
    }

}