package com.impact.analyser;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.objectweb.asm.Type.getInternalName;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class ClassUtils {


    public static Set<Class<?>> getAllTypesInPackages(List<String> packages) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false))
                .forPackages(packages.toArray(new String[]{})));
        Set<String> allClasses = reflections.getAllTypes();
        return allClasses.stream().filter(x->packages.stream().anyMatch(x::startsWith))
                .map(ClassUtils::getClass).collect(Collectors.toSet());
    }

    public static Class<?> getClass(String fullyClass) {
        try {
            return Class.forName(fullyClass);
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
        }
        return Class.class;
    }

    public static ClassNode getClassNode(Class<?> classClass) {
        ClassReader classR = null;
        try {
            classR = new ClassReader(getInternalName(classClass));
        } catch (IOException e) {
//            e.printStackTrace();
        }
        ClassNode classNode = new ClassNode();
        classR.accept(classNode,0);
        return classNode;
    }
}
