package com.sample.tests;

import com.google.gson.JsonObject;
import com.impact.analyser.*;
import com.impact.analyser.report.ReportGenerator;
import com.impact.analyser.rules.ElementRules;
import com.impact.analyser.rules.PageRules;
import org.junit.Test;
import java.util.*;

/**
 * Created by Yuvaraj on 20/02/2018.
 */
public class AsmTest {


    @Test
    public void testTDDCollector() {
        try {
            PageRules pageRules = new PageRules();
            pageRules.setBasePageClass("com.sample.tests.BaseSeleniumPage");
            ElementRules elementRules = new ElementRules();
            elementRules.setElementsDefinedWithInPageMethodAlso(true);
            elementRules.setElementsDefinedWithInPageClassOnly(true);
            elementRules.setElementClassPackages(Arrays.asList("com.sample"));
            elementRules.setPageClassPackages(Arrays.asList("com.sample.test2", "com.sample.tests"));
            TDDCollector tddCollector = new TDDCollector(pageRules, elementRules);
            List<JsonObject> jsonObjects = tddCollector.collectJsonReport(new String[]{"com.sample"});
            ReportGenerator reportGenerator = new ReportGenerator();
            reportGenerator.generateReport(jsonObjects);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void calculateTheImpact() throws Exception {
        PageRules pageRules = new PageRules();
        pageRules.setBasePageClass("com.sample.tests.BaseSeleniumPage");
        ElementRules elementRules = new ElementRules();
        elementRules.setElementsDefinedWithInPageClassOnly(true);
        elementRules.setElementsDefinedWithInPageMethodAlso(false);
        elementRules.setElementClassPackages(Arrays.asList("com.sample"));
        elementRules.setPageClassPackages(Arrays.asList("com.sample.test2", "com.sample.tests"));
        BDDCollector bddCollector = new BDDCollector(pageRules, elementRules);
        List<JsonObject> cucumberReports = bddCollector.collectJsonReport(new String[]{"com.sample"},
                "/Users/Yuvaraj/dev/impact-analyser/src/test/resources");
        ReportGenerator reportGenerator = new ReportGenerator();
        reportGenerator.generateReport(cucumberReports);
    }

}


