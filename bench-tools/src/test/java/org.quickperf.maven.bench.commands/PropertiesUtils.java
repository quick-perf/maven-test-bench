package org.quickperf.maven.bench.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class PropertiesUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);

    static Properties loadBenchToolsProperties() {
        final Properties properties = new Properties();

        URL resource = HttpGet.class.getClassLoader().getResource("bench-tools.properties");
        if (resource == null) {
            LOGGER.error("bench-tools.properties does not exist.");
        } else {
            String propertiesFilePath;
            try {
                propertiesFilePath = URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8.toString());
                try (final FileInputStream fileInputStream = new FileInputStream(propertiesFilePath)) {
                    properties.load(fileInputStream);
                } catch (IOException ioException) {
                    LOGGER.error("Access denied to bench-tools.properties.");
                }
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Path to bench-tools.properties contains unknown encoded characters");
            }
        }

        return properties;
    }
}
