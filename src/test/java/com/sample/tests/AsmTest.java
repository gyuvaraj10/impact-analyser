package com.sample.tests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.impact.analyser.*;
import com.impact.analyser.report.ReportGenerator;
import com.impact.analyser.rules.ElementRules;
import com.impact.analyser.rules.PageRules;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

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
        System.out.println(new Gson().toJson(pageEngine.getSeleniumFieldsFromPageMethod(elementRules, null)));

        ElementRules elementRulesOther = new ElementRules();
        elementRulesOther.setElementsDefinedWithInPageClassOnly(false);
        elementRulesOther.setElementClassPackages(Collections.singletonList("com.sample.elements.otherclass"));
        elementRulesOther.setPageClassPackages(Collections.singletonList("com.sample.elements.pages"));
        System.out.println(new Gson().toJson(pageEngine.getSeleniumFieldsFromPageMethod(elementRulesOther, null)));
    }

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
    public void testBDDCollector() throws Exception {
        PageRules pageRules = new PageRules();
        pageRules.setBasePageClass("com.sample.tests.BaseSeleniumPage");
        ElementRules elementRules = new ElementRules();
        elementRules.setElementsDefinedWithInPageClassOnly(true);
        elementRules.setElementClassPackages(Arrays.asList("com.sample"));
        elementRules.setPageClassPackages(Arrays.asList("com.sample.test2", "com.sample.tests"));
        BDDCollector bddCollector = new BDDCollector(pageRules, elementRules);
        List<JsonObject> cucumberReports = bddCollector.collectJsonReport(new String[]{"com.sample"},
                "/Users/Yuvaraj/dev/impact-analyser/src/test/resources");
        FileUtils.writeStringToFile(new File("/Users/Yuvaraj/dev/impact-analyser/src/main/resources/app/bdd.json"),
                new Gson().toJson(cucumberReports), Charset.defaultCharset());
        System.out.println(new Gson().toJson(cucumberReports));
    }


    @Test
    public void calculateTheImpact() throws Exception {
        PageRules pageRules = new PageRules();
        pageRules.setBasePageClass("pages.BasePage");
        ElementRules elementRules = new ElementRules();
        elementRules.setElementsDefinedWithInPageClassOnly(true);
        elementRules.setElementClassPackages(Arrays.asList("pages"));
        elementRules.setPageClassPackages(Arrays.asList("pages"));
        BDDCollector bddCollector = new BDDCollector(pageRules, elementRules);
        List<JsonObject> cucumberReports = bddCollector.collectJsonReport(new String[]{"com.sample"},
                "/Users/Yuvaraj/dev/selenium-guice/src/test/resources/features");
        ReportGenerator reportGenerator = new ReportGenerator();
        reportGenerator.generateReport(cucumberReports);
    }

}


