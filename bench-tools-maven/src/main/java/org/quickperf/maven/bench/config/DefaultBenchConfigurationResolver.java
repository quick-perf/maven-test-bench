package org.quickperf.maven.bench.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Default strategy to resolve all configuration parameters for maven benchmark in the following order:
 *
 * <ol>
 *    <li>from Environment Variable</li>
 *    <li>from a local configuration file named <pre>local-maven-bench.properties</pre></li>
 *    <li>from the default configuration file named <pre>maven-bench.properties</pre></li>
 * </ol>
 */
class DefaultBenchConfigurationResolver implements BenchConfigurationResolver {

    private static final String CONFIG_FILENAME = "maven-bench.properties";

    private final List<BenchConfigurationResolver> resolvers = new ArrayList<>();

    DefaultBenchConfigurationResolver() {
        resolvers.add(key -> {
            final String name = key.toUpperCase()
                    .replace('.', '_')
                    .replace('-', '_');
            return System.getenv(name);
        });
        addBenchPropertiesFromFile(resolvers, "local." + CONFIG_FILENAME);
        addBenchPropertiesFromFile(resolvers, CONFIG_FILENAME);
    }

    private void addBenchPropertiesFromFile(List<BenchConfigurationResolver> resolvers, String fileName) {
        try {
            resolvers.add(BenchConfigurationFileBasedResolver.createBenchPropertiesFileBasedResolver(fileName));
        } catch (final IOException e) {
            // Ok we accept that a file config may not exist.
        }
    }

    @Override
    public String resolve(String parameter) {
        return resolvers.stream()
            .map(benchConfigurationResolver -> benchConfigurationResolver.resolve(parameter))
            .filter(Objects::nonNull)
            .findFirst().orElse(null);
    }

}
