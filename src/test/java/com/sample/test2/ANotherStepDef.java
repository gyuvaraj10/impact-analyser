package com.sample.test2;

import com.sample.tests.HomePage;
import cucumber.api.java.en.Given;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by Yuvaraj on 02/03/2018.
 */
public class ANotherStepDef {

    @Given("^I have this$")
    public void iHaveThis() throws Throwable {
        WebDriver driver = new ChromeDriver();
        HomePage page = new HomePage(driver);
        page.getOnlineCheckInHeaderText1();
        page.getOnlineCheckInHeaderText();
    }
}
