import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LongSummaryStatistics;

public class AllocationCsvExporter {

    public static final AllocationCsvExporter INSTANCE = new AllocationCsvExporter();

    private AllocationCsvExporter() {}

    public void writeAllocationsToCsv(Maven3Version maven3Version, long[] allocations, String resultFilePath) throws IOException {

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
        String[] csvHeaders = new String[(2 * numberOfAllocations) + 5];
        csvHeaders[0] = "Maven version";
        csvHeaders[1] = "Average (bytes)";
        csvHeaders[2] = "Average (Gb)";
        csvHeaders[3] = "Min (bytes)";
        csvHeaders[4] = "Max (bytes)";

        for (int i = 1; i < numberOfAllocations + 1; i++) {
            csvHeaders[i + 4] = "Allocation" + " " + i + " "+ "(bytes)";
        }

        for (int i = 1; i < numberOfAllocations + 1; i++) {
            csvHeaders[i + 4 + numberOfAllocations] = "Allocation" + " " + i + " "+ "(Gb)";
        }
        return csvHeaders;
    }

    private List<Object> buildCsvRecord(Maven3Version maven3Version, long[] allocations) {

        List<Object> csvRecord = new ArrayList<>((2 * allocations.length) + 5);

        csvRecord.add(maven3Version);

        LongSummaryStatistics allocationStatistics = Arrays.stream(allocations).summaryStatistics();
        double averageAllocationInBytes = allocationStatistics.getAverage();
        csvRecord.add(averageAllocationInBytes);
        csvRecord.add(averageAllocationInBytes / Math.pow(1024, 3));
        csvRecord.add(allocationStatistics.getMin());
        csvRecord.add(allocationStatistics.getMax());

        for (int i = 0; i < allocations.length; i++) {
            csvRecord.add(allocations[i]);
        }

        for (int i = 0; i < allocations.length; i++) {
            csvRecord.add(allocations[i] / Math.pow(1024, 3));
        }

        return csvRecord;
    }

}
