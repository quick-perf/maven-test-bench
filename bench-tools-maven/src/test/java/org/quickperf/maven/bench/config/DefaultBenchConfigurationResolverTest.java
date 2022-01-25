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

import static org.junit.Assert.assertEquals;

public class DefaultBenchConfigurationResolverTest {

    private static final String ENV_TEST_KEY = "PATH";

    private static final String MAVEN_VERSION_FROM = "maven.version.from";
    private static final String SYSTEM_PROPERTIES_KEY = "hello.world";

    private final DefaultBenchConfigurationResolver resolver = new DefaultBenchConfigurationResolver();

    @Test
    public void getPropertyShouldReadFromDefaultMavenBenchProperties() {
        assertEquals("3.2.5", resolver.resolve(MAVEN_VERSION_FROM));
    }

    @Test
    public void getPropertyShouldReadFromOsEnv() {
        Assert.assertNotNull(resolver.resolve(ENV_TEST_KEY));
    }

    @Test
    public void getPropertyShouldReadFromSystemProperties() {
        final String expectedValue = "Foo Bar";
        System.setProperty(SYSTEM_PROPERTIES_KEY, expectedValue);

        final String resolve = resolver.resolve(SYSTEM_PROPERTIES_KEY);

        assertEquals(expectedValue, resolve);
    }


}