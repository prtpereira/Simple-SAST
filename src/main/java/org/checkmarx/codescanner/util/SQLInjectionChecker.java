package org.checkmarx.codescanner.util;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLInjectionChecker extends SecurityChecker {

    private static final int securityConfigurationID = 3;
    private static final String securityConfigurationName = SECURITY_CHECKER_CONFIGURATIONS.get(securityConfigurationID);

    public SQLInjectionChecker() {
        super();
    }

    public AbstractMap.SimpleEntry<String, Integer> run(String code) {
        String sqlInjectionRegex = "\"[^\"']*\\b(SELECT|WHERE|%s)\\b[^\"']*\"";

        Pattern pattern = Pattern.compile(sqlInjectionRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(code);

        int numVulns;
        for(numVulns = 0; matcher.find(); numVulns++) {
            System.out.println("SQL injection detected in file: " + matcher.group());
        }

        return new AbstractMap.SimpleEntry<>(securityConfigurationName, numVulns);
    }

}
