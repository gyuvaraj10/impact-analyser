package com.sample.tests;

import com.google.inject.Injector;
import com.impact.analyser.*;
import com.impact.analyser.report.ReportGenerator;
import com.impact.analyser.rules.TestRules;
import com.impact.analyser.rules.PageRules;
import org.junit.Test;
import java.util.*;

/**
 * Created by Yuvaraj on 20/02/2018.
 */
public class AsmTest {


    @Test
    public void testTDDCollector() {
        try {
            PageRules pageRules = new PageRules();
            pageRules.setBasePageClass("com.sample.tests.BaseSeleniumPage");
            pageRules.setPageClassPackages(Arrays.asList("com.sample"));
            TestRules testRules = new TestRules();
            testRules.setBaseTestClass("com.sample.tests.BaseSeleniumTest");
            testRules.setTestClassPackages(Arrays.asList("com.sample.tests"));
            Injector injector = Collector.getInjector();
            injector.injectMembers(pageRules);
            injector.injectMembers(testRules);
            TDDCollector tddCollector = injector.getInstance(TDDCollector.class);
            tddCollector.setPageRules(pageRules);
            tddCollector.setTestRules(testRules);
            tddCollector.collectTrace(new String[]{"com.sample"});
            ReportGenerator reportGenerator = new ReportGenerator();
            reportGenerator.generateReport();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testBDDCollector() {
        try {
            PageRules pageRules = new PageRules();
            pageRules.setBasePageClass("com.sample.tests.BaseSeleniumPage");
            pageRules.setPageClassPackages(Arrays.asList("com.sample"));
            TestRules testRules = new TestRules();
            testRules.setBaseTestClass("com.sample.tests.BaseTest");
            testRules.setTestClassPackages(Arrays.asList("com.sample"));
            Injector injector = Collector.getInjector();
            BDDCollector bddCollector = injector.getInstance(BDDCollector.class);
            bddCollector.setPageRules(pageRules);
            bddCollector.setTestRules(testRules);
            bddCollector.collectTrace(new String[]{"com.sample"}, "/Users/Yuvaraj/dev/impact-analyser/src/test/resources");
            ReportGenerator reportGenerator = new ReportGenerator();
            reportGenerator.generateReport();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}


