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

package org.wso2.carbon.identity.developer.lsp.debug.dap.messages;

/**
 * Represents an argument of a Debug protocol message.
 *
 * @param <T>
 */
public class Argument<T extends Object> {

    private Object value;

    public Argument(Object value) {

        this.value = value;
    }

    /**
     * This method is to get the arguments.
     *
     * @return argument value
     */
    public T getValue() {

        return (T) value;
    }

    /**
     * This method is to set the arguments.
     *
     * @param value the argument
     */
    public void setValue(T value) {

        this.value = value;
    }
}
