package org.checkmarx.codescanner.unit.security;

import org.checkmarx.codescanner.util.security.CrossSiteScriptingChecker;
import org.checkmarx.codescanner.util.security.SQLInjectionChecker;
import org.checkmarx.codescanner.util.security.SecurityChecker;
import org.checkmarx.codescanner.util.security.SensitiveDataChecker;
import org.junit.Assert;
import org.junit.Test;

import java.util.AbstractMap;

public class SecurityCheckerTest {

    SecurityChecker crossSiteScriptingChecker = new CrossSiteScriptingChecker();
    SecurityChecker sensitiveDataChecker = new SensitiveDataChecker();
    SecurityChecker sqlInjectionChecker = new SQLInjectionChecker();

    @Test
    public void testCrossSiteScriptingChecker() {
        String code = "            alert(\"Selecione um item na lista!\"); alert(\"ups\")";

        AbstractMap.SimpleEntry<String, Integer> expectedOutput = new AbstractMap.SimpleEntry<>("Cross site scripting", 2);

        Assert.assertEquals(expectedOutput, crossSiteScriptingChecker.detect(code));
    }

    @Test
    public void testSensitiveDataChecker() {
        String code = "Checkmarx is the enterprise application security leader and the host of Checkmarx One™ — the industry -leading cloud-native AppSec platform that helps enterprises build #DevSecTrust.";

        AbstractMap.SimpleEntry<String, Integer> expectedOutput = new AbstractMap.SimpleEntry<>("Sensitive data exposure", 3);

        Assert.assertEquals(expectedOutput, sensitiveDataChecker.detect(code));
    }

    @Test
    public void testSensitiveDataCheckerNoDetections() {
        String code = "We are honored to serve more than 1,800 customers, which includes 60 percent of all Fortune 100 organizations. We are committed to moving forward with the unwavering dedication to the safety and security of our customers, and the applications that power our day-to-day lives.";

        AbstractMap.SimpleEntry<String, Integer> expectedOutput = new AbstractMap.SimpleEntry<>("Sensitive data exposure", 0);

        Assert.assertEquals(expectedOutput, sensitiveDataChecker.detect(code));
    }

    @Test
    public void testSQLInjectionChecker() {
        String code = "txtSQL = \"SELECT INTO Customers (CustomerName,Address,City) Values(@0,@1,@2) WHERE asd = %s asdsadadaz\";";

        AbstractMap.SimpleEntry<String, Integer> expectedOutput = new AbstractMap.SimpleEntry<>("SQL Injection", 1);

        Assert.assertEquals(expectedOutput, sqlInjectionChecker.detect(code));
    }

    @Test
    public void testMultipleSQLInjectionChecker() {
        String code = "txtSQL = \"SELECT INTO Customers (CustomerName,Address,City) Values(@0,@1,@2) WHERE asd = %s asdsadadaz\"; hello\\" +
                "\"  .... SELECT .... WHERE .... %s .... \"n";

        AbstractMap.SimpleEntry<String, Integer> expectedOutput = new AbstractMap.SimpleEntry<>("SQL Injection", 2);

        Assert.assertEquals(expectedOutput, sqlInjectionChecker.detect(code));
    }

}
