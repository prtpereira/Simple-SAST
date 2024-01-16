package org.checkmarx.codescanner.util;

import java.util.AbstractMap;
import java.util.Map;

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

    public abstract AbstractMap.SimpleEntry<String, Integer> run(String code);

}
