package com.impact.analyser.report;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class PageReport {


    private String pageName;

    private Set<MethodReport> methodReportList;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Set<MethodReport> getMethodReportList() {
        return methodReportList;
    }

    public void setMethodReportList(Set<MethodReport> methodReportList) {
        this.methodReportList = methodReportList;
    }


}
