package com.sample.tests;

import com.google.gson.Gson;
import com.impact.analyser.*;
import com.impact.analyser.cucumber.models.CucumberReport;
import com.impact.analyser.report.CucumberTestReport;
import com.impact.analyser.report.PageMethodReport;
import com.impact.analyser.report.PageReport;
import com.impact.analyser.report.TestReport;
import cucumber.api.StepDefinitionReporter;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import gherkin.Parser;
import gherkin.ast.Feature;
import gherkin.ast.ScenarioDefinition;
import org.junit.Test;
import org.objectweb.asm.tree.MethodNode;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuvaraj on 20/02/2018.
 */
public class AsmTest {

    @Test
    public void testGet()  throws Exception {
        TDDCollector tddCollector = new TDDCollector();
        System.out.println(new Gson().toJson(tddCollector.collectReport(SampleTest.class)));
        BDDCollector bddCollector = new BDDCollector();
        List<CucumberTestReport> cucumberReports = bddCollector.collectReport(new String[]{"com.sample.tests", "com.sample.test2"},
                "/Users/Yuvaraj/dev/impactanalyser/src/test/resources/sample.feature");
        System.out.println(new Gson().toJson(cucumberReports));
    }
}


