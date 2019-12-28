package org.quickperf.maven.bench;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.quickperf.maven.bench.projects.Maven3Version;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.stream.Collectors;

public class AllocationCsvExporter {

    public static final AllocationCsvExporter INSTANCE = new AllocationCsvExporter();

    private AllocationCsvExporter() {}

    public void writeAllocationsToCsv(Maven3Version maven3Version, AllocationTimePair[] allocations, String resultFilePath) throws IOException {

        int numberOfAllocations = allocations.length;
        CSVFormat csvFormat = buildCsvFormat(resultFilePath, numberOfAllocations);

        FileWriter fileWriter = new FileWriter(resultFilePath, true);
        try (CSVPrinter csvPrinter = new CSVPrinter(fileWriter, csvFormat)) {
            List<Object> csvRecord = buildCsvRecord(maven3Version, allocations);
            csvPrinter.printRecord(csvRecord);
        }

    }

    private CSVFormat buildCsvFormat(String resultFilePath, int numberOfAllocations) {
        if (new File(resultFilePath).exists()) {
            return CSVFormat.DEFAULT;
        }
        String[] csvHeaders = buildCsvHeaders(numberOfAllocations);
        return CSVFormat.DEFAULT.withHeader(csvHeaders);
    }

    private String[] buildCsvHeaders(int numberOfAllocations) {
        String[] csvHeaders = new String[(3 * numberOfAllocations) +8];
        csvHeaders[0] = "Maven version";
        csvHeaders[1] = "Average (bytes)";
        csvHeaders[2] = "Average (Gb)";
        csvHeaders[3] = "Min (bytes)";
        csvHeaders[4] = "Max (bytes)";
        csvHeaders[5] = "Average length(seconds)";
        csvHeaders[6] = "Min length (seconds)";
        csvHeaders[7] = "Max length (seconds)";

        for (int i = 1; i < numberOfAllocations + 1; i++) {
            csvHeaders[i + 7] = "Allocation" + " " + i + " "+ "(bytes)";
        }
        
        for (int i = 1; i < numberOfAllocations + 1; i++) {
        	csvHeaders[i + 7+ numberOfAllocations] = "Length" + " " + i + " "+ "(seconds)";
        }

        for (int i = 1; i < numberOfAllocations + 1; i++) {
            csvHeaders[i + 7 + numberOfAllocations*2] = "Allocation" + " " + i + " "+ "(Gb)";
        }
        return csvHeaders;
    }

    private List<Object> buildCsvRecord(Maven3Version maven3Version, AllocationTimePair[] input) {

        List<Object> csvRecord = new ArrayList<>((2 * input.length) + 5);

        csvRecord.add(maven3Version);

        LongSummaryStatistics allocationStatistics = Arrays.stream(input).collect(Collectors.summarizingLong(AllocationTimePair::getAllocationInBytes));
        double averageAllocationInBytes = allocationStatistics.getAverage();
        csvRecord.add(averageAllocationInBytes);
        csvRecord.add(averageAllocationInBytes / Math.pow(1024, 3));
        csvRecord.add(allocationStatistics.getMin());
        csvRecord.add(allocationStatistics.getMax());

        LongSummaryStatistics lengthStatistics = Arrays.stream(input).collect(Collectors.summarizingLong(AllocationTimePair::getLengthInSeconds));
        double averageAllocationInSeconds = lengthStatistics.getAverage();
        csvRecord.add(averageAllocationInSeconds);
        csvRecord.add(lengthStatistics.getMin());
        csvRecord.add(lengthStatistics.getMax());
        
        for (int i = 0; i < input.length; i++) {
            csvRecord.add(input[i].getAllocationInBytes());
        }
        
        for (int i = 0; i < input.length; i++) {
        	csvRecord.add(input[i].getLengthInSeconds());
        }         
        
        for (int i = 0; i < input.length; i++) {
            csvRecord.add(input[i].getAllocationInBytes() / Math.pow(1024, 3));
        }
        
        

        return csvRecord;
    }

}
