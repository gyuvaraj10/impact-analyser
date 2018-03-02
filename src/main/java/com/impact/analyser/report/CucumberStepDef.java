package com.impact.analyser.report;

import java.util.List;

/**
 * Created by Yuvaraj on 02/03/2018.
 */
public class CucumberStepDef {

    private String name;

    private List<PageReport> pages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PageReport> getPages() {
        return pages;
    }

    public void setPages(List<PageReport> pages) {
        this.pages = pages;
    }
}
