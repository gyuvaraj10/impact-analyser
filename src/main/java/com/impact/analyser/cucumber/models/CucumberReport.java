package com.impact.analyser.cucumber.models;

import com.impact.analyser.report.PageInfo;

import java.util.List;

/**
 * Created by Yuvaraj on 02/03/2018.
 */
public class CucumberReport {

    private String scenarioName;

    private List<PageInfo> pages;

    private List<String> stepDefinitions;

    public List<String> getStepDefinitions() {
        return stepDefinitions;
    }

    public void setStepDefinitions(List<String> stepDefinitions) {
        this.stepDefinitions = stepDefinitions;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public List<PageInfo> getPages() {
        return pages;
    }

    public void setPages(List<PageInfo> pages) {
        this.pages = pages;
    }
}
