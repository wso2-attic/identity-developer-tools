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

package org.wso2.carbon.identity.java.agent.internal;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.runtime.Desc;
import javassist.scopedpool.ScopedClassPoolFactoryImpl;
import javassist.scopedpool.ScopedClassPoolRepositoryImpl;
import org.wso2.carbon.identity.java.agent.config.InterceptorConfig;
import org.wso2.carbon.identity.java.agent.config.MethodInfoConfig;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class transformer which intercepts the method call, used to emit the debug information.
 */
public class InterceptingClassTransformer implements ClassFileTransformer {

    /**
     * We use JUL as this is an java agent which should not depend on any other framework than java.
     */
    private static final Logger log = Logger.getLogger(InterceptingClassTransformer.class.getName());
    public static final String METHOD_LISTENER_TEMPLATE = "org.wso2.carbon.identity.java.agent.internal." +
            "MethodEntryListener.methodEntered(\"%s\", \"%s\",\"%s\", $sig, $args );";

    private final Map<String, InterceptorConfig> interceptorMap = new HashMap<>();
    private ScopedClassPoolFactoryImpl scopedClassPoolFactory = new ScopedClassPoolFactoryImpl();

    /* MethodEntryListener has to be created before it's static method being injected to the transformed class. Hence
     we instantiate the Method Listener class at this point. So that it will be available for the subsequence class
     interception. */
    private MethodEntryListener methodEntryListener = new MethodEntryListener();
    private ClassPool rootPool;

    public void init() {

        //Sets the useContextClassLoader =true to get any class type to be correctly resolved with correct OSGI module
        Desc.useContextClassLoader = true;
        rootPool = ClassPool.getDefault();
    }

    /**
     * An agent provides an implementation of this interface method in order to transform class files.
     * Transforms the given class file and returns a new replacement class file.
     * We check our config with classes and intercept only when the Corresponding Class Name, Method Name, Method
     * Signature matches.
     *
     * @param loader              The defining loader of the class to be transformed, may be {@code null} if the bootstrap loader.
     * @param className           The name of the class in the internal form of fully qualified class.
     * @param classBeingRedefined If this is triggered by a redefine or re transform, the class being redefined.
     * @param protectionDomain    The protection domain of the class being defined or redefined.
     * @param classfileBuffer     The input byte buffer in class file format - Have to be instrumented.
     * @return
     * @throws IllegalClassFormatException
     * @implSpec The default implementation returns null.
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {

        byte[] byteCode = classfileBuffer;

        if (shouldIntercept(className)) {
            log.info("Transforming the class " + className);

            try {
                ClassPool classPool = scopedClassPoolFactory.create(loader, rootPool,
                        ScopedClassPoolRepositoryImpl.getInstance());
                CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                CtMethod[] methods = ctClass.getDeclaredMethods();
                InterceptorConfig config = getInterceptorConfig(className);
                List<MethodInfoConfig> methodInfoConfigs = config.getMethodInfoConfigs();
                int transformedMethodCounts = 0;

                for (CtMethod method : methods) {
                    for (MethodInfoConfig methodInfoConfig : methodInfoConfigs) {
                        if (methodInfoConfig.verifyMethod(method.getName(), method.getSignature())) {
                            if (methodInfoConfig.isInsertBefore()) {
                                method.insertBefore(String.format(METHOD_LISTENER_TEMPLATE, className, method.getName(),
                                        method.getSignature()));
                            }
                            if (methodInfoConfig.isInsertAfter()) {
                                method.insertAfter(String.format(METHOD_LISTENER_TEMPLATE, className, method.getName(),
                                        method.getSignature()));
                            }
                            transformedMethodCounts++;
                        }
                    }
                }

                if (transformedMethodCounts == methodInfoConfigs.size()) {
                    byteCode = ctClass.toBytecode();
                }

                ctClass.detach();
            } catch (Throwable ex) {
                log.log(Level.SEVERE, "Error in transforming the class: " + className, ex);
            }
        }
        return byteCode;
    }

    /**
     * This Method is to Check whether the Agent should intercept or not.
     *
     * @param className The name of the class in the internal form of fully qualified class.
     * @return Whether to intercept or not.
     */
    private boolean shouldIntercept(String className) {

        return interceptorMap.containsKey(className);
    }

    /**
     * This method is to get the InterceptorConfig using Class name.
     *
     * @param className The name of the class in the internal form of fully qualified class.
     * @return The Interceptor config corresponding to the class name.
     */
    private InterceptorConfig getInterceptorConfig(String className) {

        return interceptorMap.get(className);
    }

    /**
     * This method is to add the InterceptorConfig.
     *
     * @param interceptorConfig The interceptor config corresponding to the class name.
     */
    public void addConfig(InterceptorConfig interceptorConfig) {

        interceptorMap.put(interceptorConfig.getClassName(), interceptorConfig);
    }

}
