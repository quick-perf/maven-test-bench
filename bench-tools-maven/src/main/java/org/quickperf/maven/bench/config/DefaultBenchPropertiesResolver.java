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
