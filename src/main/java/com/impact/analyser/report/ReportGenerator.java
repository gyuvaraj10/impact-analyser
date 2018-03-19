package com.impact.analyser.report;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

/**
 * Created by Yuvaraj on 11/03/2018.
 */
public class ReportGenerator {

    @Test
    public void testReportGenerator() throws Exception {
        generateReport();
    }

    public void generateReport() throws IOException {
        String indexHtml = IOUtils.toString(this.getClass().getResourceAsStream("/app/index.html"), Charset.defaultCharset());
        String baseDirPath = System.getProperty("user.dir");
        File impactAnalysisDir = Paths.get(baseDirPath,"impact").toFile();
        File sourceDirectory = new File(this.getClass().getResource("/app").getPath());
        FileUtils.copyDirectory(sourceDirectory, impactAnalysisDir);
        File[] files = impactAnalysisDir.listFiles();
        if(files != null) {
            for (File file : files) {
                if (file.isDirectory() && !file.getName().equals("bootstrap")) {
                    File[] testReportFiles = file.listFiles();
                    if(testReportFiles != null) {
                        for (File testReportFile : testReportFiles) {
                            FileUtils.readFileToString(testReportFile, Charset.defaultCharset());
                        }
                    }
                }
            }
        }
        FileUtils.writeStringToFile(Paths.get(impactAnalysisDir.getAbsolutePath(), "index.html").toFile(), indexHtml, Charset.defaultCharset());

    }
}
