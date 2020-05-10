package org.quickperf.maven.bench.config;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultBenchConfigurationResolverTest {

    private static final String ENV_TEST_KEY = "PATH";
    private static final String MAVEN_VERSION_FROM = "maven.version.from";

    private final DefaultBenchConfigurationResolver resolver = new DefaultBenchConfigurationResolver();

    @Test
    public void getPropertyShouldReadFromDefaultMavenBenchProperties() {
        assertEquals("3.2.5", resolver.resolve(MAVEN_VERSION_FROM));
    }

    @Test
    public void getPropertyShouldReadFromOsEnv() {
        Assert.assertNotNull(resolver.resolve(ENV_TEST_KEY));
    }

}