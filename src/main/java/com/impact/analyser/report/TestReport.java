package com.impact.analyser.report;

import java.util.Set;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class TestReport {

    private String testName;

    private String testClassName;

    private Set<FieldReport> fieldReports;

    public String getTestClassName() {
        return testClassName;
    }

    public void setTestClassName(String testClassName) {
        this.testClassName = testClassName;
    }

    public Set<FieldReport> getFieldReports() {
        return fieldReports;
    }

    public void setFieldReports(Set<FieldReport> fieldReports) {
        this.fieldReports = fieldReports;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestReport)) return false;

        TestReport that = (TestReport) o;

        if (!getTestName().equals(that.getTestName())) return false;
        if (!getTestClassName().equals(that.getTestClassName())) return false;
        return getFieldReports().equals(that.getFieldReports());

    }

    @Override
    public int hashCode() {
        int result = getTestName().hashCode();
        result = 31 * result + getTestClassName().hashCode();
        result = 31 * result + getFieldReports().hashCode();
        return result;
    }
}
