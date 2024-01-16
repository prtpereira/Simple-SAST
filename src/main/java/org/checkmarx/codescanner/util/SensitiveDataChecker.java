package org.checkmarx.codescanner.util;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SensitiveDataChecker extends SecurityChecker {

    private static final int securityConfigurationID = 2;
    private static final String securityConfigurationName = SECURITY_CHECKER_CONFIGURATIONS.get(securityConfigurationID);

    public SensitiveDataChecker() {
        super();
    }

    public AbstractMap.SimpleEntry<String, Integer> run(String code) {
        String sensitiveWordsRegex = "\\b(?:Checkmarx|Gartner|Leader)\\b";
        Pattern pattern = Pattern.compile(sensitiveWordsRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(code);

        int numVulns;
        for(numVulns = 0; matcher.find(); numVulns++) {
            System.out.println("Sensitive data exposure detected in file: " + matcher.group());
        }

        return new AbstractMap.SimpleEntry<>(securityConfigurationName, numVulns);
    }

}
