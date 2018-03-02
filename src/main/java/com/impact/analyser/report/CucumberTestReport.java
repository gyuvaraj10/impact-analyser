package com.impact.analyser.report;

import java.util.List;

/**
 * Created by Yuvaraj on 02/03/2018.
 */
public class CucumberTestReport {

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    private String scenarioName;

    private List<CucumberStepDef> stepDefs;

    public List<CucumberStepDef> getStepDefs() {
        return stepDefs;
    }

    public void setStepDefs(List<CucumberStepDef> stepDefs) {
        this.stepDefs = stepDefs;
    }
}
