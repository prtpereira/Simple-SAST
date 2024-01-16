package org.checkmarx.codescanner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.checkmarx.codescanner.model.Vulnerability;
import org.checkmarx.codescanner.util.security.CrossSiteScriptingChecker;
import org.checkmarx.codescanner.util.security.SQLInjectionChecker;
import org.checkmarx.codescanner.util.security.SecurityChecker;
import org.checkmarx.codescanner.util.security.SensitiveDataChecker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class CodeScannerService {

    public void scanFiles(String directoryPath, Set<Integer> scanSecurityConfigurations) {

        List<SecurityChecker> securityCheckers = buildSecurityCheckers(scanSecurityConfigurations);
        List<Vulnerability> vulnerabilities = new ArrayList<>();

        try {
            Files.walk(Paths.get(directoryPath))
                .filter(Files::isRegularFile)
                .forEach(path -> {

                    try {
                        File file = new File(path.toString());
                        FileInputStream fis = new FileInputStream(file);
                        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                        BufferedReader br = new BufferedReader(isr);

                        String line;
                        for(int lineNumber = 1; (line = br.readLine()) != null; lineNumber++)  {
                            String code = line;
                            int finalLineNumber = lineNumber;

                            securityCheckers.forEach(securityChecker -> {
                                AbstractMap.SimpleEntry<String, Integer> detectedVulns = securityChecker.run(code);
                                IntStream.range(0, detectedVulns.getValue()).forEach(n -> vulnerabilities.add(
                                        new Vulnerability(path.toString(), detectedVulns.getKey(), finalLineNumber))
                                );
                            });
                        }

                        br.close();

                    } catch (Exception e) {
                        System.err.println("An error occurred while trying to check the vulnerabilities on the source code. Error: " + e.getMessage());
                    }
                });

            writeVulnerabilitiesToFile(vulnerabilities, "plaintext", "C:\\Users\\apere\\Repos\\challenges2023\\sast-checkmarx\\Simple-SAST\\output_vulnerabilities\\output.txt");
            writeVulnerabilitiesToFile(vulnerabilities, "json", "C:\\Users\\apere\\Repos\\challenges2023\\sast-checkmarx\\Simple-SAST\\output_vulnerabilities\\output.json");

        } catch (IOException e) {
            System.err.println("Error scanning files in directory: " + e.getMessage());
        }
    }

    private List<SecurityChecker> buildSecurityCheckers(Set<Integer> scanSecurityConfigurations) {
        List<SecurityChecker> securityCheckers = new ArrayList<>();

        scanSecurityConfigurations.forEach( id -> {
            switch (id) {
                case 1: securityCheckers.add( new CrossSiteScriptingChecker() ); break;
                case 2: securityCheckers.add( new SensitiveDataChecker() ); break;
                case 3: securityCheckers.add( new SQLInjectionChecker() ); break;
                default: break;
            }
        });

        return securityCheckers;
    }

    public void writeVulnerabilitiesToFile(List<Vulnerability> vulnerabilities, String fileFormat, String filePath) { //or write all lines (previsouly stored in memory)
        switch (fileFormat) {
            case "plaintext": writeVulnerabilitiesToTxt(vulnerabilities, filePath); break;
            case "json": writeVulnerabilitiesToJson(vulnerabilities, filePath); break;
            default: break;
        }
    }

    private void writeVulnerabilitiesToTxt(List<Vulnerability> vulnerabilities, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            vulnerabilities.forEach( vulnerability -> {
                try {
                    writer.write("[" + vulnerability.getVulnerabilityType() + "] in file \"" + vulnerability.getFileName() + "\" on line " + vulnerability.getLineNumber() + "\n");
                } catch (IOException e) {
                    System.err.println("Error writing vulnerabilities to plaintext file: " + e.getMessage());
                }
            });

            System.out.println("Plaintext data has been successfully written to: " + filePath);

        } catch (Exception e) {
            System.err.println("Error writing vulnerabilities to plaintext file: " + e.getMessage());
        }
    }

    private void writeVulnerabilitiesToJson(List<Vulnerability> vulnerabilities, String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            objectMapper.writeValue(new File(filePath), vulnerabilities);

            System.out.println("JSON data has been successfully written to: " + filePath);

        } catch (IOException e) {
            System.err.println("Error writing vulnerabilities to JSON file: " + e.getMessage());
        }
    }

}
