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

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.Iterator;
import java.util.List;

public class ZeroUpdateWithoutBLOBsElementGenerator extends
        AbstractXmlElementGenerator {

    private boolean isSimple;

    public ZeroUpdateWithoutBLOBsElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addElements(XmlElement parentElement) {
        if (introspectedTable.getBaseColumns().size() < 1) {
            return;
        }
        XmlElement answer = new XmlElement("update"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getUpdateStatementId())); //$NON-NLS-1$
        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                introspectedTable.getBaseVoRecordType()));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("update "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        // set up for first column
        sb.setLength(0);
        sb.append("<set>"); //$NON-NLS-1$
        answer.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        OutputUtilities.xmlIndent(sb, 1);
        Iterator<IntrospectedColumn> iter;
        iter = ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getBaseColumns()).iterator();

        while (iter.hasNext()) {
            IntrospectedColumn introspectedColumn = iter.next();

            sb.append("<if test=\"");
            sb.append(introspectedColumn.getJavaProperty(null));
            sb.append(" != null\">");

            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));

            sb.append(",</if>");

            answer.addElement(new TextElement(sb.toString()));

            // set up for the next column
            if (iter.hasNext()) {
                sb.setLength(0);
                OutputUtilities.xmlIndent(sb, 1);
            }
        }
        sb.setLength(0);
        sb.append("</set>"); //$NON-NLS-1$
        answer.addElement(new TextElement(sb.toString()));
        boolean and = false;
        List<IntrospectedColumn> columns;
        if (introspectedTable.getPrimaryKeyColumns().size() < 1) {
            columns = introspectedTable.getBaseColumns();
        } else {
            columns = introspectedTable.getPrimaryKeyColumns();
        }

        for (IntrospectedColumn introspectedColumn : columns) {
            sb.setLength(0);
            if (and) {
                sb.append("and "); //$NON-NLS-1$
            } else {
                sb.append("where "); //$NON-NLS-1$
                and = true;
            }

            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));
            answer.addElement(new TextElement(sb.toString()));
        }

        if (context.getPlugins()
                .sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
