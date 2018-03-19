package com.impact.analyser.configure;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Created by Yuvaraj on 18/03/2018.
 */
public class LoggingListner implements TypeListener {

    @Override
    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        Field field= null;
        try {
            field = typeLiteral.getRawType().getDeclaredField("logger");
            if(field != null && field.getType().isAssignableFrom(Logger.class)) {
                typeEncounter.register(new LoggingInjector(field, typeLiteral.getType().getTypeName()));
            }
        } catch (NoSuchFieldException e) {
        }
    }
}
