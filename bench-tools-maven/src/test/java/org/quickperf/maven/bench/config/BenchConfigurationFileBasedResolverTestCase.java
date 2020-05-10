package org.quickperf.maven.bench.config;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class BenchConfigurationFileBasedResolverTestCase {

    @Test
    public void createBenchPropertiesFileBasedResolverShouldReturnResolverWhenFileExists() throws IOException {
        Assert.assertNotNull(BenchConfigurationFileBasedResolver.createBenchPropertiesFileBasedResolver("org/quickperf/maven/bench/config/test.properties"));
    }

    @Test(expected = IOException.class)
    public void createBenchPropertiesFileBasedResolverShouldReturnThrowIOExceptionWhenFileDoesNotExist() throws IOException {
        BenchConfigurationFileBasedResolver.createBenchPropertiesFileBasedResolver("org/quickperf/maven/bench/config/fileDoesNotExists.properties");
    }

    @Test
    public void resolve() throws IOException {
        BenchConfigurationFileBasedResolver props = BenchConfigurationFileBasedResolver.createBenchPropertiesFileBasedResolver("org/quickperf/maven/bench/config/test.properties");
        Assert.assertNull(props.resolve("doesNotExist"));
        Assert.assertEquals("Hello World!", props.resolve("person.say"));
    }

}