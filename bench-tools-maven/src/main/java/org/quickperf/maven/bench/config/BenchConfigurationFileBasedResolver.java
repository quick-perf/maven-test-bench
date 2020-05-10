package org.quickperf.maven.bench.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Strategy to resolve all configuration from a properties file.
 */
class BenchConfigurationFileBasedResolver implements BenchConfigurationResolver {
    private Properties props;

    private BenchConfigurationFileBasedResolver(String fileName) throws IOException {
        this.props = this.loadProperties(fileName);
    }

    static BenchConfigurationFileBasedResolver createBenchPropertiesFileBasedResolver(String fileName)
            throws IOException {
        return new BenchConfigurationFileBasedResolver(fileName);
    }

    private Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new IOException("file (" + fileName + ") not found.");
        }

        String propertiesFilePath = URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8.toString());
        try (final FileInputStream fileInputStream = new FileInputStream(propertiesFilePath)) {
            properties.load(fileInputStream);
        }
        return properties;
    }

    @Override
    public String resolve(String parameter) {
        return props.getProperty(parameter);
    }
}
