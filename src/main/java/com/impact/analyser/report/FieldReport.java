package com.impact.analyser.report;

/**
 * Created by Yuvaraj on 18/03/2018.
 */
public class FieldReport {

    private String fieldName;
    private String fieldClass;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldClass() {
        return fieldClass;
    }

    public void setFieldClass(String fieldClass) {
        this.fieldClass = fieldClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldReport)) return false;

        FieldReport that = (FieldReport) o;

        if (!getFieldName().equals(that.getFieldName())) return false;
        return getFieldClass().equals(that.getFieldClass());

    }

    @Override
    public int hashCode() {
        int result = getFieldName().hashCode();
        result = 31 * result + getFieldClass().hashCode();
        return result;
    }
}
