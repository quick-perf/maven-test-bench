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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class DefaultBenchPropertiesResolver implements BenchPropertiesResolver {

    private static final String CONFIG_FILENAME = "maven-bench.properties";

    private final List<BenchPropertiesResolver> resolvers = new ArrayList<>();

    DefaultBenchPropertiesResolver() {
        resolvers.add(key -> {
            final String name = key.toUpperCase()
                    .replace('.', '_')
                    .replace('-', '_');
            return System.getenv(name);
        });
        addBenchPropertiesFromFile(resolvers, "local." + CONFIG_FILENAME);
        addBenchPropertiesFromFile(resolvers, CONFIG_FILENAME);
    }

    private void addBenchPropertiesFromFile(List<BenchPropertiesResolver> resolvers, String fileName) {
        try {
            resolvers.add(BenchPropertiesFileBasedResolver.createBenchPropertiesFileBasedResolver(fileName));
        } catch (final IOException e) {
            // Ok we accept that a file config may not exist.
        }
    }

    @Override
    public String getProperty(String key) {
        return resolvers.stream()
            .map(benchPropertiesResolver -> benchPropertiesResolver.getProperty(key))
            .filter(Objects::nonNull)
            .findFirst().orElse(null);
    }

}
