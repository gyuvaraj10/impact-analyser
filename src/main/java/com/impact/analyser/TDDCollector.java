//package com.impact.analyser;
//
////import com.impact.analyser.report.PageMethodFieldReport;
//import com.impact.analyser.report.PageReport;
//import com.impact.analyser.report.TestReport;
//import org.objectweb.asm.tree.MethodNode;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by Yuvaraj on 27/02/2018.
// */
//public class TDDCollector {
//
//    RetrieveJUnitTests jUnitTests = new RetrieveJUnitTests();
//    RetrievePageNames retrievePageNames = new RetrievePageNames();
//    PageEngine pageEngine = new PageEngine();
//
//
//    public List<TestReport> collectReport(Class<?> testClass)  throws Exception {
//        List<TestReport> testReports = new ArrayList<>();
//        List<MethodNode> tests = jUnitTests.getTestNGTests(testClass);
//        for(MethodNode testMethod: tests) {
//            TestReport testReport = new TestReport();
//            testReport.setTestName(testMethod.name);
//            Map<String, List<String>> pagesAndMethodsUsedInTest = retrievePageNames.getPagesAndMethods(testMethod);
//            List<PageReport> pageReportList = new ArrayList<>();
//            for (Map.Entry<String, List<String>> entry : pagesAndMethodsUsedInTest.entrySet()) {
//                PageReport pageReport = new PageReport();
//                pageReport.setPageName(entry.getKey());
//                Map<String, List<String>> methodFieldMap = pageEngine.getSeleniumFieldsFromEachMethod(Class.forName(entry.getKey()));
//                List<PageMethodFieldReport> pageMethodReportList = new ArrayList<>();
//                for (Map.Entry<String, List<String>> entry1 : methodFieldMap.entrySet()) {
//                    //page method report
//                    PageMethodFieldReport pageMethodReport = new PageMethodFieldReport();
//                    pageMethodReport.setMethodName(entry1.getKey());
//                    pageMethodReport.setFields(methodFieldMap.get(entry1.getKey()));
//                    pageMethodReportList.add(pageMethodReport);
//                }
//                pageReport.setPageMethodReports(pageMethodReportList);
//                pageReportList.add(pageReport);
//            }
//            testReport.setPages(pageReportList);
//            testReports.add(testReport);
//        }
//        return testReports;
//    }
//}
