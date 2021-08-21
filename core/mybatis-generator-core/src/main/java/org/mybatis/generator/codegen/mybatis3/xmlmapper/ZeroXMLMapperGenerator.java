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
package org.mybatis.generator.codegen.mybatis3.xmlmapper;

import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.*;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class ZeroXMLMapperGenerator extends AbstractXmlGenerator {

    public ZeroXMLMapperGenerator() {
        super();
    }

    protected XmlElement getSqlMapElement() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString("Progress.12", table.toString())); //$NON-NLS-1$
        XmlElement answer = new XmlElement("mapper"); //$NON-NLS-1$
        String namespace = introspectedTable.getMyBatis3SqlMapNamespace();
        answer.addAttribute(new Attribute("namespace", //$NON-NLS-1$
                namespace));

        context.getCommentGenerator().addRootComment(answer);

        int pkSize = introspectedTable.getPrimaryKeyColumns().size();

        addResultMapElement(answer);
        addSelectSqlElement(answer);
        if (pkSize > 0) {
            addDeleteByIdElement(answer);
        }

        if (pkSize == 1) {
            addDeleteByIdsElement(answer);
        }

        addDeleteElement(answer);
        addInsertElement(answer);
        addUpdateElement(answer);
        if (pkSize > 0) {
            addFindByIdElement(answer);
        }
        addSelectAllElement(answer);
        addCountElement(answer);
        addWhereElement(answer);
        return answer;
    }

    protected void addResultMapElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new ZeroResultMapWithoutBLOBsElementGenerator(
                true);
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addSelectSqlElement(XmlElement parentElement) {
        ZeroSelectSqlElementGenerator elementGenerator = new ZeroSelectSqlElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }


    protected void addFindByIdElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new ZeroFindByIdElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addSelectAllElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new ZeroFindElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addDeleteElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new ZeroDeleteElementGenerator(true);
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addDeleteByIdElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new ZeroDeleteByIdElementGenerator(true);
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addDeleteByIdsElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new ZeroDeleteByIdsElementGenerator(true);
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addInsertElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new ZeroSaveElementGenerator(true);
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addUpdateElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new ZeroUpdateWithoutBLOBsElementGenerator(
                true);
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addCountElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new ZeroCountElementGenerator(
                true);
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addWhereElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new ZeroBaseWhereSqlElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void initializeAndExecuteGenerator(
            AbstractXmlElementGenerator elementGenerator,
            XmlElement parentElement) {
        elementGenerator.setContext(context);
        elementGenerator.setIntrospectedTable(introspectedTable);
        elementGenerator.setProgressCallback(progressCallback);
        elementGenerator.setWarnings(warnings);
        elementGenerator.addElements(parentElement);
    }

    @Override
    public Document getDocument() {
        Document document = new Document(
                XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
                XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        document.setRootElement(getSqlMapElement());

        if (!context.getPlugins().sqlMapDocumentGenerated(document,
                introspectedTable)) {
            document = null;
        }

        return document;
    }
}
