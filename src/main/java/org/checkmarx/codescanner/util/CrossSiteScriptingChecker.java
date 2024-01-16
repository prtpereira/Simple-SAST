package org.checkmarx.codescanner.util;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrossSiteScriptingChecker extends SecurityChecker {

    private static final int securityConfigurationID = 1;
    private static final String securityConfigurationName = SECURITY_CHECKER_CONFIGURATIONS.get(securityConfigurationID);

    public CrossSiteScriptingChecker() {
        super();
    }

    public AbstractMap.SimpleEntry<String, Integer> run(String code) {
        String xssRegex = "\\bAlert\\s*\\((.*?)\\)";
        Pattern pattern = Pattern.compile(xssRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(code);

        int numVulns;
        for(numVulns = 0; matcher.find(); numVulns++) {
            System.out.println("Cross-Site Scripting (XSS) detected in file: " + matcher.group());
        }

        return new AbstractMap.SimpleEntry<>(securityConfigurationName, numVulns);
    }

}
