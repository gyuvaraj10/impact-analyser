package com.impact.analyser.cucumber;


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

    public Map<String, MethodNode> getCucumberStepDefinitions(CucumberResultReport dryRunResultReport, String[] glues) throws ClassNotFoundException, IOException {
        Map<String, MethodNode> stepDefs = new HashMap<>();
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        for(CucumberElement element: dryRunResultReport.getElements()) {
            for(CucumberStep step: element.getSteps()) {
                String location = step.getStepMatch().getLocation();
                if(!location.equals("undefined")) {
                    String className = location.split("\\.")[0];
                    String stepMethodName = location.split("\\.")[1];
                    Class<?> stepDefClass = null;
                    for (String glue : glues) {
                        try {
                            stepDefClass = loader.loadClass(glue + "." + className);
                            break;
                        } catch (ClassNotFoundException ex) {
                        }
                    }
                    if (stepDefClass == null) {
                        throw new ClassNotFoundException("Can not locad the class: " + className);
                    }
                    ClassReader classR = new ClassReader(getInternalName(stepDefClass));
                    ClassNode classNode = new ClassNode();
                    classR.accept(classNode, 0);
                    for (MethodNode methodNode : classNode.methods) {
                        if (methodNode.name.equals(stepMethodName.replace("(", "").replace(")", ""))) {
                            stepDefs.put(step.getName(), methodNode);
                        }
                    }
                }
            }
        }
        return stepDefs;
    }
}
