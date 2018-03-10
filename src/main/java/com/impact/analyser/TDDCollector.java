package com.impact.analyser;

//import com.impact.analyser.report.PageMethodFieldReport;
import com.impact.analyser.report.MethodInfo;
import com.impact.analyser.report.PageInfo;
import com.impact.analyser.report.TestReport;
import com.impact.analyser.rules.ElementRules;
import com.impact.analyser.rules.PageRules;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class TDDCollector {

    private final RetrieveJUnitTests jUnitTests;
    private final RetrievePageNames retrievePageNames = new RetrievePageNames();
    private final PageEngine pageEngine = new PageEngine();
    private final PageRules pageRules;
    private final ElementRules elementRules;

    public TDDCollector(PageRules pageRules, ElementRules elementRules) {
        this.pageRules = pageRules;
        this.elementRules = elementRules;
        jUnitTests = new RetrieveJUnitTests(pageRules);
    }


    public Map<String, List<TestReport>> collectReportForAPackage(String[] testspackage) throws IOException, NoSuchFieldException {
        Map<String, List<TestReport>> testReportmap= new HashMap<>();
        for(String testPackage: testspackage) {
            for(Class<?> testClass: ClassUtils.getAllTypesInPackages(Collections.singletonList(testPackage))) {
                if(isTestClass(testClass)) {
                    testReportmap.put(testClass.getName(), collectReport(testClass));
                }
            }
        }
        return testReportmap;
    }

    private boolean isTestClass(Class<?> testClass) {
        Annotation jUnitANno = testClass.getDeclaredAnnotation(org.junit.Test.class);
        Annotation testNGAnno = testClass.getDeclaredAnnotation(org.testng.annotations.Test.class);
        boolean testMethodExists = Arrays.stream(testClass.getDeclaredMethods())
                .anyMatch(x->(x.getDeclaredAnnotation(org.junit.Test.class)!=null)||
                        (x.getDeclaredAnnotation(org.testng.annotations.Test.class)!=null));
        if(jUnitANno != null || testNGAnno != null ||testMethodExists) {
            return true;
        }
        return false;
    }
    private List<TestReport> collectReport(Class<?> testClass) throws IOException, NoSuchFieldException {
        List<TestReport> testReports = new ArrayList<>();
        Set<MethodNode> tests = jUnitTests.getTestNGTests(testClass);
        List<PageInfo> pageInfos = pageEngine.getSeleniumFieldsFromPageMethod(elementRules);
        for(MethodNode testMethod: tests) {
            TestReport testReport = new TestReport();
            testReport.setTestName(testMethod.name);
            Map<String, Set<String>> pagesAndMethodsUsedInTest = retrievePageNames.getPagesAndMethods(pageRules, testMethod);
            List<PageInfo> pageReportList = new ArrayList<>();
            for (Map.Entry<String, Set<String>> entry : pagesAndMethodsUsedInTest.entrySet()) {
                Optional<PageInfo> optional = pageInfos.stream().filter(x->x.getPageName().equals(entry.getKey())).findFirst();
                PageInfo pageReport = null;
                if(optional.isPresent()) {
                    pageReport = optional.get();
                    Set<MethodInfo> methodInfoSet = pageReport.getMethodReportList();
                    Set<MethodInfo> methodReport = new HashSet<>();
                    for(String method: entry.getValue()) {
                        Optional<MethodInfo> methodInfoOptional = methodInfoSet.stream()
                                .filter(x->x.getMethodName().equals(method))
                                .findFirst();
                        if(methodInfoOptional.isPresent()) {
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
