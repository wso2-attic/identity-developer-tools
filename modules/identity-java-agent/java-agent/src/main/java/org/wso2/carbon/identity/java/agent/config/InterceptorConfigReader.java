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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * Reads the interceptor config from the resources file in the classpath.
 */
public class InterceptorConfigReader {
    private static final Log log = LogFactory.getLog(InterceptorConfigReader.class);
    private Properties notificationClassConfigProperties;

    public InterceptorConfigReader() throws Exception {
        try {
            notificationClassConfigProperties = loadProperties();
        } catch (Exception e) {
            throw new Exception("Failed to initialize Test.", e);
        }
    }

    private Properties loadProperties()throws Exception{
        Properties properties = new Properties();
        InputStream inStream = null;
        File InterceptorPropertyFile = new File("instrumentation-config.properties");

        try {
            if (InterceptorPropertyFile.exists()) {
                inStream = new FileInputStream(InterceptorPropertyFile);
            }
            if (inStream != null) {
                properties.load(inStream);
            }
        } catch (FileNotFoundException e) {
            log.warn("Could not find configuration file for interceptor", e);
        } catch (IOException e) {
            log.warn("Error while opening input stream for property file", e);
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                log.error("Error while closing input stream ", e);
            }
        }
        return properties;
    }

    private List<InterceptorConfig> build(ArrayList<InterceptorConfig> result) {

        String prefix="class.name";
        Properties moduleNames = getSubProperties(prefix,notificationClassConfigProperties);

        int i=moduleNames.size();
        while (0<i) {
            Enumeration propertyNames = moduleNames.propertyNames();
            String key = (String) propertyNames.nextElement();
            String moduleName = (String) moduleNames.remove(key);
            InterceptorConfig interceptorConfig=buildInterceptorConfigurations(moduleName);
            result.add(interceptorConfig);
            i--;
        }
        return result;
    }

    public static Properties getSubProperties(String prefix, Properties properties) {

        if (StringUtils.isEmpty(prefix) || properties == null) {
            throw new IllegalArgumentException("Prefix and Properties should not be null to get sub properties");
        }
        int i = 1;
        Properties subProperties = new Properties();
        while (properties.getProperty(prefix + "." + i) != null) {
            subProperties.put(prefix + "." + i, properties.remove(prefix + "." + i++));
        }
        return subProperties;
    }

    private  InterceptorConfig buildInterceptorConfigurations(String moduleName) {

        List<String> moduleProperties = getInterceptorProperties(moduleName);
        String className = moduleName;
        String method = moduleProperties.get(0);
        String signature = moduleProperties.get(1);

        InterceptorConfig interceptorConfig = new InterceptorConfig();
        interceptorConfig.setClassName(className);
        interceptorConfig.addMethodSignature(method, signature);

        return interceptorConfig;
    }

    private List<String> getInterceptorProperties(String moduleName) {

        return getPropertiesWithPrefix(moduleName,notificationClassConfigProperties);
    }

    public static List<String> getPropertiesWithPrefix(String prefix, Properties properties) {

        if (StringUtils.isEmpty(prefix) || properties == null) {
            throw new IllegalArgumentException("Prefix and properties should not be null to extract properties with " +
                    "certain prefix");
        }
        List<String> list=new ArrayList<>();
        String methodNmae = null;
        String signature=null;

        Properties subProperties = new Properties();
        Enumeration propertyNames = properties.propertyNames();

        while (propertyNames.hasMoreElements()) {
            String key = (String) propertyNames.nextElement();
            if (key.startsWith(prefix)) {
                if(key.startsWith(prefix+"."+"method")) {
                    subProperties.setProperty(key, (String) properties.remove(key));
                    methodNmae = subProperties.getProperty(key);
                }
                if(key.startsWith(prefix+"."+"signature")) {
                    subProperties.setProperty(key, (String) properties.remove(key));
                    signature = subProperties.getProperty(key);
                }
            }
        }
        list.add(methodNmae);
        list.add(signature);

        return list;
    }
    /**
     * Reads the configs in the class resource
     * "instrumentation-config.properties".
     *
     * @return
     */
    public List<InterceptorConfig> readConfig() throws Exception {
        ArrayList<InterceptorConfig> result = new ArrayList<>();

        InterceptorConfigReader interceptorConfigReader=new InterceptorConfigReader();
        interceptorConfigReader.build(result);

        return result;
    }
}
