package com.impact.analyser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.impact.analyser.interfaces.IPageInformation;
import com.impact.analyser.interfaces.ITestDefInformation;
import com.impact.analyser.interfaces.ITestMapper;
import com.impact.analyser.report.MethodInfo;
import com.impact.analyser.report.PageInfo;
import com.impact.analyser.report.TestReport;
import com.impact.analyser.rules.TestRules;
import com.impact.analyser.rules.ElementRules;
import com.impact.analyser.rules.PageRules;
import com.impact.analyser.utils.ClassUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class TDDCollector {

    private RetrieveJUnitTests jUnitTests;

    @Inject
    private RetrievePageNames retrievePageNames;

    @Inject
    private PageRules pageRules;
    @Inject
    private ElementRules elementRules;
    @Inject
    private TestRules testRules;

    @Inject
    private Logger logger;

    List<PageInfo> pageInfos;

    @Inject
    ITestDefInformation retrieveTestInformation;

    @Inject
    IPageInformation iPageInformation;

    @Inject
    ITestMapper testMapper;


    public void setPageRules(PageRules pageRules) {
        this.pageRules = pageRules;
        jUnitTests = new RetrieveJUnitTests(pageRules);
    }

    public void setTestRules(TestRules testRules) {
        this.testRules = testRules;

    }

    public void setElementRules(ElementRules elementRules) {
        this.elementRules = elementRules;
    }


    public  List<JsonObject> collectJsonReport(String[] testsPackage) throws IOException, NoSuchFieldException, ClassNotFoundException {
        List<Class<?>> testClasses = retrieveTestInformation.getTestClasses(testsPackage);
        logger.log(Level.INFO, "Found {0} number of test classes", testClasses.size());
        Map<Class<?>, Set<MethodNode>> testClassMethodMap = retrieveTestInformation.getTestClassAndTestMethod(testClasses);
        Map<Class<?>, ClassNode> testClassNodeMap = retrieveTestInformation.getTestClassAndNode(testsPackage);
        logger.log(Level.INFO, "Retrieved test class and test method mapping for {0} test classes", testClassMethodMap.size());
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
        testMapper.setTestClassMethods(testClassMethodMap);
        testMapper.setPageAndElements(pageAndElements);
        testMapper.setPageAndMethods(pageAndMethods);
        Map<String, List<TestReport>> testReportMap = testMapper.map(testClasses, testClassNodeMap, testClassMethodMap);
        logger.info(String.valueOf(testReportMap.size()));
//        pageInfos = pageEngine.getSeleniumFieldsFromPageMethod(elementRules, pageRules);
        return null;
        // not changed yet
        //return getJsonObjectsForHtmlReport(collectReport(testspackage));
    }
}
