package org.checkmarx.codescanner.util.security;

import java.util.AbstractMap;

public class CrossSiteScriptingChecker extends SecurityChecker {

    private static final int securityConfigurationID = 1;
    private static final String configurationName = SECURITY_CHECKER_CONFIGURATIONS.get(securityConfigurationID);

    public CrossSiteScriptingChecker() {
        super();
    }

    public AbstractMap.SimpleEntry<String, Integer> detect(String code) {
        String xssRegex = "\\bAlert\\s*\\((.*?)\\)";

        return matchRegexPattern(xssRegex, code, configurationName);
    }

}
