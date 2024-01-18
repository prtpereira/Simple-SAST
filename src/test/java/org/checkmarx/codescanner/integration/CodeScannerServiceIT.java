package org.checkmarx.codescanner.integration;

import org.checkmarx.codescanner.service.CodeScannerService;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CodeScannerServiceIT {

    CodeScannerService codeScannerService;

    @Test
    public void testGenerateOutputFilesWithVulnerabilities() {
        codeScannerService = new CodeScannerService();

        String mockedVulnerabilitiesTxtFile = "./src/test/mocked_vulnerabilities/vulnerabilities.txt";
        String mockedVulnerabilitiesJsonFile = "./src/test/mocked_vulnerabilities/vulnerabilities.json";
        String inputDir = "./src/test/input";
        String outputDir = "./src/test/output";
        Set<Integer> scanConfigurations = new HashSet<>(){{ add(1); add(2); add(3); }};

        codeScannerService.run(inputDir, outputDir, scanConfigurations);

        Assert.assertTrue( filesAreEqual(mockedVulnerabilitiesTxtFile, "src/test/output/vulnerabilities.txt") );
        Assert.assertTrue( filesAreEqual(mockedVulnerabilitiesJsonFile, "src/test/output/vulnerabilities.json") );
    }

    private static boolean filesAreEqual(String filePath1, String filePath2) {
        Path path1 = Paths.get(filePath1);
        Path path2 = Paths.get(filePath2);

        try {
            byte[] content1 = Files.readAllBytes(path1);
            byte[] content2 = Files.readAllBytes(path2);

            return Arrays.equals(content1, content2);
        } catch (IOException e) {
            return false;
        }
    }

}
