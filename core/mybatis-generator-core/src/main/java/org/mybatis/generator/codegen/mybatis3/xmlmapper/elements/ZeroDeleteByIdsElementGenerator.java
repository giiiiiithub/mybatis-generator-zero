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
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

public class ZeroDeleteByIdsElementGenerator extends
        AbstractXmlElementGenerator {

    private boolean isSimple;

    public ZeroDeleteByIdsElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("delete"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getDeleteByIdsStatementId())); //$NON-NLS-1$

        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                introspectedTable.getPrimaryKeyJavaType().getFullyQualifiedName()));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("delete from "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        sb.setLength(0);
        sb.append("where");
        answer.addElement(new TextElement(sb.toString()));

        String parameterName = introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty()+"s";

        sb.setLength(0);
        sb.append("<choose>");
        answer.addElement(new TextElement(sb.toString()));

        sb.setLength(0);
        OutputUtilities.javaIndent(sb, 1);
        sb.append("<when test=\"" + parameterName + " != null and " + parameterName + ".size()>0\">");
        answer.addElement(new TextElement(sb.toString()));

        sb.setLength(0);
        OutputUtilities.javaIndent(sb, 2);
        sb.append(MyBatis3FormattingUtilities
                .getEscapedColumnName(introspectedTable
                        .getPrimaryKeyColumns().get(0)));
        sb.append(" in");
        answer.addElement(new TextElement(sb.toString()));

        sb.setLength(0);
        OutputUtilities.javaIndent(sb, 2);
        sb.append("<foreach item=\"id\" index=\"index\" collection=\"" + parameterName + "\" open=\"(\" separator=\",\" close=\")\">");
        answer.addElement(new TextElement(sb.toString()));

        sb.setLength(0);
        OutputUtilities.javaIndent(sb, 2);
        sb.append(MyBatis3FormattingUtilities
                .getParameterClause(introspectedTable
                        .getPrimaryKeyColumns().get(0)));
        answer.addElement(new TextElement(sb.toString()));

        sb.setLength(0);
        OutputUtilities.javaIndent(sb, 2);
        sb.append("</foreach> ");
        answer.addElement(new TextElement(sb.toString()));

        sb.setLength(0);
        OutputUtilities.javaIndent(sb, 1);
        sb.append("</when> ");
        answer.addElement(new TextElement(sb.toString()));

        sb.setLength(0);
        OutputUtilities.javaIndent(sb, 1);
        sb.append("<otherwise>1 != 1</otherwise> ");
        answer.addElement(new TextElement(sb.toString()));

        sb.setLength(0);
        sb.append("</choose> ");
        answer.addElement(new TextElement(sb.toString()));

        if (context.getPlugins()
                .sqlMapDeleteByPrimaryKeyElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
