package org.quickperf.maven.bench.config;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultBenchPropertiesResolverTest {

    private static final String SYSTEM_PROPERY_TEST_KEY = "does.not.exist";
    private static final String ENV_TEST_KEY = "PATH";
    private static final String MAVEN_VERSION_FROM = "maven.version.from";

    private final DefaultBenchPropertiesResolver resolver = new DefaultBenchPropertiesResolver();

    @After
    public void tearDown() {
        System.clearProperty(SYSTEM_PROPERY_TEST_KEY);
        System.clearProperty(MAVEN_VERSION_FROM);
    }

    @Test
    public void getPropertyShouldReadFromDefaultMavenBenchProperties() {
        Assert.assertEquals("3.2.5", resolver.getProperty(MAVEN_VERSION_FROM));
    }

    @Test
    public void getPropertyShouldReadFromJavaSystemProperties() {
        Assert.assertNull(resolver.getProperty(SYSTEM_PROPERY_TEST_KEY));
        final String value = "Nop I am here";
        System.setProperty(SYSTEM_PROPERY_TEST_KEY, value);
        Assert.assertEquals(value, resolver.getProperty(SYSTEM_PROPERY_TEST_KEY));
    }

    @Test
    public void getPropertyShouldReadFromOsEnv() {
        Assert.assertNotNull(resolver.getProperty(ENV_TEST_KEY));
    }

    @Test
    public void getPropertyShouldPrioritizeSystemPropertyOverFile() {
        Assert.assertEquals("3.2.5", resolver.getProperty(MAVEN_VERSION_FROM));
        System.setProperty(MAVEN_VERSION_FROM, "3.6.0");
        Assert.assertEquals("3.6.0", resolver.getProperty(MAVEN_VERSION_FROM));
    }

}