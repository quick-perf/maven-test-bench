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

package org.quickperf.maven.bench.projects;

import org.quickperf.maven.bench.config.BenchConfiguration;

import java.io.File;

public enum Maven3Version {
     V_3_0_4("3.0.4", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_0_5("3.0.5", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_1_0("3.1.0", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_1_1("3.1.1", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_2_1("3.2.1", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_2_2("3.2.2", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_2_3("3.2.3", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_2_5("3.2.5", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_3_1("3.3.1", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_3_3("3.3.3", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_3_9("3.3.9", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_5_0("3.5.0", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_5_2("3.5.2", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_5_3("3.5.3", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_5_4("3.5.4", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_6_0("3.6.0", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_6_1("3.6.1", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_6_2("3.6.2", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,V_3_6_3("3.6.3", UrlPatterns.HTTP_RELEASE_DOWNLOAD)
    ,HEAD("master", UrlPatterns.HTTP_GIT_REPO)
    ;

    private final String numVersion;
    private final String urlAsString;
    private final InstallProcess installProcess;

    Maven3Version(String numVersion, String urlPattern) {
        this.numVersion = numVersion;
        this.urlAsString = String.format(urlPattern, numVersion, numVersion);
        this.installProcess = InstallProcess.parse(urlPattern);
    }

    public String getMavenPath() {
        String mavenBinariesPath = BenchConfiguration.INSTANCE.getMavenBinariesPath();
        return mavenBinariesPath + File.separator + "apache-maven-" + numVersion;
    }

    public String getUrlAsString() {
        return urlAsString;
    }

    public String getNumVersion() {
        return numVersion;
    }

    public InstallProcess getInstallProcess() {
        return installProcess;
    }

    @Override
    public String toString() {
        return "Maven" + " " + numVersion;
    }

    public enum InstallProcess {
        GIT,
        HTTP;

        public static InstallProcess parse(String urlPattern) {
            final InstallProcess installProcess;
            switch (urlPattern) {
                case UrlPatterns.HTTP_GIT_REPO:
                    installProcess = GIT;
                    break;
                case UrlPatterns.HTTP_RELEASE_DOWNLOAD:
                default:
                    installProcess = HTTP;
                    break;
            }
            return installProcess;
        }
    }
    
    private static class UrlPatterns {
        private static final String HTTP_RELEASE_DOWNLOAD = "https://archive.apache.org/dist/maven/maven-3/%s/binaries/apache-maven-%s-bin.zip";
        private static final String HTTP_GIT_REPO = "https://gitbox.apache.org/repos/asf/maven.git";
    }

}
