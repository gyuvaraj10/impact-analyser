package com.sample.tests;

import com.google.gson.JsonObject;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.impact.analyser.*;
import com.impact.analyser.configure.ImpactAnalyserConfiguration;
import com.impact.analyser.report.ReportGenerator;
import com.impact.analyser.rules.TestRules;
import com.impact.analyser.rules.ElementRules;
import com.impact.analyser.rules.PageRules;
import org.junit.Test;
import java.util.*;

/**
 * Created by Yuvaraj on 20/02/2018.
 */
public class AsmTest {


    @Test
    public void testClasses() throws Exception {
        Injector injector = Guice.createInjector(new ImpactAnalyserConfiguration());
        TDDCollector tddCollector = injector.getInstance(TDDCollector.class);
        tddCollector.collectJsonReport(new String[]{"com.sample"});
    }

    @Test
    public void testTDDCollector() {
        try {

            PageRules pageRules = new PageRules();
            pageRules.setBasePageClass("com.sample.tests.BaseSeleniumPage");
            pageRules.setPageClassPackages(Arrays.asList("com.sample"));
            ElementRules elementRules = new ElementRules();
            elementRules.setElementsDefinedWithInPageMethodAlso(true);
            elementRules.setElementsDefinedWithInPageClassOnly(false);
            elementRules.setElementClassPackages(Arrays.asList("com.sample"));
            elementRules.setPageClassPackages(Arrays.asList("com.sample.test2", "com.sample.tests"));
            Injector injector = Collector.getInjector();
            TDDCollector tddCollector = injector.getInstance(TDDCollector.class);
            tddCollector.setElementRules(elementRules);
            tddCollector.setPageRules(pageRules);
            TestRules testRules = new TestRules();
            testRules.setBaseTestClass("com.sample.tests.BaseSeleniumTest");
            testRules.setTestClassPackages(Arrays.asList("com.sample.tests"));
            tddCollector.setTestRules(testRules);
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


