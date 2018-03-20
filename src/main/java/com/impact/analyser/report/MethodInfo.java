package com.impact.analyser.report;

import java.util.List;
import java.util.Map;

/**
 * Created by Yuvaraj on 03/03/2018.
 */
public class MethodInfo {

    private String methodName;
    private String methodClass;
    private boolean pageItem;

//    private List<String> privateMethods;
    private Map<String, String> fieldAndFieldClassName;

    public boolean isPageItem() {
        return pageItem;
    }

    public void setPageItem(boolean pageItem) {
        this.pageItem = pageItem;
    }

    public String getMethodClass() {
        return methodClass;
    }

    public void setMethodClass(String methodClass) {
        this.methodClass = methodClass;
    }

//    public List<String> getPrivateMethods() {
//        return privateMethods;
//    }
//
//    public void setSameClassMethods(List<String> privateMethods) {
//        this.privateMethods = privateMethods;
//    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodInfo)) return false;

        MethodInfo that = (MethodInfo) o;

        if (isPageItem() != that.isPageItem()) return false;
        if (!getMethodName().equals(that.getMethodName())) return false;
        if (!getMethodClass().equals(that.getMethodClass())) return false;
        return getFieldAndFieldClassName().equals(that.getFieldAndFieldClassName());

    }

    @Override
    public int hashCode() {
        int result = getMethodName().hashCode();
        result = 31 * result + getMethodClass().hashCode();
        result = 31 * result + (isPageItem() ? 1 : 0);
        result = 31 * result + getFieldAndFieldClassName().hashCode();
        return result;
    }
}
