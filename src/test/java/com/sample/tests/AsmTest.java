package com.sample.tests;

import com.google.gson.Gson;
import com.impact.analyser.PageEngine;
import com.impact.analyser.RetrieveFieldNamesAndMethods;
import com.impact.analyser.RetrieveJUnitTests;
import com.impact.analyser.RetrievePageNames;
import com.impact.analyser.report.PageMethodReport;
import com.impact.analyser.report.PageReport;
import com.impact.analyser.report.TestReport;
import org.junit.Test;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuvaraj on 20/02/2018.
 */
public class AsmTest {
    RetrieveJUnitTests jUnitTests = new RetrieveJUnitTests();
    RetrievePageNames retrievePageNames = new RetrievePageNames();
    PageEngine pageEngine = new PageEngine();
    RetrieveFieldNamesAndMethods retrieveFieldNamesAndMethods = new RetrieveFieldNamesAndMethods();

    @Test
    public void testGet()  throws Exception {
        TestReport testReport = new TestReport();
        List<MethodNode> tests = jUnitTests.getJUnitTests(SampleTest.class);
        MethodNode testMethod = tests.get(0);
        testReport.setTestName(testMethod.name);
        Map<String, List<String>> pagesAndMethodsUsedInTest = retrievePageNames.getPagesAndMethods(testMethod);
        List<PageReport> pageReportList = new ArrayList<>();
        for(Map.Entry<String, List<String>> entry: pagesAndMethodsUsedInTest.entrySet()) {
            PageReport pageReport = new PageReport();
            pageReport.setPageName(entry.getKey());
            Map<String, List<String>> methodFieldMap = pageEngine.getSeleniumFieldsFromEachMethod(Class.forName(entry.getKey()));
            List<PageMethodReport> pageMethodReportList = new ArrayList<>();
            for(Map.Entry<String, List<String>> entry1: methodFieldMap.entrySet()) {
                //page method report
                PageMethodReport pageMethodReport = new PageMethodReport();
                pageMethodReport.setMethodName(entry1.getKey());
                pageMethodReport.setFields(methodFieldMap.get(entry1.getKey()));
                pageMethodReportList.add(pageMethodReport);
            }
            pageReport.setPageMethodReports(pageMethodReportList);
            pageReportList.add(pageReport);
        }
        testReport.setPages(pageReportList);
        System.out.println(new Gson().toJson(testReport));
    }
}


