package com.sample.tests;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class MyStepdefs {


    @When("^I do this$")
    public void iDoThis() throws Throwable {
        WebDriver driver = new ChromeDriver();
        LandingPage landingPage = new LandingPage(driver);
        landingPage.login();
    }

    @Then("^I should get this$")
    public void iShouldGetThis() throws Throwable {
        WebDriver driver = new ChromeDriver();
        LandingPage landingPage = new LandingPage(driver);
        landingPage.getasdkaskd();
    }
}
