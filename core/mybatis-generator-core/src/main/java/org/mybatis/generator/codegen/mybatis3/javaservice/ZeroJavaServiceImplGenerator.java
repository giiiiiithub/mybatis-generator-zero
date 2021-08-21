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
package org.mybatis.generator.codegen.mybatis3.javaservice;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class ZeroJavaServiceImplGenerator extends AbstractJavaGenerator {

    public ZeroJavaServiceImplGenerator(String project) {
        super(project);
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));

        List<CompilationUnit> answer = new ArrayList<>();
        TopLevelClass serviceImpl = getServiceImpl();
        answer.add(serviceImpl);
        return answer;
    }

    protected TopLevelClass getServiceImpl() {
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaServiceType());
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                serviceType.getPackageName() + ".impl."
                        + serviceType.getShortName() + "Impl");

        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addAnnotation("@Service");
        topLevelClass.addImportedType("org.springframework.stereotype.Service");
        topLevelClass.addAnnotation("@Transactional(rollbackFor = Exception.class)");
        topLevelClass.addImportedType("org.springframework.transaction.annotation.Transactional");

        commentGenerator.addJavaFileComment(topLevelClass);

        //添加实现的接口
        topLevelClass.addSuperInterface(serviceType);
        topLevelClass.addImportedType(serviceType);

        //添加继承的抽象Service   extends BaseService<CmdsVo, Cmds, Integer>
        FullyQualifiedJavaType baseServiceType = new FullyQualifiedJavaType(serviceType.getPackageName() + ".BaseService");

        FullyQualifiedJavaType TType = new FullyQualifiedJavaType(introspectedTable.getBaseVoRecordType());
        baseServiceType.addTypeArgument(TType);
        topLevelClass.addImportedType(TType);

        FullyQualifiedJavaType UType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        baseServiceType.addTypeArgument(UType);
        topLevelClass.addImportedType(UType);

        FullyQualifiedJavaType IDType = introspectedTable.getPrimaryKeyJavaType();
        baseServiceType.addTypeArgument(IDType);
        topLevelClass.addImportedType(IDType);

        topLevelClass.setSuperClass(baseServiceType);
        topLevelClass.addImportedType(baseServiceType);

        if (introspectedTable.getPrimaryKeyColumns().size() < 1) {
            forbidDeleteById(topLevelClass);
            forbidDeleteByIds(topLevelClass);
            forbidFindById(topLevelClass);
        }

        if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
            replaceDeleteByIdsByDeleteBatch(topLevelClass);
        }


        //添加mapper成员变量
//        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(
//                introspectedTable.getMyBatis3JavaMapperType());
//        Field mapperField = new Field(mapperType.getShortName().toLowerCase(), mapperType);
//        mapperField.addAnnotation("@Autowired");
//        topLevelClass.addImportedType(mapperType);
//        topLevelClass.addImportedType("org.springframework.beans.factory.annotation.Autowired;");
//        topLevelClass.addField(mapperField);

        //添加方法
//        addDeleteMethod(topLevelClass);
//        addDeleteByIdsMethod(topLevelClass);
//        addSavetMethod(topLevelClass);
//        addUpdateMethod(topLevelClass);
//        addFindByIdMethod(topLevelClass);
//        addFindMethod(topLevelClass);
        return topLevelClass;
    }

    private void forbidFindById(TopLevelClass topLevelClass) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        Method method = new Method(introspectedTable.getFindByIdStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(false);

        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        importedTypes.add(returnType);
        method.setReturnType(returnType);


        FullyQualifiedJavaType parameterType = introspectedTable.getPrimaryKeyJavaType();
        importedTypes.add(parameterType);

        String paramName = "id";
        method.addParameter(new Parameter(parameterType, paramName));

        StringBuilder methodBody = new StringBuilder();
        methodBody.append("throw ");
        methodBody.append("new ");
        methodBody.append("RuntimeException");
        methodBody.append("(");
        methodBody.append("\"无主键, 禁止操作\"");
        methodBody.append(");");
        method.addBodyLine(methodBody.toString());

        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }

    private void forbidDeleteById(TopLevelClass topLevelClass) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        Method method = new Method(introspectedTable.getDeleteByIdStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(false);

        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getBooleanPrimitiveInstance();
        importedTypes.add(returnType);
        method.setReturnType(returnType);


        FullyQualifiedJavaType parameterType = introspectedTable.getPrimaryKeyJavaType();
        importedTypes.add(parameterType);

        String paramName = "id";
        method.addParameter(new Parameter(parameterType, paramName));

        StringBuilder methodBody = new StringBuilder();
        methodBody.append("throw ");
        methodBody.append("new ");
        methodBody.append("RuntimeException");
        methodBody.append("(");
        methodBody.append("\"无主键, 禁止操作\"");
        methodBody.append(");");
        method.addBodyLine(methodBody.toString());

        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }

    private void forbidDeleteByIds(TopLevelClass topLevelClass) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        Method method = new Method(introspectedTable.getDeleteByIdsStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(false);

        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getIntInstance();
        importedTypes.add(returnType);
        method.setReturnType(returnType);

        //参数
        FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
        importedTypes.add(listType);

        FullyQualifiedJavaType parameterType = introspectedTable.getPrimaryKeyJavaType();
        importedTypes.add(parameterType);
        listType.addTypeArgument(parameterType);
        String paramName = "ids";
        method.addParameter(new Parameter(listType, paramName));

        StringBuilder methodBody = new StringBuilder();
        methodBody.append("throw ");
        methodBody.append("new ");
        methodBody.append("RuntimeException");
        methodBody.append("(");
        methodBody.append("\"无主键, 禁止操作\"");
        methodBody.append(");");
        method.addBodyLine(methodBody.toString());

        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }

    private void replaceDeleteByIdsByDeleteBatch(TopLevelClass topLevelClass) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        Method method = new Method(introspectedTable.getDeleteByIdsStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(false);
        method.addAnnotation("@Override");

        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getIntInstance();
        importedTypes.add(returnType);
        method.setReturnType(returnType);

        //参数
        FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
        importedTypes.add(listType);

        FullyQualifiedJavaType parameterType = introspectedTable.getPrimaryKeyJavaType();
        importedTypes.add(parameterType);
        listType.addTypeArgument(parameterType);
        String paramName = "ids";
        method.addParameter(new Parameter(listType, paramName));

        StringBuilder methodBody = new StringBuilder();
        methodBody.append("return super.deleteBatch(");
        methodBody.append(paramName);
        methodBody.append(");");
        method.addBodyLine(methodBody.toString());

        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }

    protected void addDeleteMethod(TopLevelClass topLevelClass) {
        Method method = new Method(introspectedTable.getDeleteStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(false);
        method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        FullyQualifiedJavaType parameterType = introspectedTable
                .getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType();
        importedTypes.add(parameterType);

        String paramName = introspectedTable
                .getPrimaryKeyColumns().get(0).getJavaProperty();
        method.addParameter(new Parameter(parameterType, paramName));


        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaMapperType());
        StringBuilder methodBody = new StringBuilder();
        methodBody.append("return ");
        methodBody.append(mapperType.getShortName().toLowerCase());
        methodBody.append(".");
        methodBody.append(introspectedTable.getDeleteStatementId());
        methodBody.append("(");
        methodBody.append(paramName);
        methodBody.append(");");
        method.addBodyLine(methodBody.toString());

        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }

    protected void addDeleteByIdsMethod(TopLevelClass topLevelClass) {
        Method method = new Method(introspectedTable.getDeleteByIdsStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(false);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
        importedTypes.add(listType);

        FullyQualifiedJavaType parameterType = introspectedTable
                .getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType();
        importedTypes.add(parameterType);
        listType.addTypeArgument(parameterType);
        String paramName = introspectedTable
                .getPrimaryKeyColumns().get(0).getJavaProperty() + "s";
        method.addParameter(new Parameter(listType, paramName));


        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaMapperType());
        StringBuilder methodBody = new StringBuilder();
        methodBody.append("return ");
        methodBody.append(mapperType.getShortName().toLowerCase());
        methodBody.append(".");
        methodBody.append(introspectedTable.getDeleteByIdsStatementId());
        methodBody.append("(");
        methodBody.append(paramName);
        methodBody.append(");");
        method.addBodyLine(methodBody.toString());

        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }


    protected void addSavetMethod(TopLevelClass topLevelClass) {
        Method method = new Method(introspectedTable.getSaveStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(false);
        method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(introspectedTable
                .getBaseVoRecordType());
        importedTypes.add(parameterType);

        String paramName = parameterType.getShortName();
        method.addParameter(new Parameter(parameterType, paramName));


        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaMapperType());
        StringBuilder methodBody = new StringBuilder();
        methodBody.append("return ");
        methodBody.append(mapperType.getShortName().toLowerCase());
        methodBody.append(".");
        methodBody.append(introspectedTable.getSaveStatementId());
        methodBody.append("(");
        methodBody.append(paramName);
        methodBody.append(");");
        method.addBodyLine(methodBody.toString());

        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }

    protected void addUpdateMethod(TopLevelClass topLevelClass) {
        Method method = new Method(introspectedTable.getUpdateStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(false);
        method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(introspectedTable
                .getBaseVoRecordType());
        importedTypes.add(parameterType);

        String paramName = parameterType.getShortName();
        method.addParameter(new Parameter(parameterType, paramName));


        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaMapperType());
        StringBuilder methodBody = new StringBuilder();
        methodBody.append("return ");
        methodBody.append(mapperType.getShortName().toLowerCase());
        methodBody.append(".");
        methodBody.append(introspectedTable.getUpdateStatementId());
        methodBody.append("(");
        methodBody.append(paramName);
        methodBody.append(");");
        method.addBodyLine(methodBody.toString());

        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }

    protected void addFindByIdMethod(TopLevelClass topLevelClass) {

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        Method method = new Method(introspectedTable.getFindByIdStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(false);

        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        importedTypes.add(returnType);
        method.setReturnType(returnType);


        FullyQualifiedJavaType parameterType = introspectedTable
                .getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType();
        importedTypes.add(parameterType);

        String paramName = introspectedTable
                .getPrimaryKeyColumns().get(0).getJavaProperty();
        method.addParameter(new Parameter(parameterType, paramName));


        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaMapperType());
        StringBuilder methodBody = new StringBuilder();
        methodBody.append("return ");
        methodBody.append(mapperType.getShortName().toLowerCase());
        methodBody.append(".");
        methodBody.append(introspectedTable.getFindByIdStatementId());
        methodBody.append("(");
        methodBody.append(paramName);
        methodBody.append(");");
        method.addBodyLine(methodBody.toString());

        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);

    }

    protected void addFindMethod(TopLevelClass topLevelClass) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        Method method = new Method(introspectedTable.getFindStatementId());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(false);

        FullyQualifiedJavaType typeArg = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        importedTypes.add(typeArg);

        FullyQualifiedJavaType typeS = FullyQualifiedJavaType.getNewListInstance();
        importedTypes.add(typeS);

        typeS.addTypeArgument(typeArg);
        method.setReturnType(typeS);

        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(introspectedTable
                .getBaseVoRecordType());
        importedTypes.add(parameterType);

        String paramName = parameterType.getShortName();
        method.addParameter(new Parameter(parameterType, paramName));


        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaMapperType());
        StringBuilder methodBody = new StringBuilder();
        methodBody.append("return ");
        methodBody.append(mapperType.getShortName().toLowerCase());
        methodBody.append(".");
        methodBody.append(introspectedTable.getFindStatementId());
        methodBody.append("(");
        methodBody.append(paramName);
        methodBody.append(");");
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
