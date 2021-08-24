/**
 *    Copyright 2006-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.javacontroller;

import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.logging.Log;
import org.mybatis.generator.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class ZeroJavaControllerImplGenerator extends AbstractJavaGenerator {

    private Log logger = LogFactory.getLog(getClass());

    private String controllerPath;

    public ZeroJavaControllerImplGenerator(String project, String controllerPath) {
        super(project);
        this.controllerPath = controllerPath;
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));

        List<CompilationUnit> answer = new ArrayList<>();
        TopLevelClass controller = getController();
        answer.add(controller);
        return answer;
    }

    protected TopLevelClass getController() {

        FullyQualifiedJavaType domainType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        FullyQualifiedJavaType controllerType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaControllerType());

        TopLevelClass topLevelClass = new TopLevelClass(controllerType);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addAnnotation("@RestController");
        topLevelClass.addAnnotation("@RequestMapping(\"/"
                + controllerPath
                + "\")");

        //添加service成员
        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaServiceType());

        Field field = new Field(serviceType.getShortName().substring(0, 1).toLowerCase() +
                serviceType.getShortName().substring(1), serviceType);
        field.addAnnotation("@Autowired");
        topLevelClass.addField(field);

        topLevelClass.addImportedType("org.springframework.web.bind.annotation.*");
        topLevelClass.addImportedType("org.springframework.util.ObjectUtils");
        topLevelClass.addImportedType("org.springframework.http.MediaType");
        topLevelClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");

        topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseVoRecordType()));

        topLevelClass.addImportedType(new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaControllerType())
                .getPackageName().replace("controller", "common") + ".Resp");

        topLevelClass.addImportedType(new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaControllerType())
                .getPackageName().replace("controller", "errorhandler") + ".ErrorCode");

        int pkSize = introspectedTable.getPrimaryKeyColumns().size();
        //添加方法
        addSaveMethod(topLevelClass);
        addUpdateMethod(topLevelClass);
        if (pkSize > 0) {
            addFindByIdMethod(topLevelClass);
        }
        addFindMethod(topLevelClass);
        if (pkSize > 0) {
            addDeleteByIdMethod(topLevelClass);
        }
        addDeleteMethod(topLevelClass);
        return topLevelClass;
    }

    protected void addDeleteMethod(TopLevelClass topLevelClass) {
        Method method = new Method(introspectedTable.getDeleteStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("Resp"));

        //方法注解
        method.addAnnotation("@DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)");

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        //方法参数
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseVoRecordType());
        importedTypes.add(parameterType);


        FullyQualifiedJavaType voType = new FullyQualifiedJavaType(introspectedTable.getBaseVoRecordType());
        String voTypeName = voType.getShortName();
        String paramName = voTypeName.substring(0, 1).toLowerCase()
                + voTypeName.substring(1);
        Parameter parameter = new Parameter(parameterType, paramName);
        parameter.addAnnotation("@RequestBody");
        method.addParameter(parameter);
        importedTypes.add(voType);

        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaServiceType());
        importedTypes.add(serviceType);
        String serviceName = serviceType.getShortName().substring(0, 1).toLowerCase()
                + serviceType.getShortName().substring(1);

        StringBuilder methodBody = new StringBuilder();
        methodBody.append("return Resp.ok(" + serviceName + "." + introspectedTable.getDeleteStatementId() + "(" + paramName + "));");
        method.addBodyLine(methodBody.toString());
        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }


    protected void addDeleteByIdMethod(TopLevelClass topLevelClass) {
        if (introspectedTable.getPrimaryKeyColumns().size() != 1) {
            return;
        }
        Method method = new Method(introspectedTable.getDeleteByIdStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("Resp"));
        method.addAnnotation("@DeleteMapping(value = \"/{" + introspectedTable
                .getPrimaryKeyColumns().get(0).getJavaProperty() + "s" + "}\")");

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        FullyQualifiedJavaType idType = introspectedTable.getPrimaryKeyJavaType();
        importedTypes.add(idType);

        FullyQualifiedJavaType parameterType = FullyQualifiedJavaType.getNewListInstance();
        parameterType.addTypeArgument(idType);
        importedTypes.add(parameterType);


        String paramName = introspectedTable.getPrimaryKeyColumns().size() > 1 ? "ids" :
                introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty() + "s";

        Parameter parameter = new Parameter(parameterType, paramName);
        parameter.addAnnotation("@PathVariable(value = \"" + introspectedTable
                .getPrimaryKeyColumns().get(0).getJavaProperty() + "s" + "\")");
        method.addParameter(parameter);

        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaServiceType());
        importedTypes.add(serviceType);
        String serviceName = serviceType.getShortName().substring(0, 1).toLowerCase()
                + serviceType.getShortName().substring(1);

        StringBuilder methodBody = new StringBuilder();
        methodBody.append("if (ObjectUtils.isEmpty(ids)) {");
        method.addBodyLine(methodBody.toString());

        methodBody.setLength(0);
        methodBody.append("return Resp.error(ErrorCode.VALIDATE_ERROR,\"id不能为空\");");
        method.addBodyLine(methodBody.toString());

        methodBody.setLength(0);
        methodBody.append("}");
        method.addBodyLine(methodBody.toString());

        methodBody.setLength(0);
        methodBody.append("if (" + introspectedTable
                .getPrimaryKeyColumns().get(0).getJavaProperty() + "s" + ".size() == 1) {");
        method.addBodyLine(methodBody.toString());

        methodBody.setLength(0);
        methodBody.append("return Resp.ok(" + serviceName + "." + introspectedTable.getDeleteByIdStatementId() + "(" + introspectedTable
                .getPrimaryKeyColumns().get(0).getJavaProperty() + "s.get(0)" + "));");
        method.addBodyLine(methodBody.toString());

        methodBody.setLength(0);
        methodBody.append("} else {");
        method.addBodyLine(methodBody.toString());


        methodBody.setLength(0);
        methodBody.append("return Resp.ok(" + serviceName + "." + introspectedTable.getDeleteByIdsStatementId() + "(" + paramName + "));");
        method.addBodyLine(methodBody.toString());

        methodBody.setLength(0);
        methodBody.append("}");
        method.addBodyLine(methodBody.toString());

        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }


    protected void addSaveMethod(TopLevelClass topLevelClass) {
        Method method = new Method(introspectedTable.getSaveStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("Resp"));

        //方法注解
        method.addAnnotation("@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)");

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        //方法参数
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseVoRecordType());
        importedTypes.add(parameterType);


        FullyQualifiedJavaType voType = new FullyQualifiedJavaType(introspectedTable.getBaseVoRecordType());
        String voTypeName = voType.getShortName();
        String paramName = voTypeName.substring(0, 1).toLowerCase()
                + voTypeName.substring(1);
        Parameter parameter = new Parameter(parameterType, paramName);
        parameter.addAnnotation("@RequestBody");
        method.addParameter(parameter);
        importedTypes.add(voType);

        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaServiceType());
        importedTypes.add(serviceType);
        String serviceName = serviceType.getShortName().substring(0, 1).toLowerCase()
                + serviceType.getShortName().substring(1);

        StringBuilder methodBody = new StringBuilder();
        methodBody.append("return Resp.ok(" + serviceName + "." + introspectedTable.getSaveStatementId() + "(" + paramName + "));");
        method.addBodyLine(methodBody.toString());
        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }

    protected void addUpdateMethod(TopLevelClass topLevelClass) {
        if (introspectedTable.getBaseColumns().size() < 1) {
            return;
        }
        Method method = new Method(introspectedTable.getUpdateStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("Resp"));

        //方法注解
        method.addAnnotation("@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)");

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        //方法参数
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseVoRecordType());
        importedTypes.add(parameterType);


        FullyQualifiedJavaType voType = new FullyQualifiedJavaType(introspectedTable.getBaseVoRecordType());
        String voTypeName = voType.getShortName();
        String paramName = voTypeName.substring(0, 1).toLowerCase()
                + voTypeName.substring(1);
        Parameter parameter = new Parameter(parameterType, paramName);
        method.addParameter(parameter);
        importedTypes.add(voType);

        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaServiceType());
        importedTypes.add(serviceType);
        String serviceName = serviceType.getShortName().substring(0, 1).toLowerCase()
                + serviceType.getShortName().substring(1);

        StringBuilder methodBody = new StringBuilder();
        methodBody.append("return Resp.ok(" + serviceName + "." + introspectedTable.getUpdateStatementId() + "(" + paramName + "));");
        method.addBodyLine(methodBody.toString());
        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }

    protected void addFindByIdMethod(TopLevelClass topLevelClass) {
        int pkSize = introspectedTable.getPrimaryKeyColumns().size();
        if (introspectedTable.getPrimaryKeyColumns().size() < 1) {
            logger.warn("表" + introspectedTable.getFullyQualifiedTable() + "有" + pkSize + "个主键！" +
                    "不是1个主键，不生成findById方法。多个主键请直接使用生成的find方法");
            return;
        }

        Method method = new Method(introspectedTable.getFindByIdStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("Resp"));

        String idName = "id";
        //方法注解
        if (pkSize == 1) {
            method.addAnnotation("@GetMapping({\"/{" + idName + "}\"})");
        } else {
            method.addAnnotation("@GetMapping");
        }

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        //方法参数
        FullyQualifiedJavaType idType = introspectedTable.getPrimaryKeyJavaType();
        importedTypes.add(idType);
        Parameter parameter = new Parameter(idType, idName);
        if (pkSize == 1) {
            parameter.addAnnotation("@PathVariable(value = \"" + idName + "\")");
        }

        method.addParameter(parameter);

        //方法体
        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaServiceType());
        importedTypes.add(serviceType);
        String serviceName = serviceType.getShortName().substring(0, 1).toLowerCase()
                + serviceType.getShortName().substring(1);

        StringBuilder methodBody = new StringBuilder();
        methodBody.append("return Resp.ok(" + serviceName + "." + introspectedTable.getFindByIdStatementId() + "(" + idName + "));");
        method.addBodyLine(methodBody.toString());
        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }

    protected void addFindMethod(TopLevelClass topLevelClass) {
        Method method = new Method(introspectedTable.getFindStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("Resp"));

        //方法注解
        method.addAnnotation("@PostMapping(value = \"/queries\", consumes = MediaType.APPLICATION_JSON_VALUE)");

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        //方法参数
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseVoRecordType());
        importedTypes.add(parameterType);


        FullyQualifiedJavaType voType = new FullyQualifiedJavaType(introspectedTable.getBaseVoRecordType());
        String voTypeName = voType.getShortName();
        String paramName = voTypeName.substring(0, 1).toLowerCase()
                + voTypeName.substring(1);
        Parameter parameter = new Parameter(parameterType, paramName);
        parameter.addAnnotation("@RequestBody");
        method.addParameter(parameter);
        importedTypes.add(voType);

        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaServiceType());
        importedTypes.add(serviceType);
        String serviceName = serviceType.getShortName().substring(0, 1).toLowerCase()
                + serviceType.getShortName().substring(1);

        StringBuilder methodBody = new StringBuilder();
        methodBody.append("return Resp.ok(" + serviceName + "." + introspectedTable.getFindStatementId() + "(" + paramName + "));");
        method.addBodyLine(methodBody.toString());
        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);

    }


    protected void initializeAndExecuteGenerator(
            AbstractJavaMapperMethodGenerator methodGenerator,
            Interface interfaze) {
        methodGenerator.setContext(context);
        methodGenerator.setIntrospectedTable(introspectedTable);
        methodGenerator.setProgressCallback(progressCallback);
        methodGenerator.setWarnings(warnings);
        methodGenerator.addInterfaceElements(interfaze);
    }

}
