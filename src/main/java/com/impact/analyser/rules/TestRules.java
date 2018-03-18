package com.impact.analyser.rules;

import java.util.List;

/**
 * Created by Yuvaraj on 14/03/2018.
 */
public class TestRules {

    public String getBaseTestClass() {
        return baseTestClass;
    }

    public void setBaseTestClass(String baseTestClass) {
        this.baseTestClass = baseTestClass;
    }

    private String baseTestClass;

    public List<String> getTestClassPackages() {
        return testClassPackages;
    }

    public void setTestClassPackages(List<String> testClassPackages) {
        this.testClassPackages = testClassPackages;
    }

    private List<String> testClassPackages;
}
