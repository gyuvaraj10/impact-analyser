package com.impact.analyser.report;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Yuvaraj on 11/03/2018.
 */
public class ReportGenerator {

    public void generateReport(List<JsonObject> jsonObjects) throws IOException {
        String indexHtml = IOUtils.toString(this.getClass().getResourceAsStream("/app/index.html"), Charset.defaultCharset());
        String jsonReport = new Gson().toJson(jsonObjects);
        String baseDirPath = System.getProperty("user.dir");
        File impactAnalysisDir = Paths.get(baseDirPath,"impact").toFile();
        FileUtils.forceMkdir(impactAnalysisDir);
        FileUtils.writeStringToFile(Paths.get(impactAnalysisDir.getAbsolutePath(), "report.json").toFile(), jsonReport, Charset.defaultCharset());
        FileUtils.writeStringToFile(Paths.get(impactAnalysisDir.getAbsolutePath(), "index.html").toFile(), indexHtml, Charset.defaultCharset());

    }
}
