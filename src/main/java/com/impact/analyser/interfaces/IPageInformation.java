package com.impact.analyser.interfaces;

import com.impact.analyser.rules.PageRules;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * Created by Yuvaraj on 14/03/2018.
 */
public interface IPageInformation {

    Set<Class<?>> getAllPageTypesInPackages(PageRules pageRules);

    Map<Class<?>, ClassNode> getPageClassNodeMap(Set<Class<?>> classSet);

    Map<Class<?>, Set<String>> getPageElements(Set<Class<?>> pageClasses);

    Map<Class<?>, Set<String>> getPageMethods(Set<Class<?>> pageClasses);

    boolean isSeleniumField(Field field);

    boolean isSeleniumField(String type);
}
