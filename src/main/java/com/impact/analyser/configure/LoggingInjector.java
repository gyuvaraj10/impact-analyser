package com.impact.analyser.configure;


import com.google.inject.MembersInjector;

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by Yuvaraj on 18/03/2018.
 */
public class LoggingInjector implements MembersInjector {

    private Field field;
    private Logger logger;

    public LoggingInjector(Field field, String name) {
        try {
            this.field = field;
            File f = new File(String.format("./%s.log",name));
            FileHandler fileHandler = new FileHandler(f.getAbsolutePath());
            logger = Logger.getLogger(field.getDeclaringClass().getName());
            System.setProperty("java.util.logging.SimpleFormatter.format",
                    "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$-7s [%3$s] (%2$s) %5$s %6$s%n");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            field.setAccessible(true);
        } catch (Exception ex) {

        }
    }
    @Override
    public void injectMembers(Object o) {
        try {
            field.set(o, logger);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
