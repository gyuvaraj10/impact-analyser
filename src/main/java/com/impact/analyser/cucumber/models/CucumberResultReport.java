package com.impact.analyser.cucumber.models;

import java.util.List;

/**
 * Created by Yuvaraj on 02/03/2018.
 */
public class CucumberResultReport {

    String line;
    String name;
    String description;
    String id;
    String keyword;
    String uri;
    List<CucumberElement> elements;

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<CucumberElement> getElements() {
        return elements;
    }

    public void setElements(List<CucumberElement> elements) {
        this.elements = elements;
    }
}
