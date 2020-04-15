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

package org.wso2.carbon.identity.developer.lsp.debug.runtime.builders;

import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Argument;

import java.util.Map;

/**
 * Interface for the builder.
 */
public interface VariableBuilder {

    /**
     * This method will help to build the variables by taking all the set of arguments intercepted from Method context .
     * This will help to select what are the arguments we need to take from the Method context arguments.
     *
     * @param arguments          set of arguments intercepted.
     * @param variablesReference Reference to the Variable container.
     * @return map with the name of the object as key  and the object.
     */
    Argument<Map<String, Object>> build(Object[] arguments, int variablesReference);
}
