package com.impact.analyser;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.objectweb.asm.Type.getInternalName;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
class ClassUtils {

    /**
     * returns field object from the fieldName
     * @param pageClass
     * @param fieldName
     * @return
     */
    public static Field getClassField(Class<?> pageClass, String fieldName) {
        Field field = null;
        try {
            field = pageClass.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException ex) {
        }
        return field;
    }

    /**
     * returns all pages classes from the given packages
     * @param packages
     * @param basePageClassName
     * @return
     * @throws ClassNotFoundException
     */
    public static Set<Class<?>> getAllPageTypesInPackages(List<String> packages, String basePageClassName) throws ClassNotFoundException {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(true))
                .forPackages(packages.toArray(new String[]{})));
        Class basePageClass = ClassLoader.getSystemClassLoader().loadClass(basePageClassName);
        Set<Class<?>> allClasses = reflections.getSubTypesOf(basePageClass);
        return allClasses.stream().filter(x->packages.stream().anyMatch(x.getName()::startsWith))
                .collect(Collectors.toSet());
    }

    public static Set<Class<?>> getAllTestTypesInPackages(List<String> packages) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new MethodAnnotationsScanner())
                .forPackages(packages.toArray(new String[]{})));
        Class<? extends Annotation> jUnitAnno = getAnnotationClass("org.junit.Test");
        Class<? extends Annotation> testNGAnno = getAnnotationClass("org.testng.annotations.Test");
        Set<Method> allMethods = new HashSet<>();
        if(jUnitAnno != null) {
            allMethods.addAll(reflections.getMethodsAnnotatedWith(jUnitAnno));
        }
        if(testNGAnno != null) {
            allMethods.addAll(reflections.getMethodsAnnotatedWith(testNGAnno));
        }
        Set<Class<?>> testClasses = new HashSet<>();
        for(Method m: allMethods) {
            testClasses.add(m.getDeclaringClass());
        }
        return testClasses.stream().filter(x->packages.stream().anyMatch(x.getName()::startsWith)).collect(Collectors.toSet());
    }

    public static Class<?> getClass(String fullyClass) {
        try {
            return Class.forName(fullyClass);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<? extends Annotation> getAnnotationClass(String fullyClass) {
        try {
            return (Class<? extends Annotation>) Class.forName(fullyClass);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static ClassNode getClassNode(Class<?> classClass) {
        ClassReader classR = null;
        try {
            classR = new ClassReader(getInternalName(classClass));
        } catch (IOException e) {
            return null;
        }
        ClassNode classNode = new ClassNode();
        classR.accept(classNode,0);
        return classNode;
    }
}
