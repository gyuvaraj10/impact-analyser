package com.sample.tests;

import com.google.gson.Gson;
import com.impact.analyser.*;
import com.impact.analyser.report.PageMethodReport;
import com.impact.analyser.report.PageReport;
import com.impact.analyser.report.TestReport;
import org.junit.Test;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuvaraj on 20/02/2018.
 */
public class AsmTest {

    @Test
    public void testGet()  throws Exception {
        TDDCollector tddCollector = new TDDCollector();
        System.out.println(new Gson().toJson(tddCollector.collectReport(SampleTest.class)));
    }
}


