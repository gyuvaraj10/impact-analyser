package com.sample.tests;

import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import static org.junit.Assert.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class MyStepdefs extends BaseTest{


    LandingPage landingPage;

    @Before
    public void before() {
        throw new PendingException();
    }

    @Before(order = 1)
    public void before1() {
        assertTrue(true);
//        throw new RuntimeException("asds");
    }

    @When("^I do this$")
    public void iDoThis() throws Throwable {
        WebDriver driver = new ChromeDriver();
        LandingPage landingPage = new LandingPage(driver);
        landingPage.login();
    }

    @Then("^I should get this$")
    public void iShouldGetThis() throws Throwable {
        WebDriver driver = new ChromeDriver();
//        getPageObject(HomePage.class).getOnlineCheckInHeaderText();
        LandingPage.getLandingPage().getasdkaskd();
    }

    public <T extends BaseSeleniumPage> T getPageObject(Class<T> cl)  {
        try {
            return cl.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Given("^I have this(\\d+)$")
    public void iHaveThis(int arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
    }

    @Given("^I Test SKiped$")
    public void iTestSKiped() throws Throwable {

    }

    @Then("^I failed to test$")
    public void iFailedToTest() throws Throwable {

    }
}
