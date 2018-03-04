package com.impact.analyser;

//import com.impact.analyser.report.PageMethodFieldReport;
import com.impact.analyser.report.MethodInfo;
import com.impact.analyser.report.PageInfo;
import com.impact.analyser.report.TestReport;
import com.impact.analyser.rules.ElementRules;
import com.impact.analyser.rules.PageRules;
import org.objectweb.asm.tree.MethodNode;

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

    public List<TestReport> collectReport(Class<?> testClass)  throws Exception {
        List<TestReport> testReports = new ArrayList<>();
        Set<MethodNode> tests = jUnitTests.getTestNGTests(testClass);
        List<PageInfo> pageInfos = pageEngine.getSeleniumFieldsFromPageMethod(elementRules);
        for(MethodNode testMethod: tests) {
            TestReport testReport = new TestReport();
            testReport.setTestName(testMethod.name);
            Map<String, Set<String>> pagesAndMethodsUsedInTest = retrievePageNames.getPagesAndMethods(testMethod);
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
