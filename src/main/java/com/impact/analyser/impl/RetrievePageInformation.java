package com.impact.analyser.impl;

import com.google.inject.Inject;
import com.impact.analyser.utils.ClassUtils;
import com.impact.analyser.exceptions.PageClassNotFoundException;
import com.impact.analyser.interfaces.IPageInformation;
import com.impact.analyser.rules.PageRules;
import org.apache.commons.lang3.ArrayUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.ConfigurationBuilder;
import com.google.common.collect.Multimap;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Yuvaraj on 14/03/2018.
 */
public class RetrievePageInformation implements IPageInformation {


    private static final Logger logger = Logger.getLogger(RetrievePageInformation.class.getName());

    @Inject
    private RetrieveTestInformation retrieveTestInformation;

    /**
     * returns all pages classes from the given packages
     * @param pageRules
     * @return
     */
    @Override
    public Set<Class<?>> getAllPageTypesInPackages(PageRules pageRules) {
        String[] packages = pageRules.getPageClassPackages().toArray(new String[]{});
        String basePageClassName = pageRules.getBasePageClass();
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(true))
                .forPackages(packages));
        Class basePageClass = null;
        try {
            basePageClass = ClassLoader.getSystemClassLoader().loadClass(basePageClassName);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Can not find base page class {0}", basePageClassName);
            throw new PageClassNotFoundException(e);
        }
        Set<Class<?>> allClasses = reflections.getSubTypesOf(basePageClass);
        allClasses.addAll(getPageClassesWithOutBaseClass(pageRules));
        allClasses.add(basePageClass);
        return allClasses.stream().filter(x-> Arrays.stream(packages).anyMatch(x.getName()::startsWith)
                && !retrieveTestInformation.isTestClass(x))
                .collect(Collectors.toSet());
    }


    /**
     * returns the page and it's selenium element field names
     * @param pageClasses
     * @return
     */
    public Map<Class<?>, Set<String>> getPageElements(Set<Class<?>> pageClasses)  {
       return getSeleniumFieldsFromClasses(pageClasses);
    }

    /**
     * returns the page and its method names
     * @param pageClasses
     * @return
     */
    public Map<Class<?>, Set<String>> getPageMethods(Set<Class<?>> pageClasses)  {
        return getMethodsFromClasses(pageClasses);
    }

    /**
     * returns map of class and it's methods
     * @param classSet
     * @return
     */
    private Map<Class<?>, Set<String>> getMethodsFromClasses(Set<Class<?>> classSet) {
        Map<Class<?>, Set<String>> map = new HashMap<>();
        for (Class<?> elementClass : classSet) {
            Set<String> methodNames = Arrays.stream(elementClass.getDeclaredMethods()).map(Method::getName).collect(Collectors.toSet());
            if(methodNames.size() > 0) {
                map.put(elementClass, methodNames);
                logger.log(Level.INFO, "Found {1} number of methods from page class {0}",
                        new Object[]{elementClass, methodNames.size()});
            }
        }
        return map;
    }

    /**
     * returns a map of class name and selneium fields
     * @param classSet
     * @return
     */
    private Map<Class<?>, Set<String>> getSeleniumFieldsFromClasses(Set<Class<?>> classSet) {
        Map<Class<?>, Set<String>> map = new HashMap<>();
        for (Class<?> elementClass : classSet) {
            Field[] allFields = ArrayUtils.addAll(elementClass.getDeclaredFields(), elementClass.getSuperclass().getDeclaredFields());
            Set<String> seleniumFields = Arrays.stream(allFields).filter(this::isSeleniumField)
                    .map(Field::getName).collect(Collectors.toSet());
            if (seleniumFields.size() > 0) {
                map.put(elementClass, seleniumFields);
                logger.log(Level.INFO, "Found {1} number of elements from page class {0}",
                        new Object[]{elementClass, seleniumFields.size()});
            }
        }
        return map;
    }

    /**
     * returns the page classes without base class
     * @param pageRules
     * @return
     */
    private Set<Class<?>> getPageClassesWithOutBaseClass(PageRules pageRules) {
        String[] packages = pageRules.getPageClassPackages().toArray(new String[]{});
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new TypeElementsScanner())
                .forPackages(packages));
        Multimap<String, String> allTypes = reflections.getStore().get("TypeElementsScanner");
        Set<Class<?>> allPageClasses = new HashSet<>();
        for(Map.Entry<String, Collection<String>> type: allTypes.asMap().entrySet()) {
            String className = type.getKey();
            Class<?> typeClass = ClassUtils.getClass(className);
            if(typeClass != null) {
                for (String field : type.getValue()) {
                    if (!field.equals("\"\"") || !field.endsWith("()")) {
                        try {
                            Field field1 = typeClass.getDeclaredField(field);
                            if (isSeleniumField(field1)) {
                                allPageClasses.add(typeClass);
                                break;
                            }
                        } catch (NoSuchFieldException e) {
                            logger.log(Level.SEVERE, "Failed to load the field {0} please keep on this field", field);
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                logger.info("Class "+ className+ " is Not loaded");
            }
        }
        return allPageClasses;
    }

    /**
     * verifies if the field is a selenium field
     * @param field
     * @return
     */
    private boolean isSeleniumField(Field field) {
        Class<?> webElementClass = ClassUtils.getClass("org.openqa.selenium.WebElement");
        Class<?> findByClass = ClassUtils.getAnnotationClass("org.openqa.selenium.support.FindBy");
        Class<?> findAllClass = ClassUtils.getAnnotationClass("org.openqa.selenium.support.FindAll");
        Class<?> findBysClass = ClassUtils.getAnnotationClass("org.openqa.selenium.support.FindBys");
        Class<?> ByClass = ClassUtils.getClass("org.openqa.selenium.By");
        if(webElementClass != null && field.getType().isAssignableFrom(webElementClass)) {
            return true;
        } else if(field.getType().isAssignableFrom(List.class)) {
            List<Annotation> annotations = Arrays.asList(field.getDeclaredAnnotations());
            if(findByClass!= null && findAllClass != null && findBysClass != null){
                if(annotations.stream().anyMatch(x->x.annotationType().isAssignableFrom(findByClass)
                    || x.annotationType().isAssignableFrom(findAllClass)
                    || x.annotationType().isAssignableFrom(findBysClass))) {
                        return true;
                 }
            }
            String byList = "java.util.List<org.openqa.selenium.By>";
            String genericType = field.getGenericType().getTypeName();
            if(genericType.equals(byList)) {
                return true;
            }
        } else if(ByClass != null && field.getType().isAssignableFrom(ByClass)) {
            return true;
        }
        return false;
    }

    private boolean isSeleniumField(String type) {
        return (type.equals("org.openqa.selenium.By") ||
                type.equals("org.openqa.selenium.WebElement") ||
                type.equals("org.openqa.selenium.support.FindBy") ||
                type.equals("org.openqa.selenium.support.FindAll") ||
                type.equals("org.openqa.selenium.support.FindBys"));
    }
}
