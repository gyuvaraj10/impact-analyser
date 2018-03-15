package com.impact.analyser;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.impact.analyser.configure.ImpactAnalyserConfiguration;
import com.impact.analyser.rules.ElementRules;

/**
 * Created by Yuvaraj on 14/03/2018.
 */
public class Collector {

    public static Injector getInjector() {
        Injector injector = Guice.createInjector(new ImpactAnalyserConfiguration());
        return injector;
    }
}
