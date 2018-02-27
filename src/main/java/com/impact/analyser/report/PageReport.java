package com.impact.analyser.report;

import com.impact.analyser.report.PageMethodReport;

import java.util.List;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class PageReport {

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public List<PageMethodReport> getPageMethodReports() {
        return pageMethodReports;
    }

    public void setPageMethodReports(List<PageMethodReport> pageMethodReports) {
        this.pageMethodReports = pageMethodReports;
    }

    private List<PageMethodReport> pageMethodReports;

    private String pageName;

}
