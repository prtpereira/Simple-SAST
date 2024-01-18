package org.checkmarx.codescanner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.checkmarx.codescanner.model.Vulnerability;
import org.checkmarx.codescanner.util.security.CrossSiteScriptingChecker;
import org.checkmarx.codescanner.util.security.SQLInjectionChecker;
import org.checkmarx.codescanner.util.security.SecurityChecker;
import org.checkmarx.codescanner.util.security.SensitiveDataChecker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CodeScannerService {

    public void run(String inputPath, String outputPath, Set<Integer> scanSecurityConfigurations) {

        ExecutorService threadPool = Executors.newCachedThreadPool();
        List<Future<List<Vulnerability>>> futures = new ArrayList<>();

        List<SecurityChecker> securityCheckers = buildSecurityCheckers(scanSecurityConfigurations);
        List<Vulnerability> vulnerabilities = new ArrayList<>();

        try {
            Files.walk(Paths.get(inputPath))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    Future<List<Vulnerability>> futureResult = threadPool.submit(new VulnerabilityFinderCallable(file.toString(), securityCheckers));
                    futures.add(futureResult);
                });
        } catch (IOException e) {
            System.err.println("Error scanning files in directory. Error: " + e.getMessage());
            threadPool.shutdown();
            return;
        }

        for (Future<List<Vulnerability>> future : futures) {
            try {
                List<Vulnerability> result = future.get();
                vulnerabilities.addAll(result);

            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error fetching pending result from thread. Error:" + e.getMessage());
            }
        }

        if (vulnerabilities.isEmpty()) {
            System.out.println("No vulnerabilities detected. Not writing any data to output files...");
        } else {
            writeVulnerabilitiesToFile(vulnerabilities, "plaintext", outputPath);
            writeVulnerabilitiesToFile(vulnerabilities, "json",      outputPath);
        }

        threadPool.shutdown();
    }

    private List<SecurityChecker> buildSecurityCheckers(Set<Integer> scanSecurityConfigurations) {
        List<SecurityChecker> securityCheckers = new ArrayList<>();

        scanSecurityConfigurations.forEach( id -> {
            switch (id) {
                case 1 -> securityCheckers.add(new CrossSiteScriptingChecker());
                case 2 -> securityCheckers.add(new SensitiveDataChecker());
                case 3 -> securityCheckers.add(new SQLInjectionChecker());
                default -> System.err.println("There is not an available Security Checker Scanner with the id '" + id + "'");
            }
        });

        return securityCheckers;
    }

    public void writeVulnerabilitiesToFile(List<Vulnerability> vulnerabilities, String fileFormat, String outputPath) {
        switch (fileFormat) {
            case "plaintext" -> writeVulnerabilitiesToTxt(vulnerabilities, outputPath + "/vulnerabilities.txt");
            case "json"      -> writeVulnerabilitiesToJson(vulnerabilities, outputPath + "/vulnerabilities.json");
            default -> System.err.println("File format '" + fileFormat + "' is not supported.");
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

        } catch (IOException e) {
            System.err.println("File cannot be created or opened: " + filePath + " Error: " + e.getMessage());
        }
    }

    private void writeVulnerabilitiesToJson(List<Vulnerability> vulnerabilities, String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            objectMapper.writeValue(new File(filePath), vulnerabilities);

            System.out.println("JSON data has been successfully written to: " + filePath);

        } catch (IOException e) {
            System.out.println("Error writing vulnerabilities to JSON file: " + e.getMessage());
        }
    }

}
