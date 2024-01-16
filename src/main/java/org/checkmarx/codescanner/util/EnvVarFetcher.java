package org.checkmarx.codescanner.util;

public class EnvVarFetcher {

    public static String getEnvStrElseDefault(String variable, String defaultVar) {
        String envVar = getEnvStr(variable);

        if (envVar == null) return defaultVar;
        else return envVar;
    }

    public static String getEnvStr(String variable) {
        return System.getenv(variable);
    }

}
