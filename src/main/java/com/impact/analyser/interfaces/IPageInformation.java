package com.impact.analyser.interfaces;

import com.impact.analyser.rules.PageRules;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Yuvaraj on 14/03/2018.
 */
public interface IPageInformation {

    Set<Class<?>> getAllPageTypesInPackages(PageRules pageRules);

    Map<Class<?>, Set<String>> getPageElements(Set<Class<?>> pageClasses);

    Map<Class<?>, Set<String>> getPageMethods(Set<Class<?>> pageClasses);
}
