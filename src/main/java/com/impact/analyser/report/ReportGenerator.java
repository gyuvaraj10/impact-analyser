package com.impact.analyser.report;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import static java.lang.String.format;

/**
 * Created by Yuvaraj on 11/03/2018.
 */
public class ReportGenerator {

    private String appPath = "/app";
    private String[] resources = new String[]{"/app/bootstrap/css/bootstrap.css",
            "/app/bootstrap/css/bootstrap.min.css",
            "/app/bootstrap/css/bootstrap-grid.css",
            "/app/bootstrap/css/bootstrap-grid.min.css",
            "/app/bootstrap/css/bootstrap-reboot.css",
            "/app/bootstrap/css/bootstrap-reboot.min.css",
            "/app/bootstrap/js/bootstrap.bundle.js",
            "/app/bootstrap/js/bootstrap.bundle.min.js",
            "/app/bootstrap/js/bootstrap.js",
            "/app/bootstrap/js/bootstrap.min.js",
            "/app/jquery/jquery-1.8.2.min.js",
            "/app/jquery/style.css",
            "/app/chartjs/moment.min.js",
            "/app/chartjs/Chart.bundle.min.js",
            "/app/index.html"
    };

    public void generateReport() throws Exception {
        init();
        generateSummaryReport();
//        generateChartData();
        generateDetailedReport();
    }

    private void init() throws IOException {
        String baseDirPath = System.getProperty("user.dir");
        for(String resource: resources) {
            String indexHtml = IOUtils.toString(this.getClass().getResourceAsStream(resource), "UTF-8");
            File impactAnalysisDir = Paths.get(baseDirPath,"impact").toFile();
            FileUtils.writeStringToFile(Paths.get(impactAnalysisDir.getAbsolutePath(), resource).toFile(),
                    indexHtml, "UTF-8");
        }
    }

//    private void generateChartData() throws IOException {
//        String baseDirPath = System.getProperty("user.dir");
//        File impactAnalysisDir = Paths.get(baseDirPath,"impact").toFile();
//        String chartsFile = Paths.get(impactAnalysisDir.getAbsolutePath(),"app", "charts.js").toString();
//        FileWriter fileWriter = new FileWriter(chartsFile);
//        fileWriter.append("$(document).ready(function(){");
//        fileWriter.append("var ctx = $('#chart');");
//        fileWriter.append(format("var data=["));
//        List<ImpactReport> impactReports = getImpactReportSummary();
//        Set<String> pageNames = getAllPagesUsed();
//        for(String name: pageNames) {
//            if(!name.isEmpty()) {
//                Set<String> testSet = new HashSet<>();
//                for (ImpactReport impactReport : impactReports) {
//                    for (MethodImpactReport impact : impactReport.getTestMethods()) {
//                        for (Map.Entry<String, String> entry : impact.getFieldMaps().entrySet()) {
//                            String pageName = entry.getValue();
//                            if (name.equals(pageName)) {
//                                testSet.add(impact.getMethodName());
//                            }
//                        }
//                    }
//                }
//                fileWriter.append(format("{x:'%s',y:%s},", name, testSet.size()));
//            }
//        }
//        fileWriter.append("];");
//        fileWriter.append("var options = {}");
//        fileWriter.append("var myBarChart = new Chart(ctx, { type: 'horizontalBar', data: data, options:options});");
//        fileWriter.append("});");
//        fileWriter.flush();
//        fileWriter.close();
//    }


    private void generateDetailedReport() throws IOException {
        String baseDirPath = System.getProperty("user.dir");
        File impactAnalysisDir = Paths.get(baseDirPath,"impact").toFile();
        String detailedReportFileName = Paths.get(impactAnalysisDir.getAbsolutePath(),"app", "detailed-summary.js").toString();
        FileWriter fileWriter = new FileWriter(detailedReportFileName);
        fileWriter.append("$(document).ready(function(){");
        fileWriter.append("var tableBody = $('#detail-impact-summary tbody');");
        List<ImpactReport> impactReports = getImpactReportSummary();
        Set<String> pageNames = getAllPagesUsed();
        for(String name: pageNames) {
            if(!name.isEmpty()) {
                for (ImpactReport impactReport : impactReports) {
                    String testClass = impactReport.getTestClassName();
                    Set<String> testMethods = new HashSet<>();
                    for (MethodImpactReport impact : impactReport.getTestMethods()) {
                        for (Map.Entry<String, String> entry : impact.getFieldMaps().entrySet()) {
                            String pageName = entry.getValue();
                            if (name.equals(pageName)) {
                                testMethods.add(impact.getMethodName());
                            }
                        }
                    }
                    StringBuilder methods = new StringBuilder();
                    for(String testMethod: testMethods) {
                        methods.append(testMethod);
                        methods.append("\\n");
                    }
                    if(!methods.toString().isEmpty()) {
                     fileWriter.append(format("tableBody.append(\"<tr><td>%s</td><td>%s</td><td>%s</td></tr>\");",
                            name, testClass, methods.toString()));
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
        String testReportFileName = Paths.get(impactAnalysisDir.getAbsolutePath(),"app", "summary.js").toString();
        FileWriter fileWriter = new FileWriter(testReportFileName);
        fileWriter.append("$(document).ready(function(){");
        fileWriter.append("var tableBody = $('#impact-summary tbody');");
        List<ImpactReport> impactReports = getImpactReportSummary();
        Set<String> pageNames = getAllPagesUsed();
        for(String name: pageNames) {
            if(!name.isEmpty()) {
                Set<String> testSet = new HashSet<>();
                for (ImpactReport impactReport : impactReports) {
                    for (MethodImpactReport impact : impactReport.getTestMethods()) {
                        for (Map.Entry<String, String> entry : impact.getFieldMaps().entrySet()) {
                            String pageName = entry.getValue();
                            if (name.equals(pageName)) {
                                testSet.add(impact.getMethodName());
                            }
                        }
                    }
                }
                fileWriter.append(format("tableBody.append('<tr><td>%s</td><td>%s</td></tr>');", name, testSet.size()));
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
            if( impactReport.getTestMethods() != null) {
                for (MethodImpactReport impact : impactReport.getTestMethods()) {
                    if (impact != null || impact.getFieldMaps() != null) {
                        for (Map.Entry<String, String> entry : impact.getFieldMaps().entrySet()) {
                            String pageName = entry.getValue();
                            pageNames.add(pageName);
                        }
                    }
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
        File[] files = impactAnalysisDir.listFiles();
        if(files != null) {
            for (File file : files) {
                ImpactReport impactReport = new ImpactReport();
                if (file.isDirectory() && !file.getName().equals("app") && file.listFiles().length != 0) {
                    impactReport.setTestClassName(file.getName());
                    File[] testReportFiles = file.listFiles();
                    List<MethodImpactReport> pageReports = new ArrayList<>();
                    if(testReportFiles != null && testReportFiles.length !=0) {
                        for (File testReportFile : testReportFiles) {
                            MethodImpactReport methodImpactReport = new MethodImpactReport();
                            String content = FileUtils.readFileToString(testReportFile, "UTF-8");
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
                    }
                    impactReport.setTestMethods(pageReports);
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
