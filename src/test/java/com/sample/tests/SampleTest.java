package com.sample.tests;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import static org.junit.Assert.assertEquals;

public class SampleTest {

    @Test
    public void testMain() {
        System.setProperty("webdriver.chrome.driver","/Users/Yuvaraj/Desktop/softwares/chromedriver/chromedriver");
        String actual = "Your Amazon.co.uk";
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.amazon.co.uk/");
        HomePage page = PageFactory.initElements(driver, HomePage.class);
        String onlineText = page.getOnlineCheckInHeaderText();
        page.getOnlineCheckInHeaderText1();
        LandingPage landingPage = new LandingPage(driver);
        landingPage.getasdkaskd();
        landingPage.login();
        assertEquals(onlineText, actual);

    }
}
