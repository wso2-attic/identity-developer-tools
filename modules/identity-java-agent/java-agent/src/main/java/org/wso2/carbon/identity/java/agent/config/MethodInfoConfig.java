/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.identity.java.agent.config;

/**
 * Configuration for method and method signature.
 */
public class MethodInfoConfig {

    private String methodName;
    private String signature;
    private boolean insertBefore;
    private boolean insertAfter;

    public MethodInfoConfig(String methodName, String signature, boolean insertBefore, boolean insertAfter) {

        this.methodName = methodName;
        this.signature = signature;
        this.insertBefore = insertBefore;
        this.insertAfter = insertAfter;
    }

    /**
     * This method is to when intercepting check whether to insert after.
     *
     * @return Whether to intercept at end of the method body.
     */
    public boolean isInsertAfter() {

        return insertAfter;
    }

    /**
     * This method is to when intercepting check whether to insert before.
     *
     * @return Whether to intercept at start of the method body.
     */
    public boolean isInsertBefore() {

        return insertBefore;
    }

    /**
     * This method is to get the Method Name from the Config.
     *
     * @return The method name of the Config.
     */
    public String getMethodName() {

        return methodName;
    }

    /**
     * This method is to get the Method Signature from the Config.
     *
     * @return The method signature of the Config.
     */
    public String getSignature() {

        return signature;
    }

    /**
     * This method is to Verify the Method with the Class loaded and the Method in the config.
     *
     * @param methodName The method name of the Config.
     * @param signature  The method signature of the Config.
     * @return Whether to verify or not.
     */
    public boolean verifyMethod(String methodName, String signature) {

        return (this.methodName.equals(methodName) && this.signature.equals(signature));
    }
}
