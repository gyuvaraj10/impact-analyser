package com.impact.analyser.report;

import java.util.List;
import java.util.Set;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class TestReport {

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    private String testName;

    public Set<MethodInfo> getMethodInfos() {
        return methodInfos;
    }

    public void setMethodInfos(Set<MethodInfo> methodInfos) {
        this.methodInfos = methodInfos;
    }

    private Set<MethodInfo> methodInfos;

//    private List<PageInfo> pages;

//    public List<PageInfo> getPages() {
//        return pages;
//    }
//
//    public void setPages(List<PageInfo> pages) {
//        this.pages = pages;
//    }
}
