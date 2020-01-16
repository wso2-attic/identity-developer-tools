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

package org.wso2.carbon.identity.java.agent.host;

import java.util.LinkedList;
import java.util.Map;

/**
 * Instrumentation comntext for a method.
 */
public class MethodContext {

    private Thread instrumentedThread;
    private Object instrumentedObject;
    private Object methodStack;
    private Object[] argumentValues;
    private Class[] argumentTypes;
    private LinkedList<Map<String, Object>> dataStack = new LinkedList<>();
    private String className;
    private String methodName;
    private String methodSignature;

    public MethodContext(Thread instrumentedThread, String methodName, String methodSignature) {

        this.instrumentedThread = instrumentedThread;
        this.methodName = methodName;
        this.methodSignature = methodSignature;
    }

    public Thread getInstrumentedThread() {

        return instrumentedThread;
    }

    public Object getInstrumentedObject() {

        return instrumentedObject;
    }

    public Object getMethodStack() {

        return methodStack;
    }

    public String getMethodName() {

        return methodName;
    }

    public String getMethodSignature() {

        return methodSignature;
    }

    public Object[] getArgumentValues() {

        return argumentValues;
    }

    public void setArgumentValues(Object[] argumentValues) {

        this.argumentValues = argumentValues;
    }

    public Class[] getArgumentTypes() {

        return argumentTypes;
    }

    public void setArgumentTypes(Class[] argumentTypes) {

        this.argumentTypes = argumentTypes;
    }

    public void pushData(Map<String, Object> dataFrame) {

        dataStack.addFirst(dataFrame);
    }

    public Map<String, Object> popData() {

        if (dataStack.isEmpty()) {
            return null;
        }
        return dataStack.removeFirst();
    }

    public void setClassName(String className) {

        this.className = className;
    }

    public String getClassName() {

        return className;
    }
}
