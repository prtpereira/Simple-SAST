package org.checkmarx.codescanner.util.security;

import java.util.AbstractMap;

public class SQLInjectionChecker extends SecurityChecker {

    private static final int securityConfigurationID = 3;
    private static final String securityConfigurationName = SECURITY_CHECKER_CONFIGURATIONS.get(securityConfigurationID);

    public SQLInjectionChecker() {
        super();
    }

    public AbstractMap.SimpleEntry<String, Integer> detect(String code) {
        String sqlInjectionRegex = "\"[^\"']*\\b(SELECT|WHERE|%s)\\b[^\"']*\"";

        return matchRegexPattern(sqlInjectionRegex, code, securityConfigurationName);
    }

}
