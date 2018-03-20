package com.impact.analyser.interfaces;

import com.impact.analyser.report.TestReport;
import com.impact.analyser.rules.PageRules;
import com.impact.analyser.rules.TestRules;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by Yuvaraj on 15/03/2018.
 */
public interface ITestMapper {

    void setPageRules(PageRules pageRules);

    void setTestRules(TestRules pageRules);

    void setPageClassNodes(Map<Class<?>, ClassNode> pageClassNodes);

    void setPageAndMethods(Map<Class<?>, Set<String>> pageAndMethods);

    void setPageAndElements(Map<Class<?>, Set<String>> pageAndElements);

    void setPageClasses(Set<Class<?>> pageClasses);

    void setTestClassMethods(Map<Class<?>, Set<MethodNode>> testClassMethodMap);

    void map(Map<Class<?>, ClassNode> testClassNodes,
                                      Map<Class<?>, Set<MethodNode>> testClassAndMethods) throws IOException;
    void mapCucumber(Map<String, Map<String, MethodNode>> scenarioSteps, Map<Class<?>, ClassNode> testClassNodes,
             Map<Class<?>, Set<MethodNode>> testClassAndMethods) throws IOException;
}
