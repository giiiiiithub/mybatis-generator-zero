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
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.codegen.AbstractJavaGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class ZeroJavaServiceGenerator extends AbstractJavaGenerator {

    public ZeroJavaServiceGenerator(String project) {
        super(project);
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));

        List<CompilationUnit> answer = new ArrayList<>();
        Interface serviceInterFace = getServiceInterFace();
        answer.add(serviceInterFace);
        return answer;
    }

    protected Interface getServiceInterFace() {
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaServiceType());
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);

        FullyQualifiedJavaType typeS = new FullyQualifiedJavaType(new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaServiceType()).getPackageName() + ".IService");
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

        return interfaze;
    }
}
