package com.impact.analyser.cucumber;


import com.impact.analyser.ClassUtils;
import com.impact.analyser.cucumber.models.CucumberElement;
import com.impact.analyser.cucumber.models.CucumberResultReport;
import com.impact.analyser.cucumber.models.CucumberStep;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.*;

import static org.objectweb.asm.Type.getInternalName;

/**
 * Created by Yuvaraj on 02/03/2018.
 */
public class RetrieveCucumberStepDefinitions {

    public Map<String, Map<String, MethodNode>> getCucumberStepAndDefinitionForAScenario(CucumberResultReport feature, String[] glues) throws ClassNotFoundException, IOException {
        Map<String, Map<String, MethodNode>> scenarios = new HashMap<>();
        List<CucumberElement> elements = feature.getElements();
        Optional<CucumberElement> backGroundEle = elements.stream().filter(x->x.getType().equals("background")).findFirst();
        Map<String, MethodNode> backGroundstepDefs = null;
        if(backGroundEle.isPresent()) {
            CucumberElement element = backGroundEle.get();
            backGroundstepDefs = getStepsDefDetails(element, glues);
        }
        for(CucumberElement element: feature.getElements()) {
            if(!element.getType().equals("background")) {
                Map<String, MethodNode> stepDefs = getStepsDefDetails(element, glues);
                Map<String, MethodNode> stepDefinitionsIncludingBackGround = new HashMap<>();
                if(backGroundstepDefs != null) {
                    stepDefinitionsIncludingBackGround.putAll(backGroundstepDefs);
                }
                if(stepDefs != null) {
                    stepDefinitionsIncludingBackGround.putAll(stepDefs);
                }
                scenarios.put(element.getName(), stepDefinitionsIncludingBackGround);
            }
        }
        return scenarios;
    }

    private Map<String, MethodNode> getStepsDefDetails(CucumberElement element, String[] glues) throws ClassNotFoundException, IOException {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        Map<String, MethodNode> stepDefs = new HashMap<>();
        for(CucumberStep step: element.getSteps()) {
            String location = step.getStepMatch().getLocation();
            if(!location.equals("undefined")) {
                String className = location.split("\\.")[0];
                String stepMethodName = location.split("\\.")[1];
                Class<?> stepDefClass = null;
                Set<Class<?>> stepDefClasses = ClassUtils.getAllStepDefsInPackages(Arrays.asList(glues));
                if(stepDefClasses.size()>0) {
                    Optional<Class<?>> stepDefC = stepDefClasses.stream().filter(x->x.getName().contains(className)).findFirst();
                    if(stepDefC.isPresent()) {
                        stepDefClass = stepDefC.get();
                    }
                }
                if (stepDefClass == null) {
                    throw new ClassNotFoundException("Can not locad the class: " + className);
                }
                ClassReader classR = new ClassReader(getInternalName(stepDefClass));
                ClassNode classNode = new ClassNode();
                classR.accept(classNode, 0);
                for (MethodNode methodNode : classNode.methods) {
                    if (methodNode.name.equals(stepMethodName.split("\\(")[0])) {
                        stepDefs.put(step.getName(), methodNode);
                    }
                }
            }
        }
        return stepDefs;
    }
}
