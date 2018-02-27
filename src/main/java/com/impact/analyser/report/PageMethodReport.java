package com.impact.analyser.report;

import java.util.List;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class PageMethodReport {

    private String methodName;
    private List<String> fields;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
