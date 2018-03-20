package com.impact.analyser.report;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.*;
import static java.lang.String.format;

/**
 * Created by Yuvaraj on 11/03/2018.
 */
public class ReportGenerator {


    public void generateReport() throws Exception {
        init();
        generateSummaryReport();
        generateDetailedReport();
    }

    private void init() throws IOException {
        String indexHtml = IOUtils.toString(this.getClass().getResourceAsStream("/app/index.html"), Charset.defaultCharset());
        String baseDirPath = System.getProperty("user.dir");
        File impactAnalysisDir = Paths.get(baseDirPath,"impact").toFile();
        File sourceDirectory = new File(this.getClass().getResource("/app").getPath());
        FileUtils.copyDirectory(sourceDirectory, impactAnalysisDir);
        FileUtils.writeStringToFile(Paths.get(impactAnalysisDir.getAbsolutePath(), "index.html").toFile(),
                indexHtml, Charset.defaultCharset());

    }


    private void generateDetailedReport() throws IOException {
        String baseDirPath = System.getProperty("user.dir");
        File impactAnalysisDir = Paths.get(baseDirPath,"impact").toFile();
        String detailedReportFileName = Paths.get(impactAnalysisDir.getAbsolutePath(), "detailed-summary.js").toString();
        FileWriter fileWriter = new FileWriter(detailedReportFileName);
        fileWriter.append("$(document).ready(function(){");
        fileWriter.append("var tableBody = $('#detail-impact-summary tbody');");
        List<ImpactReport> impactReports = getImpactReportSummary();
        Set<String> pageNames = getAllPagesUsed();
        for(String name: pageNames) {
            if(!name.isEmpty()) {
                for (ImpactReport impactReport : impactReports) {
                    String testClass = impactReport.getTestClassName();
                    StringBuilder testMethods = new StringBuilder();
                    for (MethodImpactReport impact : impactReport.getTestMethods()) {
                        for (Map.Entry<String, String> entry : impact.getFieldMaps().entrySet()) {
                            String pageName = entry.getValue();
                            if (name.equals(pageName)) {
                                testMethods.append(impact.getMethodName());
                                testMethods.append("\\n");
                            }
                        }
                    }
                    String tms = testMethods.toString();
                    if(!tms.isEmpty()) {
                     fileWriter.append(format("tableBody.append(\"<tr><td>%s</td><td>%s</td><td>%s</td></tr>\");",
                            name, testClass, tms));
                    }

                }

            }
        }
        fileWriter.append("});");
        fileWriter.flush();
        fileWriter.close();
    }

    /**
     * generates the summary report table
     * @throws IOException
     */
    private void generateSummaryReport() throws IOException {
        String baseDirPath = System.getProperty("user.dir");
        File impactAnalysisDir = Paths.get(baseDirPath,"impact").toFile();
        String testReportFileName = Paths.get(impactAnalysisDir.getAbsolutePath(), "summary.js").toString();
        FileWriter fileWriter = new FileWriter(testReportFileName);
        fileWriter.append("$(document).ready(function(){");
        fileWriter.append("var tableBody = $('#impact-summary tbody');");
        List<ImpactReport> impactReports = getImpactReportSummary();
        Set<String> pageNames = getAllPagesUsed();
        for(String name: pageNames) {
            if(!name.isEmpty()) {
                int testCount = 0;
                for (ImpactReport impactReport : impactReports) {
                    for (MethodImpactReport impact : impactReport.getTestMethods()) {
                        for (Map.Entry<String, String> entry : impact.getFieldMaps().entrySet()) {
                            String pageName = entry.getValue();
                            if (name.equals(pageName)) {
                                testCount++;
                            }
                        }
                    }
                }
                fileWriter.append(format("tableBody.append('<tr><td>%s</td><td>%s</td></tr>');", name, testCount));
            }
        }
        fileWriter.append("});");
        fileWriter.flush();
        fileWriter.close();
    }

    /**
     * gets the page names used
     * @return
     * @throws IOException
     */
    private Set<String> getAllPagesUsed() throws IOException {
        List<ImpactReport> impactReports = getImpactReportSummary();
        Set<String> pageNames = new HashSet<>();
        for(ImpactReport impactReport : impactReports) {
            for(MethodImpactReport impact: impactReport.getTestMethods()) {
                for(Map.Entry<String, String> entry: impact.getFieldMaps().entrySet()) {
                    String pageName = entry.getValue();
                    pageNames.add(pageName);
                }
            }
        }
        return pageNames;
    }

    /**
     * gets the complete impact summary report from the generated jsonf iles
     * @return
     * @throws IOException
     */
    private List<ImpactReport> getImpactReportSummary() throws IOException {
        List<ImpactReport> impactReports = new ArrayList<>();
        String baseDirPath = System.getProperty("user.dir");
        File impactAnalysisDir = Paths.get(baseDirPath,"impact").toFile();
        File sourceDirectory = new File(this.getClass().getResource("/app").getPath());
        FileUtils.copyDirectory(sourceDirectory, impactAnalysisDir);
        File[] files = impactAnalysisDir.listFiles();
        if(files != null) {
            for (File file : files) {
                ImpactReport impactReport = new ImpactReport();
                if (file.isDirectory() && !file.getName().equals("bootstrap")
                        && !file.getName().equals("jquery")) {
                    impactReport.setTestClassName(file.getName());
                    File[] testReportFiles = file.listFiles();
                    if(testReportFiles != null) {
                        List<MethodImpactReport> pageReports = new ArrayList<>();
                        for (File testReportFile : testReportFiles) {
                            MethodImpactReport methodImpactReport = new MethodImpactReport();
                            String content = FileUtils.readFileToString(testReportFile, Charset.defaultCharset());
                            Gson gson = new Gson();
                            JsonObject jsonObject = gson.fromJson(content, JsonObject.class);
                            JsonArray fieldReportArray = jsonObject.getAsJsonArray("fieldReports");
                            Map<String, String> map = new HashMap<>();
                            fieldReportArray.forEach(fieldReport -> {
                                JsonObject jsonObject1 = fieldReport.getAsJsonObject();
                                String elementName = jsonObject1.get("fieldName").getAsString();
                                String pageClass = jsonObject1.get("fieldClass").getAsString();
                                map.put(elementName, pageClass);
                            });
                            methodImpactReport.setFieldMaps(map);
                            methodImpactReport.setMethodName(jsonObject.get("testName").getAsString());
                            pageReports.add(methodImpactReport);
                        }
                        impactReport.setTestMethods(pageReports);
                    }
                }
                if(impactReport.getTestClassName() != null) {
                    impactReports.add(impactReport);
                }
            }
        }
        return impactReports;
    }


    private class ImpactReport {

        private String testClassName;
        private List<MethodImpactReport> testMethods;

        public String getTestClassName() {
            return testClassName;
        }

        public void setTestClassName(String testClassName) {
            this.testClassName = testClassName;
        }

        public List<MethodImpactReport> getTestMethods() {
            return testMethods;
        }

        public void setTestMethods(List<MethodImpactReport> testMethods) {
            this.testMethods = testMethods;
        }
    }

    private class MethodImpactReport {
        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public Map<String, String> getFieldMaps() {
            return fieldMaps;
        }

        public void setFieldMaps(Map<String, String> fieldMaps) {
            this.fieldMaps = fieldMaps;
        }

        private String methodName;
        private Map<String, String> fieldMaps;
    }
}
