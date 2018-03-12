package com.impact.analyser;

//import com.impact.analyser.report.PageMethodFieldReport;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.impact.analyser.report.MethodInfo;
import com.impact.analyser.report.PageInfo;
import com.impact.analyser.report.TestReport;
import com.impact.analyser.rules.ElementRules;
import com.impact.analyser.rules.PageRules;
import org.apache.commons.io.FileUtils;
import org.apache.maven.wagon.PathUtils;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public  List<JsonObject> collectJsonReport(String[] testspackage) throws IOException, NoSuchFieldException, ClassNotFoundException {
        return getJsonObjectsForHtmlReport(collectReport(testspackage));
    }

    public Map<String, List<TestReport>> collectReport(String[] testspackage) throws IOException, NoSuchFieldException, ClassNotFoundException {
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
        Set<MethodNode> tests = jUnitTests.getTestNGTests(testClass);
        List<PageInfo> pageInfos = pageEngine.getSeleniumFieldsFromPageMethod(elementRules, pageRules);
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
