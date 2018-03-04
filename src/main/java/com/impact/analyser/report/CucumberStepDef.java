package com.impact.analyser.report;

import java.util.List;

/**
 * Created by Yuvaraj on 02/03/2018.
 */
public class CucumberStepDef {

    private String name;

    private List<PageInfo> pages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PageInfo> getPages() {
        return pages;
    }

    public void setPages(List<PageInfo> pages) {
        this.pages = pages;
    }
}
