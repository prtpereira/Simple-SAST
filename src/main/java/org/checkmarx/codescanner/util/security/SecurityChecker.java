package org.checkmarx.codescanner.util.security;

import java.util.AbstractMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SecurityChecker {

    public static final Map<Integer, String> SECURITY_CHECKER_CONFIGURATIONS = Map.of(1, "Cross site scripting",
            2, "Sensitive data exposure",
            3, "SQL Injection");

    private static final int securityConfigurationID = 0;
    private static final String securityConfigurationName = "";

    public SecurityChecker() {
        super();
    }

    public int getSecurityConfigurationID() {
        return securityConfigurationID;
    }

    public String getSecurityConfigurationName() {
        return securityConfigurationName;
    }

    public abstract AbstractMap.SimpleEntry<String, Integer> detect(String code);

    protected AbstractMap.SimpleEntry<String, Integer> matchRegexPattern(String regexPattern, String code, String configurationName) {
        Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(code);

        int numVulns;
        for(numVulns = 0; matcher.find(); numVulns++) {
            System.out.println(configurationName + " detected. Found pattern: " + matcher.group());
        }

        return new AbstractMap.SimpleEntry<>(configurationName, numVulns);
    }
}
