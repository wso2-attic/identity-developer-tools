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
 * This class contain all the Instrumentation context details for a Method.
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

    /**
     * This method is to get the Instrumented Thread.
     *
     * @return The instrumented thread.
     */
    public Thread getInstrumentedThread() {

        return instrumentedThread;
    }

    /**
     * This method is to get the Instrumented Object.
     *
     * @return The instrumented object.
     */
    public Object getInstrumentedObject() {

        return instrumentedObject;
    }

    /**
     * This method is to get the Method Stack.
     *
     * @return The instrumented method stack.
     */
    public Object getMethodStack() {

        return methodStack;
    }

    /**
     * This method is to get the Method Name.
     *
     * @return The instrumented method name.
     */
    public String getMethodName() {

        return methodName;
    }

    /**
     * This method is to get the Method Signature.
     *
     * @return The instrumented method signature.
     */
    public String getMethodSignature() {

        return methodSignature;
    }

    /**
     * This method is to get the Argument Values.
     *
     * @return The instrumented method  argument values.
     */
    public Object[] getArgumentValues() {

        return argumentValues;
    }

    /**
     * This method is to set the Argument Values.
     *
     * @param argumentValues The arguments (values) of the method.
     */
    public void setArgumentValues(Object[] argumentValues) {

        this.argumentValues = argumentValues;
    }

    /**
     * This method is to get the Argument Types.
     *
     * @return Data types of all the arguments for the method.
     */
    public Class[] getArgumentTypes() {

        return argumentTypes;
    }

    /**
     * This method is to set the Argument Types.
     *
     * @param argumentTypes Data types of all the arguments for the method.
     */
    public void setArgumentTypes(Class[] argumentTypes) {

        this.argumentTypes = argumentTypes;
    }

    /**
     * This method is to add the data frame to dataStack.
     *
     * @param dataFrame Pushed data frame.
     */
    public void pushData(Map<String, Object> dataFrame) {

        dataStack.addFirst(dataFrame);
    }

    /**
     * This method is to remove the first data frame  to dataStack.
     *
     * @return Lastly added element in the stack.
     */
    public Map<String, Object> popData() {

        if (dataStack.isEmpty()) {
            return null;
        }
        return dataStack.removeFirst();
    }

    /**
     * This method is to set Class Name.
     *
     * @param className The instrumented class name.
     */
    public void setClassName(String className) {

        this.className = className;
    }

    /**
     * This method is to get Class Name.
     *
     * @return The instrumented class name.
     */
    public String getClassName() {

        return className;
    }
}
