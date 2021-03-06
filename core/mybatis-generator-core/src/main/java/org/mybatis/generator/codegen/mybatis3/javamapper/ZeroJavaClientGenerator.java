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
package org.mybatis.generator.codegen.mybatis3.javamapper;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.*;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.ZeroXMLMapperGenerator;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class ZeroJavaClientGenerator extends AbstractJavaClientGenerator {

    public ZeroJavaClientGenerator(String project) {
        this(project, true);
    }

    public ZeroJavaClientGenerator(String project, boolean requiresMatchedXMLGenerator) {
        super(project, requiresMatchedXMLGenerator);
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaMapperType());
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);
        interfaze.addAnnotation("@Mapper");
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));

        String rootInterface = introspectedTable
                .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                    .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                    rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }

//        addDelete(interfaze);
//        addDeleteByIdsMethod(interfaze);
//        addSavetMethod(interfaze);
//        addFindByIdMethod(interfaze);
//        addFindMethod(interfaze);
//        addUpdateMethod(interfaze);

        FullyQualifiedJavaType typeS = new FullyQualifiedJavaType(
                new FullyQualifiedJavaType(
                        introspectedTable.getMyBatis3JavaMapperType()).getPackageName() + ".IMapper"
        );
        interfaze.addImportedType(typeS);

        FullyQualifiedJavaType TType = new FullyQualifiedJavaType(introspectedTable.getBaseVoRecordType());
        typeS.addTypeArgument(TType);
        interfaze.addImportedType(TType);

        FullyQualifiedJavaType UType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        typeS.addTypeArgument(UType);
        interfaze.addImportedType(UType);

        FullyQualifiedJavaType IDType = introspectedTable.getPrimaryKeyJavaType();
        typeS.addTypeArgument(IDType);
        interfaze.addImportedType(IDType);

        interfaze.addSuperInterface(typeS);

        List<CompilationUnit> answer = new ArrayList<>();
        if (context.getPlugins().clientGenerated(interfaze, introspectedTable)) {
            answer.add(interfaze);
        }

        List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits();
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }

        return answer;
    }

    protected void addDelete(Interface interfaze) {
        if (introspectedTable.getRules().generateDeleteByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new ZeroDeleteByIdMethodGenerator(true);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addDeleteByIdsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateDeleteByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new ZeroDeleteByIdsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addSavetMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateInsert()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new ZeroSaveMethodGenerator(true);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addFindByIdMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateSelectByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new ZeroFindByIdMethodGenerator(true);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addFindMethod(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new ZeroFindMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }

    protected void addUpdateMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new ZeroUpdateWithoutBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
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

    public List<CompilationUnit> getExtraCompilationUnits() {
        return Collections.emptyList();
    }

    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator() {
        return new ZeroXMLMapperGenerator();
    }
}
