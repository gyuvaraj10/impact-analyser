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
    private final RetrievePageNames retrievePageNames = new RetrievePageNames();
    private final PageEngine pageEngine = new PageEngine();
    private PageRules pageRules;
    private ElementRules elementRules;
    private TestRules testRules;
    List<PageInfo> pageInfos;
    private static final Logger logger = Logger.getLogger(TDDCollector.class.getName());

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
        logger.log(Level.INFO, "Retrieved test class and test method mapping for {0} test classes", testClassMethodMap.size());
        Set<Class<?>> pageClasses = iPageInformation.getAllPageTypesInPackages(pageRules);
        logger.log(Level.INFO, "Found {0} number of page classes", pageClasses.size());
        Map<Class<?>, Set<String>> pageAndElements = iPageInformation.getPageElements(pageClasses);
        logger.info("Done retrieving page elements "+ pageAndElements.size());
        Map<Class<?>, Set<String>> pageAndMethods = iPageInformation.getPageMethods(pageClasses);
        logger.info("Done retrieving page methods "+ pageAndMethods.size());
        Map<String, List<TestReport>> testReportMap = testMapper.map(testClasses, testClassMethodMap, pageClasses, pageAndElements, pageAndMethods);

//        pageInfos = pageEngine.getSeleniumFieldsFromPageMethod(elementRules, pageRules);
        return null;
        // not changed yet
        //return getJsonObjectsForHtmlReport(collectReport(testspackage));
    }




    private Map<String, List<TestReport>> collectReport(String[] testspackage) throws IOException, NoSuchFieldException, ClassNotFoundException {
        Map<String, List<TestReport>> testReportmap= new HashMap<>();
        for(String testPackage: testspackage) {
            for(Class<?> testClass: ClassUtils.getAllTestTypesInPackages(Collections.singletonList(testPackage))) {
                if(isTestClass(testClass)) {
                    testReportmap.put(testClass.getName(), collectReport(testClass));
                }
            }
        }
        return testReportmap;
    }

    private List<JsonObject> getJsonObjectsForHtmlReport(Map<String, List<TestReport>> reports) {
        List<JsonObject> jsonObjects = new ArrayList<>();
        for(Map.Entry<String, List<TestReport>> entry: reports.entrySet()) {
            String testClassName = entry.getKey();
            for(TestReport testReport: entry.getValue()){
                String testMethodName = testReport.getTestName();
                for(PageInfo pageInfo: testReport.getPages()) {
                    String pageName = pageInfo.getPageName();
                    for(MethodInfo methodInfo: pageInfo.getMethodReportList()) {
                        String pageMethodName = methodInfo.getMethodName();
                        for(Map.Entry<String, String> fieldClassEntry: methodInfo.getFieldAndFieldClassName().entrySet()){
                            JsonObject jsonObject = new JsonObject();
                            String fieldName = fieldClassEntry.getKey();
                            String fieldClass = fieldClassEntry.getValue();
                            jsonObject.addProperty("testClass", testClassName);
                            jsonObject.addProperty("testMethod", testMethodName);
                            jsonObject.addProperty("pageName", pageName);
                            jsonObject.addProperty("pageMethod", pageMethodName);
                            jsonObject.addProperty("fieldName", fieldName);
                            jsonObject.addProperty("fieldClass", fieldClass);
                            List<String> privateMethods = methodInfo.getPrivateMethods();
                            if(privateMethods!= null && !privateMethods.isEmpty()) {
                                String pMethods = new Gson().toJson(privateMethods);
                                pMethods = pMethods.replace("[\"", "").replace("\"]", "").replace("\",\"","\n");
                                jsonObject.addProperty("pagePrivateMethods", pMethods);
                            }
                            jsonObjects.add(jsonObject);
                        }
                    }
                }
            }
        }
        return jsonObjects;
    }

    private boolean isTestClass(Class<?> testClass) {
        Class<? extends Annotation> jUnitAnno = ClassUtils.getAnnotationClass("org.junit.Test");
        Class<? extends Annotation> testNGAnnot = ClassUtils.getAnnotationClass("org.testng.annotations.Test");
        Annotation jUnitANno = null, testNGAnno = null;
        boolean jUnitTestMethodExists = false;
        boolean testNGTestMethodExists = false;
        if(jUnitAnno != null) {
            jUnitANno = testClass.getDeclaredAnnotation(jUnitAnno);
            jUnitTestMethodExists = Arrays.stream(testClass.getDeclaredMethods())
                    .anyMatch(x->(x.getDeclaredAnnotation(jUnitAnno)!=null));
        }
        if(testNGAnnot != null) {
            testNGAnno = testClass.getDeclaredAnnotation(testNGAnnot);
            testNGTestMethodExists = Arrays.stream(testClass.getDeclaredMethods())
                    .anyMatch(x->(x.getDeclaredAnnotation(testNGAnnot)!=null));
        }

        if(jUnitANno != null || testNGAnno != null ||jUnitTestMethodExists|| testNGTestMethodExists) {
            return true;
        }
        return false;
    }

    private List<TestReport> collectReport(Class<?> testClass) throws IOException, NoSuchFieldException, ClassNotFoundException {
        List<TestReport> testReports = new ArrayList<>();
        logger.log(Level.INFO, "Collecting the tests from test class:"+ testClass.getName());
        Set<MethodNode> tests = jUnitTests.getTestNGTests(testClass);
        logger.log(Level.INFO, "Collected "+tests.size()+ " from test class:"+ testClass.getName());
        for(MethodNode testMethod: tests) {
            logger.log(Level.INFO, "collecting the test report from test: "+ testMethod.name);
            TestReport testReport = new TestReport();
            testReport.setTestName(testMethod.name);
            Map<String, Set<String>> pagesAndMethodsUsedInTest = retrievePageNames.getPagesAndMethods(pageRules, testMethod, testClass);
            logger.log(Level.INFO, "pages used in this test are retrieved and size is: "+ pagesAndMethodsUsedInTest.size());
            List<PageInfo> pageReportList = new ArrayList<>();
            for (Map.Entry<String, Set<String>> entry : pagesAndMethodsUsedInTest.entrySet()) {
                logger.log(Level.INFO, "scanning page method: "+ entry.getValue()+ " for test "+ testMethod.name+ " from test class: "+ testClass.getName());
                Optional<PageInfo> optional = pageInfos.stream().filter(x->x.getPageName().equals(entry.getKey()) &&
                        !x.getPageName().equals(testClass.getName())).findFirst();
                PageInfo pageReport = null;
                if(optional.isPresent()) {
                    logger.log(Level.INFO, "page report found");
                    pageReport = optional.get();
                    Set<MethodInfo> methodInfoSet = pageReport.getMethodReportList();
                    Set<MethodInfo> methodReport = new HashSet<>();
                    for(String method: entry.getValue()) {
                        Optional<MethodInfo> methodInfoOptional = methodInfoSet.stream()
                                .filter(x->x.getMethodName().equals(method))
                                .findFirst();
                        if(methodInfoOptional.isPresent()) {
                            logger.log(Level.INFO, "method info found");
                            methodReport.add(methodInfoOptional.get());
                        }
                    }
                    pageReport.setMethodReportList(methodReport);
                    pageReportList.add(pageReport);
                }
            }
            testReport.setPages(pageReportList);
            testReports.add(testReport);
        }
        return testReports;
    }
}
