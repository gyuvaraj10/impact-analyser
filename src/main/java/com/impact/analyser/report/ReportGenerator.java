package com.impact.analyser.report;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

/**
 * Created by Yuvaraj on 11/03/2018.
 */
public class ReportGenerator {

    public void generateReport() throws IOException {
        String indexHtml = IOUtils.toString(this.getClass().getResourceAsStream("/app/index.html"), Charset.defaultCharset());
        String baseDirPath = System.getProperty("user.dir");
        File impactAnalysisDir = Paths.get(baseDirPath,"impact").toFile();
//        impactAnalysisDir.listFiles()
//        FileUtils.writeStringToFile(Paths.get(impactAnalysisDir.getAbsolutePath(), "report.json").toFile(), jsonReport, Charset.defaultCharset());
        FileUtils.writeStringToFile(Paths.get(impactAnalysisDir.getAbsolutePath(), "index.html").toFile(), indexHtml, Charset.defaultCharset());

    }
}
