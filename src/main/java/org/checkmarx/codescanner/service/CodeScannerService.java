package org.checkmarx.codescanner.service;

import org.checkmarx.codescanner.model.Vulnerability;
import org.checkmarx.codescanner.util.VulnerabilityWriter;
import org.checkmarx.codescanner.util.security.CrossSiteScriptingChecker;
import org.checkmarx.codescanner.util.security.SQLInjectionChecker;
import org.checkmarx.codescanner.util.security.SecurityChecker;
import org.checkmarx.codescanner.util.security.SensitiveDataChecker;

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

public final class CodeScannerService {

    private final VulnerabilityWriter vulnerabilityWriter;
    private final ExecutorService threadPool;

    public CodeScannerService(VulnerabilityWriter vulnerabilityWriter, ExecutorService threadPool) {
        this.vulnerabilityWriter = vulnerabilityWriter;
        this.threadPool = threadPool;
    }

    public CodeScannerService() {
        this.vulnerabilityWriter = new VulnerabilityWriter();
        this.threadPool = Executors.newCachedThreadPool();
    }


    public void run(String inputPath, String outputPath, Set<Integer> scanSecurityConfigurations) {

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
            vulnerabilityWriter.writeToFile(vulnerabilities, "plaintext", outputPath);
            vulnerabilityWriter.writeToFile(vulnerabilities, "json",      outputPath);
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

}
