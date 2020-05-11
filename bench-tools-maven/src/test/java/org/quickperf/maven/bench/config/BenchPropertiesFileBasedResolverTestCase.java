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

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

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