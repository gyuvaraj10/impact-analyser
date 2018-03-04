package com.impact.analyser.report;

import java.util.Set;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class PageInfo {

    private String pageName;

    private Set<MethodInfo> methodReportList;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Set<MethodInfo> getMethodReportList() {
        return methodReportList;
    }

    public void setMethodReportList(Set<MethodInfo> methodReportList) {
        this.methodReportList = methodReportList;
    }
}
