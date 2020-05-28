package org.quickperf.maven.bench.commands;

import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertiesUtilsTest {

    public static final String KEY_TEST = "org.quickperf.maven.bench.commands.propertiesUtils.test";

    @Test
    public void loadBenchToolsProperties() {
        final Properties properties = PropertiesUtils.loadBenchToolsProperties();

        assertThat(properties).isNotNull();
        assertThat(properties.getProperty(KEY_TEST)).isEqualTo("Hello World");
    }

}