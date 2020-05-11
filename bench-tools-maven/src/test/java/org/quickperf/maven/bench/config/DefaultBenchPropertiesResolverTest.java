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

public class DefaultBenchPropertiesResolverTest {

    private static final String ENV_TEST_KEY = "PATH";

    private static final String MAVEN_VERSION_FROM = "maven.version.from";

    private final DefaultBenchPropertiesResolver resolver = new DefaultBenchPropertiesResolver();

    @Test
    public void getPropertyShouldReadFromDefaultMavenBenchProperties() {
        assertEquals("3.2.5", resolver.getProperty(MAVEN_VERSION_FROM));
    }

    @Test
    public void getPropertyShouldReadFromOsEnv() {
        Assert.assertNotNull(resolver.getProperty(ENV_TEST_KEY));
    }

}