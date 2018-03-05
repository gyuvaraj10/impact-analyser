package com.sample.tests;

import com.google.gson.Gson;
import com.impact.analyser.*;
import com.impact.analyser.report.CucumberTestReport;
import com.impact.analyser.rules.ElementRules;
import com.impact.analyser.rules.PageRules;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yuvaraj on 20/02/2018.
 */
public class AsmTest {


    @Test
    public void testPageEngine() throws Exception {
        PageEngine pageEngine = new PageEngine();
        ElementRules elementRules = new ElementRules();
        elementRules.setElementsDefinedWithInPageClassOnly(true);
        elementRules.setPageClassPackages(Arrays.asList("com.sample.test2", "com.sample.tests"));
        System.out.println(new Gson().toJson(pageEngine.getSeleniumFieldsFromPageMethod(elementRules)));

        ElementRules elementRulesOther = new ElementRules();
        elementRulesOther.setElementsDefinedWithInPageClassOnly(false);
        elementRulesOther.setElementClassPackages(Collections.singletonList("com.sample.elements.otherclass"));
        elementRulesOther.setPageClassPackages(Collections.singletonList("com.sample.elements.pages"));
        System.out.println(new Gson().toJson(pageEngine.getSeleniumFieldsFromPageMethod(elementRulesOther)));
    }

    @Test
    public void testTDDCollector()  throws Exception {
        PageRules pageRules = new PageRules();
        pageRules.setBasePageClass("com.sample.tests.BaseSeleniumPage");
        ElementRules elementRules = new ElementRules();
        elementRules.setElementsDefinedWithInPageClassOnly(false);
        elementRules.setElementClassPackages(Arrays.asList("com.sample"));
        elementRules.setPageClassPackages(Arrays.asList("com.sample.test2", "com.sample.tests"));
        TDDCollector tddCollector = new TDDCollector(pageRules, elementRules);
        System.out.println(new Gson().toJson(tddCollector
                .collectReportForAPackage(new String[]{"com.sample.tests"})));
    }

    @Test
    public void testBDDCollector() throws Exception {
        PageRules pageRules = new PageRules();
        pageRules.setBasePageClass("com.sample.tests.BaseSeleniumPage");
        ElementRules elementRules = new ElementRules();
        elementRules.setElementsDefinedWithInPageClassOnly(false);
        elementRules.setElementClassPackages(Arrays.asList("com.sample"));
        elementRules.setPageClassPackages(Arrays.asList("com.sample.test2", "com.sample.tests"));
        BDDCollector bddCollector = new BDDCollector(pageRules, elementRules);
        List<CucumberTestReport> cucumberReports = bddCollector.collectReport(new String[]{"com.sample.tests", "com.sample.test2"},
                "/Users/Yuvaraj/dev/impactanalyser/src/test/resources");
        System.out.println(new Gson().toJson(cucumberReports));
    }
}


