package org.checkmarx.codescanner;

import org.checkmarx.codescanner.service.CodeScannerService;
import org.checkmarx.codescanner.util.EnvVarFetcher;
import org.checkmarx.codescanner.util.security.SecurityChecker;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class Main {

    public static void main(String[] args) {
        Set<Integer> securityConfigurations = new TreeSet<>();
        Scanner scanner = new Scanner(System.in);
        CodeScannerService codeScannerService = new CodeScannerService();

        System.out.println("Please enter the source code path to be scanned: ");
        //String sourceCodeDirectory = scanner.nextLine();
        String sourceCodeDirectory = EnvVarFetcher.getEnvStrElseDefault("INPUT_SRC_FILES_DIR",
                "./input_src_files");

        System.out.println("Please enter your desired types of security checks to be performed:");
        System.out.println("(Select from these options: " + SecurityChecker.SECURITY_CHECKER_CONFIGURATIONS + ")");

        // while(scanner.hasNextInt())
        //     securityConfigurations.add(scanner.nextInt());
        securityConfigurations = new HashSet<>(){{ add(1); add(2); add(3); }};

        System.out.println("==== ");
        System.out.println("Path introduced: " + sourceCodeDirectory);
        System.out.println("Security configurations selected: " + securityConfigurations);
        System.out.println("==== \n");

        codeScannerService.run(sourceCodeDirectory, securityConfigurations);
    }

}