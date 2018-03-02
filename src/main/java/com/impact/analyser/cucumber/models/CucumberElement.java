package com.impact.analyser.cucumber.models;

import java.util.List;

/**
 * Created by Yuvaraj on 02/03/2018.
 */
public class CucumberElement {

    int line;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<CucumberStep> getSteps() {
        return steps;
    }

    public void setSteps(List<CucumberStep> steps) {
        this.steps = steps;
    }

    String name;
    String description;
    String id;
    String type;
    String keyword;
    private List<CucumberStep> steps;


}
