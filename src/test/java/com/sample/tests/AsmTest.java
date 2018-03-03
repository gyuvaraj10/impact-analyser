package com.sample.tests;

import com.google.gson.Gson;
import com.impact.analyser.*;
import com.impact.analyser.report.CucumberTestReport;
import com.impact.analyser.rules.ElementRules;
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
    public void testGet()  throws Exception {
//        TDDCollector tddCollector = new TDDCollector();
//        System.out.println(new Gson().toJson(tddCollector.collectReport(SampleTest.class)));

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


//        BDDCollector bddCollector = new BDDCollector();
//        List<CucumberTestReport> cucumberReports = bddCollector.collectReport(new String[]{"com.sample.tests", "com.sample.test2"},
//                "/Users/Yuvaraj/dev/impactanalyser/src/test/resources");
//        System.out.println(new Gson().toJson(cucumberReports));
    }
}


