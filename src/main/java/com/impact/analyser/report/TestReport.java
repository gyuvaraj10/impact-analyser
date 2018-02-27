package com.impact.analyser.report;

import java.util.List;

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

    private List<PageReport> pages;

    public List<PageReport> getPages() {
        return pages;
    }

    public void setPages(List<PageReport> pages) {
        this.pages = pages;
    }
}
