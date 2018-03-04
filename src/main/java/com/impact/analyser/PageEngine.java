package com.impact.analyser;

import com.impact.analyser.report.MethodInfo;
import com.impact.analyser.report.PageInfo;
import com.impact.analyser.rules.ElementRules;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.tree.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class PageEngine {

    public List<PageInfo> getSeleniumFieldsFromPageMethod(ElementRules elementRules) throws NoSuchFieldException {
        List<PageInfo> pageReports = new ArrayList<>();
        if (elementRules.isElementsDefinedWithInPageClassOnly()) {
            Set<Class<?>> allPageClasses = ClassUtils.getAllTypesInPackages(elementRules.getPageClassPackages());
            pageReports.addAll(getSeleniumFields(elementRules.isElementsDefinedWithInPageClassOnly(), null, allPageClasses));
        } else {
            Set<Class<?>> allElementClasses = ClassUtils.getAllTypesInPackages(elementRules.getElementClassPackages());
            Map<String, List<String>> classAndFields = getSeleniumFieldsFromClasses(allElementClasses);
            Set<Class<?>> allPageClasses = ClassUtils.getAllTypesInPackages(elementRules.getPageClassPackages());
            pageReports.addAll(getSeleniumFields(elementRules.isElementsDefinedWithInPageClassOnly(), classAndFields, allPageClasses));
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
                                             Set<Class<?>> allPageClasses) throws NoSuchFieldException {
        if(elementsInPageClass) {
            return getPageReportsForElementsWithInPageClassesOnly(allPageClasses);
        } else {
            return getPageReportsForElementsInDifferentClass(classAndFields, allPageClasses);
        }
    }

    private List<PageInfo> getPageReportsForElementsInDifferentClass(Map<String, List<String>> classAndFields,
                                                                     Set<Class<?>> allPageClasses) {
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
                        if (abstractInsnNode.getType() == AbstractInsnNode.FIELD_INSN) {
                            FieldInsnNode fin = (FieldInsnNode) abstractInsnNode;
                            String className = fin.owner.replace("/",".");
                            Optional<Map.Entry<String, List<String>>> cb = classAndFields.entrySet().stream()
                                    .filter(x->x.getKey().equals(className))
                                    .findFirst();
                            if(cb.isPresent()) {
                                String classOb = cb.get().getKey();
                                Class<?> page = ClassUtils.getClass(classOb);
                                Field field = getClassField(page, fin.name);
                                if(field==null) {
                                    field = getClassField(page.getSuperclass(), fin.name);
                                }
                                if (field != null && isSeleniumField(field)) {
                                    fieldAndFieldClassName.put(fin.name, className);
                                }
                            }
                        }
                        if(abstractInsnNode.getType() == AbstractInsnNode.METHOD_INSN) {
                            ClassNode classNode = ClassUtils.getClassNode(pageClass);
                            MethodInsnNode insnNode = (MethodInsnNode)abstractInsnNode;
                            Optional<MethodNode> optional = classNode.methods.stream().filter(x->x.name.equals(insnNode.name)).findFirst();
                            if(optional.isPresent()) {
                                privateMethods.add(optional.get().name);
                            }
                        }
                    }
                    if(privateMethods.size() >0){
                        methodReport.setSameClassMethods(privateMethods);
                    }
                    boolean fieldAdd = false;
                    boolean privateMethodAdd = false;
                    if(fieldAndFieldClassName.size()>0) {
                        methodReport.setFieldAndFieldClassName(fieldAndFieldClassName);
                        fieldAdd = true;
                    }
                    for(MethodInfo mR: methodReports) {
                        if(privateMethods.contains(mR.getMethodName())){
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

    private List<PageInfo> getPageReportsForElementsWithInPageClassesOnly(Set<Class<?>> allPageClasses) {
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
                        if (abstractInsnNode.getType() == AbstractInsnNode.FIELD_INSN) {
                            FieldInsnNode fin = (FieldInsnNode) abstractInsnNode;
                            Class<?> page = pageClass;
                            Field field = getClassField(page, fin.name);
                            if(field == null) {
                                page = pageClass.getSuperclass();
                                field = getClassField(pageClass.getSuperclass(), fin.name);
                            }
                            if (field!=null && isSeleniumField(field)) {
                                fieldAndFieldClassName.put(fin.name, page.getName());
                            }
                        }
                        if(abstractInsnNode.getType() == AbstractInsnNode.METHOD_INSN) {
                            ClassNode classNode = ClassUtils.getClassNode(pageClass);
                            MethodInsnNode insnNode = (MethodInsnNode)abstractInsnNode;
                            Optional<MethodNode> optional = classNode.methods.stream().filter(x->x.name.equals(insnNode.name)).findFirst();
                            if(optional.isPresent()) {
                                privateMethods.add(optional.get().name);
                            }
                        }
                    }
                    if(privateMethods.size() >0){
                        methodReport.setSameClassMethods(privateMethods);
                    }
                    if(fieldAndFieldClassName.size()>0) {
                        methodReport.setFieldAndFieldClassName(fieldAndFieldClassName);
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
    /**
     * returns field object from the fieldName
     * @param pageClass
     * @param fieldName
     * @return
     */
    private Field getClassField(Class<?> pageClass, String fieldName) {
        Field field = null;
        try {
            field = pageClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
        }
        return field;
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
                    .map(x->x.getName()).collect(Collectors.toList());
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
        if(field.getType().isAssignableFrom(WebElement.class)) {
            return true;
        } else if(field.getType().isAssignableFrom(List.class)) {
            List<Annotation> annotations = Arrays.asList(field.getDeclaredAnnotations());
            if(annotations.stream().anyMatch(x->x.annotationType().isAssignableFrom(FindBy.class)
                    || x.annotationType().isAssignableFrom(FindAll.class)
                    || x.annotationType().isAssignableFrom(FindBys.class))) {
                return true;
            }
        } else if(field.getType().isAssignableFrom(By.class)) {
            return true;
        }
        return false;
    }
}
