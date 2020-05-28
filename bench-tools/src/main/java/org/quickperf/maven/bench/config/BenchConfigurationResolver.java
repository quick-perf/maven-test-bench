package org.quickperf.maven.bench.config;

/**
 * Define the way to resolve the value of a configuration parameter.
 */
@FunctionalInterface
interface BenchConfigurationResolver {
    String resolve(String parameter);
}
