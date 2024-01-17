package org.checkmarx.codescanner.util.security;

import java.util.AbstractMap;

public class SensitiveDataChecker extends SecurityChecker {

    private static final int securityConfigurationID = 2;
    private static final String securityConfigurationName = SECURITY_CHECKER_CONFIGURATIONS.get(securityConfigurationID);

    public SensitiveDataChecker() {
        super();
    }

    public AbstractMap.SimpleEntry<String, Integer> detect(String code) {
        String sensitiveWordsRegex = "\\b(?:Checkmarx|Gartner|Leader)\\b";

        return matchRegexPattern(sensitiveWordsRegex, code, securityConfigurationName);
    }

}
