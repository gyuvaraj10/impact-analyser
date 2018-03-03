//package com.impact.analyser;
//
//
//import com.impact.analyser.cucumber.RetrieveCucumberStepDefinitions;
//import com.impact.analyser.cucumber.models.CucumberReportFormatter;
//import com.impact.analyser.cucumber.models.CucumberResultReport;
//import com.impact.analyser.report.CucumberStepDef;
//import com.impact.analyser.report.CucumberTestReport;
//import com.impact.analyser.report.PageMethodFieldReport;
//import com.impact.analyser.report.PageReport;
//import com.impact.analyser.rules.ElementRules;
//import com.impact.analyser.rules.PageRules;
//import cucumber.api.cli.Main;
//
//import org.apache.commons.io.FileUtils;
//import org.objectweb.asm.tree.MethodNode;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.Charset;
//import java.util.*;
//
///**
// * Created by Yuvaraj on 27/02/2018.
// */
//public class BDDCollector {
//
//    private final RetrieveCucumberStepDefinitions cucumberStepDefinitions = new RetrieveCucumberStepDefinitions();
//    private final RetrievePageNames retrievePageNames = new RetrievePageNames();
//    private final PageEngine pageEngine = new PageEngine();
//    private final PageRules pageRules;
//    private final ElementRules elementRules;
//
//    public BDDCollector(PageRules pageRules, ElementRules elementRules) {
//        this.pageRules = pageRules;
//        this.elementRules = elementRules;
//    }
//
//    public List<CucumberTestReport> collectReport(String[] glue, String featureFilePath) throws Exception {
//        List<CucumberResultReport> scenarios = getScenarios(glue, featureFilePath);
//        List<CucumberTestReport> testReports = new ArrayList<>();
//        Map<String, Map<String, MethodNode>> scenarioStepDefDetailMap = new HashMap<>();
//        Iterator<CucumberResultReport> iterator = scenarios.iterator();
//        while(iterator.hasNext()) {
//            CucumberResultReport scenario = iterator.next();
//            scenarioStepDefDetailMap.put(scenario.getName(),
//                    cucumberStepDefinitions.getCucumberStepAndDefinitionForAScenario(scenario, glue));
//        }
//        for(Map.Entry<String, Map<String, MethodNode>> scenario: scenarioStepDefDetailMap.entrySet()) {
//            CucumberTestReport cucumberReport = new CucumberTestReport();
//            cucumberReport.setScenarioName(scenario.getKey());
//            List<CucumberStepDef> stepDefs= new ArrayList<>();
//            for(Map.Entry<String, MethodNode> entry: scenario.getValue().entrySet()) {
//                CucumberStepDef stepDef= new CucumberStepDef();
//                String stepName = entry.getKey();
//                stepDef.setName(stepName);
//                MethodNode stepDefinition = entry.getValue();
//                Map<String, List<String>> pageAndPageMethodsMap = retrievePageNames.getPagesAndMethods(pageRules, stepDefinition);
//                List<PageReport> pageReportList = new ArrayList<>();
//                for (Map.Entry<String, List<String>> methodEntry : pageAndPageMethodsMap.entrySet()) {
//                    PageReport pageReport = new PageReport();
//                    pageReport.setPageName(methodEntry.getKey());
//                    Map<String, Map<String, String>> methodFieldMap = pageEngine.
//                            getSeleniumFieldsFromPageMethod(elementRules, Class.forName(methodEntry.getKey()));
//                    List<PageMethodFieldReport> pageMethodReportList = new ArrayList<>();
//                    for (Map.Entry<String, Map<String, String>> methodFieldEntry : methodFieldMap.entrySet()) {
//                        //page method report
//                        PageMethodFieldReport pageMethodReport = new PageMethodFieldReport();
//                        pageMethodReport.setMethodName(methodFieldEntry.getKey());
//                        pageMethodReport.setFields(methodFieldMap.get(methodFieldEntry.getKey()));
//                        pageMethodReportList.add(pageMethodReport);
//                    }
//                    pageReport.setPageMethodReports(pageMethodReportList);
//                    pageReportList.add(pageReport);
//                }
//                stepDef.setPages(pageReportList);
//                stepDefs.add(stepDef);
//            }
//            cucumberReport.setStepDefs(stepDefs);
//            testReports.add(cucumberReport);
//        }
//        return testReports;
//    }
//
//    /**
//     * gets the list of step definitions for the list of scenarios by parsing the cucumber report json file
//     * @param glues
//     * @param featureFilePath
//     * @return
//     * @throws IOException
//     */
//    private List<CucumberResultReport> getScenarios(String[] glues, String featureFilePath) throws IOException {
//        String reportJson = "result.json";
//        ClassLoader loader = Thread.currentThread().getContextClassLoader();
//        String[] cucumberArgs = new String[7+glues.length];
//        int start = 0;
//        for(String glue: glues) {
//            cucumberArgs[start] = "-g";
//            int nextIndex = start+1;
//            cucumberArgs[nextIndex] = glue;
//            start = nextIndex+1;
//        }
//        cucumberArgs[start++] = "-p";
//        cucumberArgs[start++] = "json:"+ reportJson;
//        cucumberArgs[start++] = "-m";
//        cucumberArgs[start++] = "-d";
//        cucumberArgs[start] = featureFilePath;
//
//        Main.run(cucumberArgs, loader);
//        String content = FileUtils.readFileToString(new File(reportJson), Charset.defaultCharset());
//        return CucumberReportFormatter.parse(content);
//    }
//}
