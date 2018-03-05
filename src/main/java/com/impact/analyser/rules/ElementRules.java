package com.impact.analyser.rules;

import java.util.List;

/**
 * Created by Yuvaraj on 03/03/2018.
 */
public class ElementRules {

    public boolean isElementsDefinedWithInPageClassOnly() {
        return elementsDefinedWithInPageClassOnly;
    }

    public void setElementsDefinedWithInPageClassOnly(boolean elementsDefinedWithInPageClassOnly) {
        this.elementsDefinedWithInPageClassOnly = elementsDefinedWithInPageClassOnly;
    }

    public List<String> getElementClassPackages() {
        return elementClassPackages;
    }

    public void setElementClassPackages(List<String> elementClassPackages) {
        this.elementClassPackages = elementClassPackages;
    }

    public List<String> getPageClassPackages() {
        return pageClassPackages;
    }

    public void setPageClassPackages(List<String> pageClassPackages) {
        this.pageClassPackages = pageClassPackages;
    }

    private boolean elementsDefinedWithInPageClassOnly;
    private List<String> elementClassPackages;
    private List<String> pageClassPackages;

}
