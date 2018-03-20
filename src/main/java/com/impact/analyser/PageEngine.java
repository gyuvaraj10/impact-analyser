package com.impact.analyser;

import com.impact.analyser.report.MethodInfo;
import com.impact.analyser.report.PageInfo;
import com.impact.analyser.rules.ElementRules;
import com.impact.analyser.rules.PageRules;
import com.impact.analyser.utils.ClassUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.tree.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class PageEngine {

    public List<PageInfo> getSeleniumFieldsFromPageMethod(ElementRules elementRules, PageRules pageRules) throws NoSuchFieldException, ClassNotFoundException {
        List<PageInfo> pageReports = new ArrayList<>();
        if (elementRules.isElementsDefinedWithInPageClassOnly()) {
            Set<Class<?>> allPageClasses = ClassUtils.getAllPageTypesInPackages(elementRules.getPageClassPackages(), pageRules.getBasePageClass());
            pageReports.addAll(getSeleniumFields(elementRules.isElementsDefinedWithInPageClassOnly(), null, allPageClasses, elementRules));
        } else {
            Set<Class<?>> allElementClasses = ClassUtils.getAllPageTypesInPackages(elementRules.getElementClassPackages(), pageRules.getBasePageClass());
            Map<String, List<String>> classAndFields = getSeleniumFieldsFromClasses(allElementClasses);
            Set<Class<?>> allPageClasses = ClassUtils.getAllPageTypesInPackages(elementRules.getPageClassPackages(), pageRules.getBasePageClass());
            pageReports.addAll(getSeleniumFields(elementRules.isElementsDefinedWithInPageClassOnly(), classAndFields, allPageClasses, elementRules));
        }
        return pageReports;
    }

    /**
     *
     * @param elementsInPageClass
     * @param classAndFields
     * @param allPageClasses
     * @return {pageMethodName: {fieldName: elementClassName}}
     * @throws NoSuchFieldException
     */
    private List<PageInfo> getSeleniumFields(boolean elementsInPageClass,
                                             Map<String, List<String>> classAndFields,
                                             Set<Class<?>> allPageClasses, ElementRules elementRules) throws NoSuchFieldException {
        if(elementsInPageClass) {
            return getPageReportsForElementsWithInPageClassesOnly(allPageClasses, elementRules);
        } else {
            return getPageReportsForElementsInDifferentClass(classAndFields, allPageClasses, elementRules);
        }
    }

    private List<PageInfo> getPageReportsForElementsInDifferentClass(Map<String, List<String>> classAndFields,
                                     Set<Class<?>> allPageClasses, ElementRules elementRules) {
        List<PageInfo> pageReports = new ArrayList<>();
        for(Class<?> pageClass: allPageClasses) {
            PageInfo pageReport = new PageInfo();
            pageReport.setPageName(pageClass.getName());
            Set<MethodInfo> methodReports = new HashSet<>();
            ClassNode pageClassNode = ClassUtils.getClassNode(pageClass);
            List<MethodNode> pageMethods = pageClassNode.methods;
            for(MethodNode methodNode: pageMethods) {
                String methodName = methodNode.name;
                if (!methodName.equals("<init>")) {
                    MethodInfo methodReport = new MethodInfo();
                    methodReport.setMethodName(methodName);
                    Map<String, String> fieldAndFieldClassName = new HashMap<>();
                    List<String> privateMethods = new ArrayList<>();
                    for(AbstractInsnNode abstractInsnNode: methodNode.instructions.toArray()) {
                        if(elementRules.isElementsDefinedWithInPageMethodAlso()) {
                            for (LocalVariableNode localVariableNode : methodNode.localVariables) {
                                String fieldName = localVariableNode.name;
                                if(!fieldName.equals("this") && localVariableNode.desc.startsWith("L")) {
                                    if(isSeleniumField(localVariableNode.desc.substring(1, localVariableNode.desc.length()-1).replace("/","."))) {
                                        fieldAndFieldClassName.put(fieldName, pageClass.getName());
                                    }
                                }
                            }
                        }
                        if (abstractInsnNode.getType() == AbstractInsnNode.FIELD_INSN) {
                            FieldInsnNode fin = (FieldInsnNode) abstractInsnNode;
                            String className = fin.owner.replace("/",".");
                            Optional<Map.Entry<String, List<String>>> cb = classAndFields.entrySet().stream()
                                    .filter(x->x.getKey().equals(className))
                                    .findFirst();
                            if(cb.isPresent()) {
                                String classOb = cb.get().getKey();
                                Class<?> page = ClassUtils.getClass(classOb);
                                Field field = null;
                                if(page != null) {
                                    field = ClassUtils.getClassField(page, fin.name);
                                }
                                if(field==null) {
                                    field = ClassUtils.getClassField(page.getSuperclass(), fin.name);
                                }
                                if (field != null && isSeleniumField(field)) {
                                    fieldAndFieldClassName.put(fin.name, className);
                                }
                            }
                            if(elementRules.isElementsDefinedWithInPageMethodAlso()) {
                                Class<?> ownerClass = ClassUtils.getClass(className);
                                Field methodField = ClassUtils.getClassField(ownerClass, fin.name);
                                if(methodField!= null && isSeleniumField(methodField)) {
                                    fieldAndFieldClassName.put(fin.name, className);
                                }
                            }
                        }
                        if(abstractInsnNode.getType() == AbstractInsnNode.METHOD_INSN) {
                            ClassNode classNode = ClassUtils.getClassNode(pageClass);
                            MethodInsnNode insnNode = (MethodInsnNode)abstractInsnNode;
                            if(!insnNode.name.equals("<init>")) {
                                Optional<MethodNode> optional = classNode.methods.stream().filter(x -> x.name.equals(insnNode.name)).findFirst();
                                if (optional.isPresent()) {
                                    privateMethods.add(optional.get().name);
                                }
                            }
                        }
                    }
                    if(privateMethods.size() >0){
//                        methodReport.setSameClassMethods(privateMethods);
                    }
                    boolean fieldAdd = false;
                    boolean privateMethodAdd = false;
                    if(fieldAndFieldClassName.size()>0) {
                        methodReport.setFieldAndFieldClassName(fieldAndFieldClassName);
                        fieldAdd = true;
                    }
                    for(MethodInfo mR: methodReports) {
                        if (privateMethods.contains(mR.getMethodName())) {
                            privateMethodAdd = true;
                            break;
                        }
                    }
                    if(fieldAdd|| privateMethodAdd) {
                        methodReports.add(methodReport);
                    }
                }
            }
            if(methodReports.size()>0) {
                pageReport.setMethodReportList(methodReports);
                pageReports.add(pageReport);
            }
        }
        return pageReports;
    }

    private List<PageInfo> getPageReportsForElementsWithInPageClassesOnly(Set<Class<?>> allPageClasses,
                                            ElementRules elementRules) {
        List<PageInfo> pageReports = new ArrayList<>();
        for (Class<?> pageClass : allPageClasses) {
            PageInfo pageReport = new PageInfo();
            pageReport.setPageName(pageClass.getName());
            Set<MethodInfo> methodReports = new HashSet<>();
            ClassNode pageClassNode = ClassUtils.getClassNode(pageClass);
            List<MethodNode> pageMethods = pageClassNode.methods;
            for (MethodNode methodNode : pageMethods) {
                String methodName = methodNode.name;
                if (!methodName.equals("<init>")) {
                    MethodInfo methodReport = new MethodInfo();
                    methodReport.setMethodName(methodName);
                    Map<String, String> fieldAndFieldClassName = new HashMap<>();
                    List<String> privateMethods = new ArrayList<>();
                    for (AbstractInsnNode abstractInsnNode : methodNode.instructions.toArray()) {
                        if(elementRules.isElementsDefinedWithInPageMethodAlso()) {
                            for (LocalVariableNode localVariableNode : methodNode.localVariables) {
                                String fieldName = localVariableNode.name;
                                if(!fieldName.equals("this") && localVariableNode.desc.startsWith("L")) {
                                    if(isSeleniumField(localVariableNode.desc.substring(1, localVariableNode.desc.length()-1).replace("/","."))) {
                                        fieldAndFieldClassName.put(fieldName, pageClass.getName());
                                    }
                                }
                            }
                        }
                        if (abstractInsnNode.getType() == AbstractInsnNode.FIELD_INSN) {
                            FieldInsnNode fin = (FieldInsnNode) abstractInsnNode;
                            Class<?> page = pageClass;
                            Field field = ClassUtils.getClassField(page, fin.name);
                            if (field == null) {
                                page = pageClass.getSuperclass();
                                field = ClassUtils.getClassField(pageClass.getSuperclass(), fin.name);
                            }
                            if (field != null && isSeleniumField(field)) {
                                fieldAndFieldClassName.put(fin.name, page.getName());
                            }
                        }
                        if (abstractInsnNode.getType() == AbstractInsnNode.METHOD_INSN) {
                            ClassNode classNode = ClassUtils.getClassNode(pageClass);
                            MethodInsnNode insnNode = (MethodInsnNode) abstractInsnNode;
                            if(!insnNode.name.equals("<init>")) {
                                Optional<MethodNode> optional = classNode.methods.stream().filter(x -> x.name.equals(insnNode.name)).findFirst();
                                if (optional.isPresent()) {
                                    privateMethods.add(optional.get().name);
                                }
                            }
                        }
                    }
                    if (privateMethods.size() > 0) {
//                        methodReport.setSameClassMethods(privateMethods);
                    }
                    if (fieldAndFieldClassName.size() > 0) {
                        methodReport.setFieldAndFieldClassName(fieldAndFieldClassName);
                        methodReports.add(methodReport);
                    }
                }
            }
            if (methodReports.size() > 0) {
                pageReport.setMethodReportList(methodReports);
                pageReports.add(pageReport);
            }
        }
        return pageReports;
    }

    /**
     * returns a map of class name and selneium fields
     * @param classSet
     * @return
     */
    private Map<String, List<String>> getSeleniumFieldsFromClasses(Set<Class<?>> classSet) {
        Map<String, List<String>> map = new HashMap<>();
        Iterator<Class<?>> iterator = classSet.iterator();
        while (iterator.hasNext()) {
            Class<?> elementClass = iterator.next();
            Field[] allFields = ArrayUtils.addAll(elementClass.getDeclaredFields(), elementClass.getSuperclass().getDeclaredFields());
            List<String> seleniumFields = Arrays.stream(allFields).filter(this::isSeleniumField)
                    .map(Field::getName).collect(Collectors.toList());
            if(seleniumFields.size()>0) {
                map.put(elementClass.getName(), seleniumFields);
            }
        }
        return map;
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
        if(field.getType().isAssignableFrom(webElementClass)) {
            return true;
        } else if(field.getType().isAssignableFrom(List.class)) {
            List<Annotation> annotations = Arrays.asList(field.getDeclaredAnnotations());
            if(annotations.stream().anyMatch(x->x.annotationType().isAssignableFrom(findByClass)
                    || x.annotationType().isAssignableFrom(findAllClass)
                    || x.annotationType().isAssignableFrom(findBysClass))) {
                return true;
            }
            String byList = "java.util.List<org.openqa.selenium.By>";
            String genericType = field.getGenericType().getTypeName();
            if(genericType.equals(byList)) {
                return true;
            }
        } else if(field.getType().isAssignableFrom(ByClass)) {
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
