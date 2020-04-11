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

/**
 * Interface to help build the SAMLEntryVariable.
 */
public interface SAMLExitVariablePlan {

    /**
     *This method is to add httpServletResponse to variables list  After the argument is proceeded.
     *
     * @param httpServletResponse the HttpServletResponse object, known as "response" in a JSP page.
     */
    void setHttpServletResponse(Object httpServletResponse);

    /**
     * This method is to add samlResponse to variables list  After the argument is proceeded.
     *
     * @param samlResponse the SAML Response string.
     */
    void setSAMLResponse(Object samlResponse);
}
