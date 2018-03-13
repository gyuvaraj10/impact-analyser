package com.sample.tests;

import org.openqa.selenium.By;

/**
 * Created by Yuvaraj on 13/03/2018.
 */
public class AnotherPage extends BaseSeleniumPage {

    public void login() {
        By use = By.className("");
        By pass = By.partialLinkText("dasd");
        use.findElement(null);
        pass.findElement(null);
    }
}
