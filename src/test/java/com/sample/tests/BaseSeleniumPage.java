package com.sample.tests;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Yuvaraj on 18/02/2018.
 */
public class BaseSeleniumPage {

    @FindBy(css = "#nav-your-amazon")
    protected WebElement baseElement;

    public String name;

}
