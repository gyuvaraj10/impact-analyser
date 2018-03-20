package com.impact.analyser;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.impact.analyser.cucumber.RetrieveCucumberStepDefinitions;
import com.impact.analyser.cucumber.models.CucumberReportFormatter;
import com.impact.analyser.cucumber.models.CucumberResultReport;
import com.impact.analyser.interfaces.IPageInformation;
import com.impact.analyser.interfaces.ITestDefInformation;
import com.impact.analyser.interfaces.ITestMapper;
import com.impact.analyser.rules.PageRules;
import com.impact.analyser.rules.TestRules;
import cucumber.api.cli.Main;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class BDDCollector {

    @Inject
    private RetrieveCucumberStepDefinitions cucumberStepDefinitions;

    @Inject
    private PageRules pageRules;

    @Inject
    private TestRules testRules;

    @Inject
    private Logger logger;

    @Inject
    IPageInformation iPageInformation;

    @Inject
    ITestDefInformation iTestDefInformation;

    @Inject
    ITestMapper testMapper;


    public void setPageRules(PageRules pageRules) {
        this.pageRules = pageRules;
    }

    public void setTestRules(TestRules testRules) {
        this.testRules = testRules;
    }

    public void collectTrace(String[] glue, String featureFilePath) throws IOException, NoSuchFieldException, ClassNotFoundException {
        List<CucumberResultReport> scenarios = getScenarios(glue, featureFilePath);
        Iterator<CucumberResultReport> iterator = scenarios.iterator();
        Map<String, Map<String, MethodNode>> scenarioStepDes = new HashMap<>();
        Map<Class<?>, ClassNode> stepDefClassNode = iTestDefInformation.getCucumberClassAndNode(glue);
        while(iterator.hasNext()) {
            CucumberResultReport feature = iterator.next();
            scenarioStepDes.putAll(cucumberStepDefinitions.getCucumberStepAndDefinitionForAScenario(feature, glue));
        }
        Map<Class<?>, Set<MethodNode>> stepDefAndMethodNodes = iTestDefInformation
                .getStepDefClassAndMethod(new ArrayList<>(stepDefClassNode.keySet()));
        Set<Class<?>> pageClasses = iPageInformation.getAllPageTypesInPackages(pageRules);
        logger.log(Level.INFO, "Found {0} number of page classes", pageClasses.size());
        Map<Class<?>, Set<String>> pageAndElements = iPageInformation.getPageElements(pageClasses);
        logger.info("Done retrieving page elements "+ pageAndElements.size());
        Map<Class<?>, Set<String>> pageAndMethods = iPageInformation.getPageMethods(pageClasses);
        logger.info("Done retrieving page methods "+ pageAndMethods.size());
        Map<Class<?>, ClassNode> pageClassNodeMap = iPageInformation.getPageClassNodeMap(pageClasses);
        testMapper.setPageRules(pageRules);
        testMapper.setTestRules(testRules);
        testMapper.setPageClassNodes(pageClassNodeMap);
        testMapper.setPageAndMethods(pageAndMethods);
        testMapper.setPageClasses(pageClasses);
        testMapper.setTestClassMethods(stepDefAndMethodNodes);
        testMapper.setPageAndElements(pageAndElements);
        testMapper.setPageAndMethods(pageAndMethods);
        testMapper.mapCucumber(scenarioStepDes, stepDefClassNode, stepDefAndMethodNodes);
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
}
