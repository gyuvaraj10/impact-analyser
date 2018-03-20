package com.impact.analyser;

import com.google.gson.Gson;
import com.impact.analyser.report.TestReport;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Yuvaraj on 18/03/2018.
 */
public class GraphWriter {

    public void init(String testClass) throws IOException {
        String folderPath = String.format("./impact/%s", testClass);
        File folderFile = new File(folderPath);
        if(!folderFile.exists()) {
            FileUtils.forceMkdir(new File(folderPath));
        }
    }

    public void writeTestReport(TestReport testReport, String testMethodName, String testClassName) {
        String folderPath = String.format("./impact/%s/%s.json", testClassName, testMethodName);
        Gson gson = new Gson();
        String testReportJson = gson.toJson(testReport);
        try {
            FileUtils.writeStringToFile(new File(folderPath), testReportJson, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
