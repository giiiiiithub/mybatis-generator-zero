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
package org.mybatis.generator.codegen.mybatis3;

import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.javacontroller.ZeroJavaControllerImplGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.SimpleAnnotatedClientGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.SimpleJavaClientGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.ZeroJavaClientGenerator;
import org.mybatis.generator.codegen.mybatis3.javaservice.ZeroJavaServiceGenerator;
import org.mybatis.generator.codegen.mybatis3.javaservice.ZeroJavaServiceImplGenerator;
import org.mybatis.generator.codegen.mybatis3.model.ZeroModelVoGenerator;
import org.mybatis.generator.internal.ObjectFactory;

import java.util.List;

/**
 * Introspected table implementation for generating simple MyBatis3 artifacts (no "by example" methods,
 * flat model, etc.)
 *
 * @author Jeff Butler
 *
 */
public class ZeroIntrospectedTableMyBatis3Impl extends IntrospectedTableMyBatis3SimpleImpl {
    public ZeroIntrospectedTableMyBatis3Impl() {
        super();
    }

    @Override
    protected AbstractJavaClientGenerator createJavaClientGenerator() {
        if (context.getJavaClientGeneratorConfiguration() == null) {
            return null;
        }

        String type = context.getJavaClientGeneratorConfiguration()
                .getConfigurationType();

        AbstractJavaClientGenerator javaGenerator;
        if ("XMLMAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
            javaGenerator = new SimpleJavaClientGenerator(getClientProject());
        } else if ("ANNOTATEDMAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
            javaGenerator = new SimpleAnnotatedClientGenerator(getClientProject());
        } else if ("MAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
            javaGenerator = new SimpleJavaClientGenerator(getClientProject());
        }
        if ("ZEROXMLMAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
            javaGenerator = new ZeroJavaClientGenerator(getClientProject());
        } else {
            javaGenerator = (AbstractJavaClientGenerator) ObjectFactory
                    .createInternalObject(type);
        }

        return javaGenerator;
    }

    @Override
    public void calculateGenerators(List<String> warnings,
                                    ProgressCallback progressCallback) {
        calculateJavaModelGenerators(warnings, progressCallback);

        calculateJavaModelVoGenerators(warnings, progressCallback);

        calculateJavaServiceGenerators(warnings, progressCallback);

        calculateJavaServiceImplGenerators(warnings, progressCallback);

        calculateJavaControllerGenerators(warnings, progressCallback);

        AbstractJavaClientGenerator javaClientGenerator =
                calculateClientGenerators(warnings, progressCallback);

        calculateXmlMapperGenerator(javaClientGenerator, warnings, progressCallback);
    }

    protected void calculateJavaModelVoGenerators(List<String> warnings,
                                                  ProgressCallback progressCallback) {

        AbstractJavaGenerator javaGenerator = new ZeroModelVoGenerator(getModelVoProject());
        initializeAbstractGenerator(javaGenerator, warnings,
                progressCallback);
        javaGenerators.add(javaGenerator);
    }

    protected void calculateJavaServiceGenerators(List<String> warnings,
                                                  ProgressCallback progressCallback) {

        ZeroJavaServiceGenerator javaServiceGenerator = new ZeroJavaServiceGenerator(getServiceProject());
        initializeAbstractGenerator(javaServiceGenerator, warnings,
                progressCallback);
        javaGenerators.add(javaServiceGenerator);
    }

    protected void calculateJavaServiceImplGenerators(List<String> warnings,
                                                      ProgressCallback progressCallback) {

        ZeroJavaServiceImplGenerator javaServiceGenerator = new ZeroJavaServiceImplGenerator(getServiceImplProject());
        initializeAbstractGenerator(javaServiceGenerator, warnings,
                progressCallback);
        javaGenerators.add(javaServiceGenerator);
    }

    protected void calculateJavaControllerGenerators(List<String> warnings,
                                                     ProgressCallback progressCallback) {

        ZeroJavaControllerImplGenerator controllerImplGenerator =
                new ZeroJavaControllerImplGenerator(getControllerProject(), getControllerPath());
        initializeAbstractGenerator(controllerImplGenerator, warnings,
                progressCallback);
        javaGenerators.add(controllerImplGenerator);
    }

    protected String getModelVoProject() {
        return context.getZeroJavaModelVoGeneratorConfiguration().getTargetProject();
    }

    protected String getServiceProject() {
        return context.getJavaServiceGeneratorConfiguration().getTargetProject();
    }

    protected String getServiceImplProject() {
        return context.getJavaServiceGeneratorConfiguration().getTargetProject();
    }

    protected String getControllerProject() {
        return context.getJavaControllerGeneratorConfiguration().getTargetProject();
    }

    protected String getControllerPath() {
        return context.getJavaControllerGeneratorConfiguration().getControllerPath();
    }
}
