package org.quickperf.maven.bench.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

class BenchPropertiesFileBasedResolver implements BenchPropertiesResolver {
    private Properties props;

    private BenchPropertiesFileBasedResolver(String fileName) throws IOException {
        this.props = this.loadProperties(fileName);
    }

    static BenchPropertiesFileBasedResolver createBenchPropertiesFileBasedResolver(String fileName)
            throws IOException {
        return new BenchPropertiesFileBasedResolver(fileName);
    }

    private Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new IOException("file (" + fileName + ") not found.");
        }

        String propertiesFilePath = resource.getPath();
        propertiesFilePath = URLDecoder.decode(propertiesFilePath, StandardCharsets.UTF_8.toString());
        try (final FileInputStream fileInputStream = new FileInputStream(propertiesFilePath)) {
            properties.load(fileInputStream);
        }
        return properties;
    }

    @Override
    public String getProperty(String key) {
        return props.getProperty(key);
    }
}
