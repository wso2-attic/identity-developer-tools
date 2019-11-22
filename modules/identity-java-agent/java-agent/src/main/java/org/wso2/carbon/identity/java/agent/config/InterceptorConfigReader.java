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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the interceptor config from the resources file in the classpath.
 */
public class InterceptorConfigReader {
    private static final String filename = "/home/piyumi/Music/identity-developer-tools/modules/identity-java-agent/java-agent/src/main/resources/instrumentation-config.properties";


    /**
     * Reads the configs in the class resource
     * "instrumentation-config.properties".
     *
     * @return
     */
    public List<InterceptorConfig> readConfig() {

        List<String> classNumbers=new ArrayList<>();
        List<String> classProperties=new ArrayList<>();
        try {

            FileInputStream fileReader = new FileInputStream(filename);
            BufferedReader readBuffered = new BufferedReader(new InputStreamReader(fileReader));

            String line = null;
            while ((line = readBuffered.readLine()) != null) {

                if (line.trim().length() == 0 || line.startsWith("#") || line.startsWith(" "))
                    continue;

                if (line.startsWith("class.")) {
                    String delimiters = "\\.+|=\\s*";
                    String[] tokensVal = line.split(delimiters);
                    for (int i = 0; i < tokensVal.length; i++) {
                        if (i == 1) {
                            String classNumber = tokensVal[i];
                            classNumbers.add(classNumber);
                        }
                        if (i == 3) {
                            String classProperty = tokensVal[i];
                            classProperties.add(classProperty);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ArrayList<InterceptorConfig> result = new ArrayList<>();

        for(int j=0;j< classProperties.size()-2;j+=3){
            InterceptorConfig interceptorConfig = new InterceptorConfig();
            interceptorConfig.setClassName(classProperties.get(j));
            interceptorConfig.addMethodSignature(classProperties.get(j + 1),classProperties.get(j + 2));
            result.add(interceptorConfig);
        }

        return result;

    }
}
