package com.impact.analyser.runner;

import com.impact.analyser.TDDCollector;
import com.impact.analyser.report.TestReport;
import com.impact.analyser.rules.ElementRules;
import com.impact.analyser.rules.PageRules;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuvaraj on 09/03/2018.
 */
@Mojo(name="impactAnalyser")
public class ImpactAnalyserMojo extends AbstractMojo {

    @Parameter(name = "basePageClass", required = true)
    private String basePageClass;

    @Parameter(name = "elementsInSamePage", required = true)
    private boolean elementsDefinedWithInPage;

    @Parameter(name = "elementClassPackages", required = true)
    private List<String> elementClassPackages;

    @Parameter(required = true)
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("Base Page Class");
        System.out.println(basePageClass);
        System.out.println("Base Page Class");
        System.out.println(elementClassPackages.get(0));
        System.out.println("Base Page Class");
        System.out.println(elementsDefinedWithInPage);
        PageRules pageRules = new PageRules();
        pageRules.setBasePageClass("com.sample.tests.BaseSeleniumPage");
        ElementRules elementRules = new ElementRules();
        elementRules.setElementsDefinedWithInPageClassOnly(false);
        elementRules.setElementClassPackages(Arrays.asList("com.sample"));
        elementRules.setPageClassPackages(Arrays.asList("com.sample.test2", "com.sample.tests"));
        TDDCollector tddCollector = new TDDCollector(pageRules, elementRules);
        try {
            Map<String, List<TestReport>> reports = tddCollector.collectReportForAPackage(new String[]{"com.sample.tests"});

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
