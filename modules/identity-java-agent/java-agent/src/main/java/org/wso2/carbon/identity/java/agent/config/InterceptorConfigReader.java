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



import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Reads the interceptor config from the resources file in the classpath.
 */
public class InterceptorConfigReader {
    public static final String filename = "/home/piyumi/identity-developer-tools/modules/identity-java-agent/java-agent/src/main/resources/instrumentation-config.properties";
    private String className;
    private String methodName;
    private String signature;

    /**
     * Reads the configs in the class resource
     * "instrumentation-config.properties".
     *
     * @return
     */
    public List<InterceptorConfig> readConfig(){


            Properties properties = new Properties();
            try {
                FileInputStream fileReader = new FileInputStream(filename);
                properties.load(fileReader);

                this.className = properties.getProperty("class.1.className");
                this.methodName= properties.getProperty("class.1.methodName");
                this.signature=properties.getProperty("class.1.signature");

            }catch (IOException e){
                e.printStackTrace();

            }


        ArrayList<InterceptorConfig> result = new ArrayList<>();
            ClassLoader classLoader = getClass().getClassLoader();
            //TODO: Read the instrumentation-config.properties

            InterceptorConfig interceptorConfig = new InterceptorConfig();
            interceptorConfig.setClassName(className);
            interceptorConfig.addMethodSignature(methodName,signature);

            result.add(interceptorConfig);
            return result;

    }

}
