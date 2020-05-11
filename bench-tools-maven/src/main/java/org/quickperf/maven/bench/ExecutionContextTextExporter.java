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

package org.quickperf.maven.bench;

import org.quickperf.maven.bench.config.BenchProperties;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class ExecutionContextTextExporter {

    private ExecutionContextTextExporter() {}

    static ExecutionContextTextExporter INSTANCE = new ExecutionContextTextExporter();

    public void writeExecutionContextToTextFile(String dateTimeAsString) {

        String filePath =  BenchProperties.INSTANCE.getExportPathOfMeasures()
                         + File.separator
                         + "execution-context-" +dateTimeAsString + ".txt";

        try (FileWriter writer = new FileWriter(filePath);
             BufferedWriter bw = new BufferedWriter(writer)) {

            int numberOfWarms = BenchProperties.INSTANCE.getNumberOfWarms();
            bw.write("Warm up: " + numberOfWarms);

            bw.newLine();
            bw.newLine();

            String javaVersion = System.getProperty("java.version");
            bw.write("Java version: " + javaVersion);

            bw.newLine();

            String javaVendor = System.getProperty("java.vendor");
            bw.write("Java vendor: " + javaVendor);

            bw.newLine();

            String javaVmName = System.getProperty("java.vm.name");
            bw.write("Java VM name: " + javaVmName);

            bw.newLine();
            bw.newLine();

            SystemInfo systemInfo = new SystemInfo();

            HardwareAbstractionLayer hardware = systemInfo.getHardware();

            CentralProcessor processor = hardware.getProcessor();
            bw.write("Processor: " + processor);

            bw.newLine();

            int logicalProcessorCount = processor.getLogicalProcessorCount();
            bw.write("Logical processor count: " + logicalProcessorCount);

            bw.newLine();
            bw.newLine();

            long totalMemoryInBytes = hardware.getMemory().getTotal();

            long totalMemoryInGigaBytes = (long) (totalMemoryInBytes / Math.pow(1024, 3));

            bw.write("Total memory (giga bytes): " + totalMemoryInGigaBytes);

            bw.newLine();

            long availableMemoryInBytes = systemInfo.getHardware().getMemory().getAvailable();

            double availableMemoryInGigaBytes = availableMemoryInBytes / Math.pow(1024, 3);
            bw.write("Available memory (giga bytes): " + availableMemoryInGigaBytes);

            bw.newLine();
            bw.newLine();

            OperatingSystem os = systemInfo.getOperatingSystem();
            bw.write("OS: " + os);

            bw.flush();

        } catch (IOException e) {
            throw new IllegalStateException("Can't create execution context file", e);
        }

    }

}
