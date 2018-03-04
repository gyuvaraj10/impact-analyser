package com.impact.analyser.report;

import java.util.List;
import java.util.Map;

/**
 * Created by Yuvaraj on 03/03/2018.
 */
public class MethodInfo {

    private String methodName;
    private List<String> privateMethods;
    private Map<String, String> fieldAndFieldClassName;

    public List<String> getPrivateMethods() {
        return privateMethods;
    }

    public void setSameClassMethods(List<String> privateMethods) {
        this.privateMethods = privateMethods;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<String, String> getFieldAndFieldClassName() {
        return fieldAndFieldClassName;
    }

    public void setFieldAndFieldClassName(Map<String, String> fieldAndFieldClassName) {
        this.fieldAndFieldClassName = fieldAndFieldClassName;
    }
}
