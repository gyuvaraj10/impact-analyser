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

    private List<PageInfo> pages;

    public List<PageInfo> getPages() {
        return pages;
    }

    public void setPages(List<PageInfo> pages) {
        this.pages = pages;
    }
}
