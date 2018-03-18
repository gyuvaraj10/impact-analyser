package com.impact.analyser.configure;

import com.google.inject.Injector;
import com.google.inject.MembersInjector;

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Created by Yuvaraj on 18/03/2018.
 */
public class LoggingInjector implements MembersInjector {

    Field field;
    Logger logger;

    public LoggingInjector(Field field) {
        try {
            this.field = field;
            File f = new File("./debug.log");
            FileHandler fileHandler = new FileHandler(f.getAbsolutePath());
            logger = Logger.getLogger(field.getDeclaringClass().getName());
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
