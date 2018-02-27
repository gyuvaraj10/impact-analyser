package com.impact.analyser;

import org.objectweb.asm.tree.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class PageEngine {


    public Map<String, List<String>> getSeleniumFieldsFromEachMethod(Class<?> pageClass) throws NoSuchFieldException {
        Map<String, List<String>> methodItems = new HashMap<>();
        ClassNode classNode = ClassUtils.getClassNode(pageClass);
        for(MethodNode methodNode: classNode.methods) {
            methodItems.putAll(getSeleniumFields(classNode, methodNode, pageClass));
        }
        return methodItems;
    }

    private Map<String, List<String>> getSeleniumFields(ClassNode classNode, MethodNode methodNode, Class<?> pageClass) throws NoSuchFieldException {
        Map<String, List<String>> methodItems = new HashMap<>();
        String methodName = methodNode.name;
        List<String> methodFields = new ArrayList<>();
        if (!methodName.equals("<init>")) {
            for (AbstractInsnNode abstractInsnNode : methodNode.instructions.toArray()) {
                if (abstractInsnNode.getType() == AbstractInsnNode.FIELD_INSN) {
                    FieldInsnNode fin = (FieldInsnNode) abstractInsnNode;
                    if (isSeleniumField(pageClass.getDeclaredField(fin.name))) {
                        methodFields.add(fin.name);
                    }
                }
                if(abstractInsnNode.getType() == AbstractInsnNode.METHOD_INSN) {
                    MethodInsnNode insnNode = (MethodInsnNode)abstractInsnNode;
                    Optional<MethodNode> optional = classNode.methods.stream().filter(x->x.name.equals(insnNode.name)).findFirst();
                    if(optional.isPresent()) {
                        methodItems.putAll(getSeleniumFields(classNode, optional.get(), pageClass));
                    }
                }
            }
            if(methodFields.size() >0 ) {
                methodItems.put(methodName, methodFields);
            }
        }
        return methodItems;
    }

    public List<String> getSeleniumFieldsFromPage(Class<?> pageClass) {
        List<Field> declaredFields = Arrays.asList(pageClass.getDeclaredFields());
        List<String> pageFields = new ArrayList<>();
        for(Field field: declaredFields) {
            if(field.getType().isAssignableFrom(WebElement.class)) {
                pageFields.add(field.getName());
            } else if(field.getType().isAssignableFrom(List.class)) {
                List<Annotation> annotations = Arrays.asList(field.getDeclaredAnnotations());
                if(annotations.stream().anyMatch(x->x.annotationType().isAssignableFrom(FindBy.class)
                        || x.annotationType().isAssignableFrom(FindAll.class)
                        || x.annotationType().isAssignableFrom(FindBys.class))) {
                    pageFields.add(field.getName());
                }
            }
        }
        return pageFields;
    }

    private boolean isSeleniumField(Field field) {
        if(field.getType().isAssignableFrom(WebElement.class)) {
            return true;
        } else if(field.getType().isAssignableFrom(List.class)) {
            List<Annotation> annotations = Arrays.asList(field.getDeclaredAnnotations());
            if(annotations.stream().anyMatch(x->x.annotationType().isAssignableFrom(FindBy.class)
                    || x.annotationType().isAssignableFrom(FindAll.class)
                    || x.annotationType().isAssignableFrom(FindBys.class))) {
                return true;
            }
        }
        return false;
    }
}
