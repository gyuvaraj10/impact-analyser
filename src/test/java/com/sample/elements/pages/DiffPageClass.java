package com.sample.elements.pages;

import com.sample.elements.otherclass.ElementsInOtherClass;

/**
 * Created by Yuvaraj on 03/03/2018.
 */
public class DiffPageClass {

    public void m1() {
        ElementsInOtherClass.loginBtn.toString();
        ElementsInOtherClass.password.toString();
    }

    public void m2() {
        ElementsInOtherClass.userName.toString();
    }

    public void m3() {
        m1();
        m2();
    }
}
