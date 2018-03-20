package com.impact.analyser;

import com.google.inject.Inject;
import com.impact.analyser.interfaces.IPageInformation;
import com.impact.analyser.interfaces.ITestDefInformation;
import com.impact.analyser.interfaces.ITestMapper;
import com.impact.analyser.rules.TestRules;
import com.impact.analyser.rules.PageRules;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class TDDCollector {

    @Inject
    private PageRules pageRules;

    @Inject
    private TestRules testRules;

    @Inject
    private Logger logger;

    @Inject
    ITestDefInformation retrieveTestInformation;

    @Inject
    IPageInformation iPageInformation;

    @Inject
    ITestMapper testMapper;


    public void setPageRules(PageRules pageRules) {
        this.pageRules = pageRules;
    }

    public void setTestRules(TestRules testRules) {
        this.testRules = testRules;

    }

    public void collectTrace(String[] testsPackage) throws IOException, NoSuchFieldException, ClassNotFoundException {
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
        testMapper.map(testClassNodeMap, testClassMethodMap);
    }
}
