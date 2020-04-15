/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.developer.lsp.debug.runtime.translators;

/**
 * Translator just pass the String arguments as it is.
 */
public class StringPassTranslator implements VariableTranslator {

    private StringPassTranslator() {

    }

    private static class StringPassTranslatorHolder {

        private static final StringPassTranslator INSTANCE = new StringPassTranslator();
    }

    /**
     * This static method allow to get the instance of the StringPassTranslator.
     *
     * @return Pass the same string.
     */
    public static StringPassTranslator getInstance() {

        return StringPassTranslatorHolder.INSTANCE;
    }

    @Override
    public Object translate(Object object, int variablesReference) {

        return object;
    }
}
