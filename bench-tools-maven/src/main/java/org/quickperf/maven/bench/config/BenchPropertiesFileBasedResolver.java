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

        String propertiesFilePath = URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8.toString());
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
