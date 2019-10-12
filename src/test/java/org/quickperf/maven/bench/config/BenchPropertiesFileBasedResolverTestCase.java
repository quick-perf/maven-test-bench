package org.quickperf.maven.bench.config;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.*;

public class BenchPropertiesFileBasedResolverTestCase {


    @Test
    public void createBenchPropertiesFileBasedResolverShouldReturnResolverWhenFileExists() throws IOException {
        Assert.assertNotNull(BenchPropertiesFileBasedResolver.createBenchPropertiesFileBasedResolver("org/quickperf/maven/bench/config/test.properties"));
    }

    @Test(expected = IOException.class)
    public void createBenchPropertiesFileBasedResolverShouldReturnThrowIOExceptionWhenFileDoesNotExist() throws IOException {
        BenchPropertiesFileBasedResolver.createBenchPropertiesFileBasedResolver("org/quickperf/maven/bench/config/fileDoesNotExists.properties");
    }

    @Test
    public void getProperty() throws IOException {
        BenchPropertiesFileBasedResolver props = BenchPropertiesFileBasedResolver.createBenchPropertiesFileBasedResolver("org/quickperf/maven/bench/config/test.properties");
        Assert.assertNull(props.getProperty("doesNotExist"));
        Assert.assertEquals("Hello World!", props.getProperty("person.say"));
    }
}