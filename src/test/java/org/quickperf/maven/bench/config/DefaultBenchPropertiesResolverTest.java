package org.quickperf.maven.bench.config;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultBenchPropertiesResolverTest {

    private static final String ENV_TEST_KEY = "PATH";
    private static final String MAVEN_VERSION_FROM = "maven.version.from";

    private final DefaultBenchPropertiesResolver resolver = new DefaultBenchPropertiesResolver();

    @Test
    public void getPropertyShouldReadFromDefaultMavenBenchProperties() {
        Assert.assertEquals("3.2.5", resolver.getProperty(MAVEN_VERSION_FROM));
    }

    @Test
    public void getPropertyShouldReadFromOsEnv() {
        Assert.assertNotNull(resolver.getProperty(ENV_TEST_KEY));
    }


}