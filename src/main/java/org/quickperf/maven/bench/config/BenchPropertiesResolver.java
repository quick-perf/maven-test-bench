package org.quickperf.maven.bench.config;

@FunctionalInterface
interface BenchPropertiesResolver {
    String getProperty(String key);
}
