package com.impact.analyser.rules;

import java.util.List;

/**
 * Created by Yuvaraj on 03/03/2018.
 */
public class PageRules {

    public List<String> getPageClassPackages() {
        return pageClassPackages;
    }

    public void setPageClassPackages(List<String> pageClassPackages) {
        this.pageClassPackages = pageClassPackages;
    }

    public String getBasePageClass() {
        return basePageClass;
    }

    public void setBasePageClass(String basePageClass) {
        this.basePageClass = basePageClass;
    }

    private String basePageClass;

    private List<String> pageClassPackages;
}
