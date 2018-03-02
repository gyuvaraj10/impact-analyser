package com.impact.analyser;


import com.impact.analyser.cucumber.RetrieveCucumberStepDefinitions;
import com.impact.analyser.cucumber.models.CucumberReportFormatter;
import com.impact.analyser.cucumber.models.CucumberResultReport;
import com.impact.analyser.report.CucumberStepDef;
import com.impact.analyser.report.CucumberTestReport;
import com.impact.analyser.report.PageMethodReport;
import com.impact.analyser.report.PageReport;
import cucumber.api.cli.Main;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class BDDCollector {

    RetrieveCucumberStepDefinitions cucumberStepDefinitions = new RetrieveCucumberStepDefinitions();
    RetrievePageNames retrievePageNames = new RetrievePageNames();
    PageEngine pageEngine = new PageEngine();



    public List<CucumberResultReport> getStepDefinitions(String[] glues, String featureFilePath) throws IOException {
        String reportJson = "result.json";
        String scenarioName = "You naughty";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String[] cucumberArgs = new String[7+glues.length];
        int start = 0;
        for(String glue: glues) {
            cucumberArgs[start] = "-g";
            int nextIndex = start+1;
            cucumberArgs[nextIndex] = glue;
            start = nextIndex+1;
        }
        cucumberArgs[start++] = "-p";
        cucumberArgs[start++] = "json:"+ reportJson;
        cucumberArgs[start++] = "-m";
        cucumberArgs[start++] = "-d";
        cucumberArgs[start++] = featureFilePath;

        Main.run(cucumberArgs, loader);
        String content = FileUtils.readFileToString(new File(reportJson), Charset.defaultCharset());
        List<CucumberResultReport> resultReport = CucumberReportFormatter.parse(content);
        System.out.println(resultReport.toString());
        return resultReport;
    }

    public List<CucumberTestReport> collectReport(String[] glue, String featureFilePath) throws Exception {
        List<CucumberResultReport> scenarios = getStepDefinitions(glue, featureFilePath);
        List<CucumberTestReport> testReports = new ArrayList<>();
        Map<String, Map<String, MethodNode>> scenarioMap = new HashMap<>();
        Iterator<CucumberResultReport> iterator = scenarios.iterator();
        while(iterator.hasNext()) {
            CucumberResultReport scenario = iterator.next();
            scenarioMap.put(scenario.getName(), cucumberStepDefinitions.getCucumberStepDefinitions(scenario, glue));
        }
        for(Map.Entry<String, Map<String, MethodNode>> scenario: scenarioMap.entrySet()) {
            CucumberTestReport cucumberReport = new CucumberTestReport();
            cucumberReport.setScenarioName(scenario.getKey());
            List<CucumberStepDef> stepDefs= new ArrayList<>();
            for(Map.Entry<String, MethodNode> entry: scenario.getValue().entrySet()) {
                CucumberStepDef stepDef= new CucumberStepDef();
                String stepName = entry.getKey();
                stepDef.setName(stepName);
                MethodNode stepDefinition = entry.getValue();
                Map<String, List<String>> pagesAndMethodsUsedInTest = retrievePageNames.getPagesAndMethods(stepDefinition);
                List<PageReport> pageReportList = new ArrayList<>();
                for (Map.Entry<String, List<String>> methodEntry : pagesAndMethodsUsedInTest.entrySet()) {
                    PageReport pageReport = new PageReport();
                    pageReport.setPageName(methodEntry.getKey());
                    Map<String, List<String>> methodFieldMap = pageEngine.getSeleniumFieldsFromEachMethod(Class.forName(methodEntry.getKey()));
                    List<PageMethodReport> pageMethodReportList = new ArrayList<>();
                    for (Map.Entry<String, List<String>> methodFieldEntry : methodFieldMap.entrySet()) {
                        //page method report
                        PageMethodReport pageMethodReport = new PageMethodReport();
                        pageMethodReport.setMethodName(methodFieldEntry.getKey());
                        pageMethodReport.setFields(methodFieldMap.get(methodFieldEntry.getKey()));
                        pageMethodReportList.add(pageMethodReport);
                    }
                    pageReport.setPageMethodReports(pageMethodReportList);
                    pageReportList.add(pageReport);
                }
                stepDef.setPages(pageReportList);
                stepDefs.add(stepDef);
            }
            cucumberReport.setStepDefs(stepDefs);
            testReports.add(cucumberReport);
        }
        return testReports;
    }
}
