package com.sample.tests;

import com.google.gson.Gson;
import com.impact.analyser.*;
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
        Parser parser = new Parser();
        String path = this.getClass().getResource("/sample.feature").getPath();
        InputStreamReader in = new InputStreamReader(new FileInputStream(this.getClass().getResource("/sample.feature").getPath()),
                "UTF-8");
        Feature feature = (Feature) parser.parse(in);
        List<ScenarioDefinition> scenarioDefinitions = feature.getScenarioDefinitions();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        MultiLoader resourceLoader = new MultiLoader(classLoader);
        ResourceLoaderClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        String[] argv = new String[] {"-g", "com.sample.tests", path};
        RuntimeOptions runtimeOptions = new RuntimeOptions(Arrays.asList(argv));
        Runtime runtime = new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
        List features = runtimeOptions.cucumberFeatures(resourceLoader);
        StepDefinitionReporter stepDefinitionReporter = runtimeOptions.stepDefinitionReporter(classLoader);
        runtime.getGlue().reportStepDefinitions(stepDefinitionReporter);
        Runtime runtime1 = runtime;

        System.out.println(new Gson().toJson(scenarioDefinitions));
    }
}


