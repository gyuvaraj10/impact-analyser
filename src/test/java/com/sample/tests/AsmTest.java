package com.sample.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.impact.analyser.*;
import com.impact.analyser.report.CucumberTestReport;
import com.impact.analyser.report.MethodInfo;
import com.impact.analyser.report.PageInfo;
import com.impact.analyser.report.TestReport;
import com.impact.analyser.rules.ElementRules;
import com.impact.analyser.rules.PageRules;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

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
    public void testTDDCollector()   {
        try {
            PageRules pageRules = new PageRules();
            pageRules.setBasePageClass("com.sample.tests.BaseSeleniumPage");
            ElementRules elementRules = new ElementRules();
            elementRules.setElementsDefinedWithInPageClassOnly(false);
            elementRules.setElementClassPackages(Arrays.asList("com.sample"));
            elementRules.setPageClassPackages(Arrays.asList("com.sample.test2", "com.sample.tests"));
            TDDCollector tddCollector = new TDDCollector(pageRules, elementRules);
            List<JsonObject> jsonObjects = tddCollector.collectReportForAPackage(new String[]{"com.sample.tests"});
            FileUtils.writeStringToFile(new File("/Users/Yuvaraj/dev/impact-analyser/src/main/resources/app/rule2.json"),
                    new Gson().toJson(jsonObjects), Charset.defaultCharset());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        List<JsonObject> cucumberReports = bddCollector.collectReport(new String[]{"com.sample.tests", "com.sample.test2"},
                "/Users/Yuvaraj/dev/impact-analyser/src/test/resources");
        FileUtils.writeStringToFile(new File("/Users/Yuvaraj/dev/impact-analyser/src/main/resources/app/bdd.json"),
                new Gson().toJson(cucumberReports), Charset.defaultCharset());
    }
}


