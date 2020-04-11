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
public interface SAMLEntryVariablePlan {

     /**
      * This method is add httpServletRequest to variables list  After the argument is proceeded.
      *
      * @param httpServletRequest the HttpServletRequest object, known as "request" in a JSP page.
      */
     void setHttpServletRequest (Object httpServletRequest);

     /**
      * This method is add samlRequest to variables list  After the argument is proceeded.
      *
      * @param samlRequest the SAML Request string.
      */
     void setSAMLRequest (Object samlRequest);
}
