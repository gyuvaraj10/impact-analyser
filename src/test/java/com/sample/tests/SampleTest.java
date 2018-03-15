package com.sample.tests;



import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;

public class SampleTest extends BaseSeleniumTest {

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
}
