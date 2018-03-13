package com.sample.tests;

//import org.junit.Test;
import net.masterthought.cucumber.Reportable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class SampleTest extends BaseTest {

    HomePage page1;
    LandingPage landingPage;

    @Test
    public void testMain() {
        System.setProperty("webdriver.chrome.driver","/Users/Yuvaraj/Desktop/softwares/chromedriver/chromedriver");
        String actual = "Your Amazon.co.uk";
//        WebDriver driver = new ChromeDriver();
//        driver.get("https://www.amazon.co.uk/");
        page1.getOnlineCheckInHeaderText();
        AnotherPage page = new AnotherPage();
        page.login();
//        HomePage page = new HomePage(null);
        String onlineText = page1.getOnlineCheckInHeaderText();
//        page.getOnlineCheckInHeaderText1();
        LandingPage landingPage = new LandingPage(null);
        landingPage.getasdkaskd();
        landingPage.login();
        assertEquals(onlineText, actual);

    }

    @Test
    public void testMain234() {
        System.setProperty("webdriver.chrome.driver","/Users/Yuvaraj/Desktop/softwares/chromedriver/chromedriver");
        String actual = "Your Amazon.co.uk";
        AnotherPage page = new AnotherPage();
        page.login();
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.amazon.co.uk/");
        LandingPage landingPage = new LandingPage(driver);
        landingPage.getasdkaskd();
        landingPage.login();

    }

    @org.junit.Test
    public void mergeReport() {
        File reportOutputDirectory = new File("./");
        List<String> jsonFiles = Arrays.stream(reportOutputDirectory.listFiles())
                .filter(file->file.getName().contains("report.json"))
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
        String projectName = "cucumberProject";
        Configuration configuration = new Configuration(reportOutputDirectory, projectName);
        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        Reportable reportable = reportBuilder.generateReports();
        System.out.println(reportable.getSkippedSteps());
    }
}
