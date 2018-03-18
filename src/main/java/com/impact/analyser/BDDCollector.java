package com.impact.analyser;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.impact.analyser.cucumber.RetrieveCucumberStepDefinitions;
import com.impact.analyser.cucumber.models.CucumberReportFormatter;
import com.impact.analyser.cucumber.models.CucumberResultReport;
import com.impact.analyser.report.*;
import com.impact.analyser.rules.ElementRules;
import com.impact.analyser.rules.PageRules;
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

    private final RetrieveCucumberStepDefinitions cucumberStepDefinitions = new RetrieveCucumberStepDefinitions();
    private final RetrievePageNames retrievePageNames = new RetrievePageNames();
    private final PageEngine pageEngine = new PageEngine();
    private final PageRules pageRules;
    private final ElementRules elementRules;

    public BDDCollector(PageRules pageRules, ElementRules elementRules) {
        this.pageRules = pageRules;
        this.elementRules = elementRules;
    }

    public List<JsonObject> collectJsonReport(String[] glue, String featureFilePath) throws Exception {
        List<CucumberResultReport> scenarios = getScenarios(glue, featureFilePath);
        List<CucumberTestReport> testReports = new ArrayList<>();
        Map<String, Map<String, MethodNode>> scenarioStepDefDetailMap = new HashMap<>();
        Iterator<CucumberResultReport> iterator = scenarios.iterator();
        List<PageInfo> pageInfos = pageEngine.getSeleniumFieldsFromPageMethod(elementRules, pageRules);
        while(iterator.hasNext()) {
            CucumberResultReport feature = iterator.next();
            scenarioStepDefDetailMap = cucumberStepDefinitions.getCucumberStepAndDefinitionForAScenario(feature, glue);
            for (Map.Entry<String, Map<String, MethodNode>> scenario : scenarioStepDefDetailMap.entrySet()) {
                CucumberTestReport cucumberReport = new CucumberTestReport();
                cucumberReport.setFeatureFileName(feature.getName());
                cucumberReport.setScenarioName(scenario.getKey());
                List<CucumberStepDef> stepDefs = new ArrayList<>();
                for (Map.Entry<String, MethodNode> entry : scenario.getValue().entrySet()) {
                    CucumberStepDef stepDef = new CucumberStepDef();
                    String stepName = entry.getKey();
                    stepDef.setName(stepName);
                    MethodNode stepDefinition = entry.getValue();
                    Map<String, Set<String>> pageAndPageMethodsMap = retrievePageNames.getPagesAndMethods(pageRules, stepDefinition);
                    List<PageInfo> pageReportList = new ArrayList<>();
                    for (Map.Entry<String, Set<String>> methodEntry : pageAndPageMethodsMap.entrySet()) {
                        Optional<PageInfo> optional = pageInfos.stream().filter(x -> x.getPageName().equals(methodEntry.getKey())).findFirst();
                        PageInfo pageReport = new PageInfo();
                        if (optional.isPresent()) {
                            PageInfo pageReport1 = optional.get();
                            pageReport.setPageName(pageReport1.getPageName());
                            Set<MethodInfo> methodInfoSet = pageReport1.getMethodReportList();
                            Set<MethodInfo> methodReport = new HashSet<>();
                            for (String method : methodEntry.getValue()) {
                                Optional<MethodInfo> methodInfoOptional = methodInfoSet.stream()
                                        .filter(x -> x.getMethodName().equals(method))
                                        .findFirst();
                                if (methodInfoOptional.isPresent()) {
                                    methodReport.add(methodInfoOptional.get());
                                }
                            }
                            pageReport.setMethodReportList(methodReport);
                            pageReportList.add(pageReport);
                        }
                    }
                    stepDef.setPages(pageReportList);
                    stepDefs.add(stepDef);
                }
                cucumberReport.setStepDefs(stepDefs);
                testReports.add(cucumberReport);
            }
        }
        return getJsonObjectsForHtmlReport(testReports);
    }

    /**
     * gets the list of step definitions for the list of scenarios by parsing the cucumber report json file
     * @param glues
     * @param featureFilePath
     * @return
     * @throws IOException
     */
    private List<CucumberResultReport> getScenarios(String[] glues, String featureFilePath) throws IOException {
        String reportJson = "result.json";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String[] cucumberArgs = new String[7+glues.length];
        List<String> args = new ArrayList<>();
        int start = 0;
        for(String glue: glues) {
            cucumberArgs[start] = "-g";
            args.add("-g");
            int nextIndex = start+1;
            cucumberArgs[nextIndex] = glue;
            args.add(glue);
            start = nextIndex+1;
        }
        cucumberArgs[start++] = "-p";
        cucumberArgs[start++] = "json:"+ reportJson;
        cucumberArgs[start++] = "-m";
        cucumberArgs[start++] = "-d";
        cucumberArgs[start] = featureFilePath;
        args.add("-p");
        args.add("json:"+ reportJson);
        args.add("-m");
        args.add("-d");
        args.add(featureFilePath);
        Main.run(args.toArray(new String[]{}), loader);
        String content = FileUtils.readFileToString(new File(reportJson), Charset.defaultCharset());
        return CucumberReportFormatter.parse(content);
    }

    private List<JsonObject> getJsonObjectsForHtmlReport(List<CucumberTestReport> testReports) {
        List<JsonObject> jsonObjects = new ArrayList<>();
        for(CucumberTestReport cucumberTestReport: testReports) {
            String featureFileName = cucumberTestReport.getFeatureFileName();
            String scenarioName = cucumberTestReport.getScenarioName();
            for(CucumberStepDef stepDef: cucumberTestReport.getStepDefs()){
                String testMethodName = stepDef.getName();
                for(PageInfo pageInfo: stepDef.getPages()) {
                    String pageName = pageInfo.getPageName();
                    for(MethodInfo methodInfo: pageInfo.getMethodReportList()) {
                        String pageMethodName = methodInfo.getMethodName();
                        for(Map.Entry<String, String> fieldClassEntry: methodInfo.getFieldAndFieldClassName().entrySet()){
                            JsonObject jsonObject = new JsonObject();
                            String fieldName = fieldClassEntry.getKey();
                            String fieldClass = fieldClassEntry.getValue();
                            jsonObject.addProperty("testClass", featureFileName);
                            jsonObject.addProperty("testMethod", scenarioName);
                            jsonObject.addProperty("pageName", pageName);
                            jsonObject.addProperty("pageMethod", pageMethodName);
                            jsonObject.addProperty("fieldName", fieldName);
                            jsonObject.addProperty("fieldClass", fieldClass);
//                            List<String> privateMethods = methodInfo.getPrivateMethods();
//                            if(privateMethods!= null && !privateMethods.isEmpty()) {
//                                String pMethods = new Gson().toJson(privateMethods);
//                                pMethods = pMethods.replace("[\"", "").replace("\"]", "").replace("\",\"","\n");
//                                jsonObject.addProperty("pagePrivateMethods", pMethods);
//                            }
                            jsonObjects.add(jsonObject);
                        }
                    }
                }
            }
        }
        return jsonObjects;
    }
}
