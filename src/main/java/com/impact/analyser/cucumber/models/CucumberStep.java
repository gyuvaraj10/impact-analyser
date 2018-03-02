package com.impact.analyser.cucumber.models;

/**
 * Created by Yuvaraj on 02/03/2018.
 */
public class CucumberStep {

    int line;
    String name;

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public StepResult getResult() {
        return result;
    }

    public void setResult(StepResult result) {
        this.result = result;
    }

    public StepMatch getStepMatch() {
        return match;
    }

    public void setStepMatch(StepMatch match) {
        this.match = match;
    }

    String keyword;
    StepResult result;
    StepMatch match;

}
